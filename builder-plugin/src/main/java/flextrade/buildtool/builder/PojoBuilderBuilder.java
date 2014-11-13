package flextrade.buildtool.builder;

import static com.google.inject.internal.util.Sets.newHashSet;
import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr.ref;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;


import com.dyuproject.protostuff.Message;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

import flextrade.buildtool.PojoBuilderMojo;

public class PojoBuilderBuilder<T extends Message> {


    private final JCodeModel codeModel = new JCodeModel();
    private final Class<T> clazz;
    private final JDefinedClass definedClass;

    private final Set<Property> properties = newHashSet();

    public PojoBuilderBuilder(Class<T> clazz) throws JClassAlreadyExistsException {

        this.clazz = clazz;
        definedClass = codeModel._class(clazz.getName() + "Builder");
    }


    public void build() throws IOException {

        final String fieldName = "Property";

        Arrays.asList(clazz.getMethods()).stream().forEach(new CreateField());


        createBuildMethod();

        File file = new File(PojoBuilderMojo.TARGET_BUILDERS_SOURCES);
        file.mkdirs();
        codeModel.build(file);

    }

    private void createBuildMethod() {
        JClass pojoClass = codeModel.ref(clazz);
        JMethod builderMethod = definedClass.method(JMod.PUBLIC, pojoClass, "build");
        JBlock methodBody = builderMethod.body();

        JVar message = methodBody.decl(pojoClass, "result", _new(pojoClass));
        for(Property property : properties) {
            methodBody.invoke(message, property.getSetter().getName()).arg(property.getFieldVar());
        }

        methodBody._return(message);
    }

    private String convertFirstCharToLowercase(String string) {
        return string.isEmpty() ? string : string.substring(0, 1).toLowerCase() + string.substring(1);
    }

    private class CreateField implements Consumer<Method> {
        @Override
        public void accept(Method method) {
            if(method.getName().startsWith("set")) {
                String fieldName = method.getName().substring(3);
                String fieldNameCamelCase = convertFirstCharToLowercase(fieldName);

                Class[] params = method.getParameterTypes();

                assert params.length == 1 : "Cannot create builder if setter has multiple params";

                Class fieldType = params[0];

                JFieldVar field = definedClass.field(JMod.PRIVATE, codeModel.ref(fieldType), fieldNameCamelCase);

                createWithMethod(fieldName, fieldNameCamelCase, fieldType, field);

                properties.add(new Property(field, method));
            }
        }

        private void createWithMethod(String fieldName, String fieldNameCamelCase, Class fieldType, JFieldVar field) {
            JMethod withMethod = definedClass.method(JMod.PUBLIC, definedClass, "with" + fieldName);
            withMethod.param(fieldType, fieldNameCamelCase);
            withMethod.body().assign(JExpr._this().ref(field), ref(fieldNameCamelCase));
            withMethod.body()._return(JExpr._this());
        }
    }
}
