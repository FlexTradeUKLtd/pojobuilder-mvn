package flextrade.buildtool.builder;

import static com.google.inject.internal.util.Sets.newHashSet;
import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr.ref;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.function.Consumer;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

class PojoBuilderBuilder {

    private final JCodeModel codeModel = new JCodeModel();
    private final Class<?> clazz;
    private final String outputDir;
    private final JDefinedClass definedClass;

    private final Set<FieldSetter> properties = newHashSet();
    private final PropertyTranslator propertyTranslater = new PropertyTranslator(codeModel);

    public PojoBuilderBuilder(Class<?> clazz, String outputDir) throws JClassAlreadyExistsException, IOException {
        this.clazz = clazz;
        this.outputDir = outputDir;
        definedClass = codeModel._class(clazz.getName() + "Builder");

        new FieldFinder(clazz).getFields().forEach(new CreateField());
        createBuildMethod();
        buildFile();
    }

    private void buildFile() throws IOException {
        File file = new File(outputDir);
        file.mkdirs();
        codeModel.build(file);
    }

    private void createBuildMethod() {
        JClass pojoClass = codeModel.ref(clazz);
        JMethod builderMethod = definedClass.method(JMod.PUBLIC, pojoClass, "build");
        JBlock methodBody = builderMethod.body();

        JVar builtPojo = methodBody.decl(pojoClass, "result", _new(pojoClass));
        for(FieldSetter property : properties) {
            methodBody.invoke(builtPojo, property.getSetter().getName()).arg(property.getFieldVar());
        }

        methodBody._return(builtPojo);
    }

    private class CreateField implements Consumer<Property> {
        @Override
        public void accept(Property property) {

            Method setter = property.setter.method;
            String fieldName = property.getFieldName();
            String fieldNameCamelCase = property.getFieldNameCamelCase();

            Class[] params = setter.getParameterTypes();
            assert params.length == 1 : "Cannot create builder if setter has multiple params";

            JType fieldType = propertyTranslater.getJType(property.getType());

            JFieldVar field = definedClass.field(JMod.PRIVATE, fieldType, fieldNameCamelCase);
            createWithMethod(fieldName, fieldNameCamelCase, fieldType, field);
            properties.add(new FieldSetter(field, setter));
        }


        private void createWithMethod(String fieldName, String fieldNameCamelCase, JType fieldType, JFieldVar field) {
            JMethod withMethod = definedClass.method(JMod.PUBLIC, definedClass, "with" + fieldName);
            withMethod.param(fieldType, fieldNameCamelCase);
            withMethod.body().assign(JExpr._this().ref(field), ref(fieldNameCamelCase));
            withMethod.body()._return(JExpr._this());
        }
    }

    private static class FieldSetter {

        private final JFieldVar fieldVar;
        private final Method setter;

        public FieldSetter(JFieldVar fieldVar, Method setter) {
            this.fieldVar = fieldVar;
            this.setter = setter;
        }

        public Method getSetter() {
            return setter;
        }

        public JFieldVar getFieldVar() {
            return fieldVar;
        }
    }
}
