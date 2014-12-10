package flextrade.buildtool.builder;

import static com.sun.codemodel.JExpr.ref;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTypeVar;
import com.sun.codemodel.JVar;

import flextrade.buildtool.model.ClassModel;
import flextrade.buildtool.model.CodeModelProperty;
import flextrade.buildtool.model.FieldFinder;
import flextrade.buildtool.model.Property;
import flextrade.buildtool.model.PropertyTranslator;

public class PojoMatcherBuilder {

    private final JCodeModel codeModel = new JCodeModel();
    private final Class<?> clazz;
    private final String outputDir;
    private final JDefinedClass definedClass;
    private final JFieldVar matcherField;
    private final Map<String, JTypeVar> typeVars = new HashMap<>();
    private final PropertyTranslator propertyTranslater = new PropertyTranslator(codeModel, typeVars);

    public PojoMatcherBuilder(Class<?> clazz, String outputDir) throws JClassAlreadyExistsException, IOException {
        this.clazz = clazz;
        this.outputDir = outputDir;

        ClassModel classModel = new ClassModel().withClazz(clazz).withCodeModel(codeModel).withNameTail("Matcher");


        definedClass = codeModel._class(clazz.getName() + "Matcher");
        definedClass._extends(codeModel.ref(BaseMatcher.class).narrow(classModel.getPojoClass()));


//        definedClass = codeModel._class(clazz.getName() + "Matcher");
//        definedClass._extends(codeModel.ref(BaseMatcher.class).narrow(clazz));
//        for(TypeVariable typeParam : clazz.getTypeParameters()) {
//            String name = typeParam.getName();
//            JTypeVar typeVar = definedClass.generify(name);
//            typeVars.put(name, typeVar);
//        }

        matcherField = definedClass.field(JMod.PRIVATE, codeModel.ref(Matcher.class).narrow(clazz), "matcher", isInstanceOfClazz());

//        new FieldFinder(clazz).getFields().forEach(new CreateWithMethod());

        classModel.getProperties().forEach(prop -> createWithMethod(prop));

        createMatchesMethod(classModel);
        createDescriptToMethod(classModel);

        buildFile();
    }

    private void createWithMethod(CodeModelProperty property) {
        JMethod withMethod = definedClass.method(JMod.PUBLIC, definedClass, "with" + property.getFieldName());
        withMethod.param(property.getJType(), property.getFieldNameCamelCase());
        withMethod.body().assign(matcherField,
                matchers_allOf(
                        matcherField,
                        matchers_hasProperty(
                                property.getFieldNameCamelCase(),
                                is(property.getFieldNameCamelCase())
                        )
                )

        );
        withMethod.body()._return(JExpr._this());
    }

    private void createDescriptToMethod(ClassModel classModel) {
        JMethod describeToMethod = classModel.getDefinedClass().method(JMod.PUBLIC, codeModel.VOID, "describeTo");
        JVar descriptionVar = describeToMethod.param(Description.class, "description");
        describeToMethod.annotate(Override.class);
        describeToMethod.body().invoke(matcherField, "describeTo").arg(descriptionVar);
    }

    private void createMatchesMethod(ClassModel classModel) {
        JMethod matchesMethod = classModel.getDefinedClass().method(JMod.PUBLIC, codeModel.BOOLEAN, "matches");
        JVar param = matchesMethod.param(Object.class, "o");
        matchesMethod.annotate(Override.class);
        matchesMethod.body()._return(JExpr.invoke(matcherField, "matches").arg(param));
    }

    private JInvocation isInstanceOfClazz() {
        return codeModel.ref(Matchers.class).staticInvoke("instanceOf").arg(codeModel.ref(clazz).dotclass());
    }

    private void buildFile() throws IOException {
        File file = new File(outputDir);
        file.mkdirs();
        codeModel.build(file);
    }

    private class CreateWithMethod implements Consumer<Property> {
        @Override
        public void accept(Property property) {
            JMethod withMethod = definedClass.method(JMod.PUBLIC, definedClass, "with" + property.getFieldName());
            withMethod.param(propertyTranslater.getJType(property.getType()), property.getFieldNameCamelCase());
            withMethod.body().assign(matcherField,
                    matchers_allOf(
                            matcherField,
                            matchers_hasProperty(
                                    property.getFieldNameCamelCase(),
                                    is(property.getFieldNameCamelCase())
                            )
                    )

            );
            withMethod.body()._return(JExpr._this());
        }
    }

    private JInvocation is(String expected) {
        return codeModel.ref(Matchers.class).staticInvoke("is").arg(ref(expected));
    }

    private JInvocation matchers_allOf(JExpression matcher1, JExpression matcher2) {
        return codeModel.ref(Matchers.class).staticInvoke("allOf").arg(matcher1).arg(matcher2);
    }

    private JInvocation matchers_hasProperty(String propertyName, JExpression matcher) {
        return codeModel.ref(Matchers.class).staticInvoke("hasProperty").arg(JExpr.lit(propertyName)).arg(matcher);
    }
}
