package flextrade.buildtool.builder;

import static com.sun.codemodel.JExpr.ref;

import java.io.File;
import java.io.IOException;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

import flextrade.buildtool.model.ClassModel;
import flextrade.buildtool.model.CodeModelProperty;

public class PojoMatcherBuilder {

    private final JCodeModel codeModel = new JCodeModel();
    private final String outputDir;
    private final JFieldVar matcherField;

    public PojoMatcherBuilder(Class<?> clazz, String outputDir) throws JClassAlreadyExistsException, IOException {
        this.outputDir = outputDir;

        final ClassModel classModel = new ClassModel().withClazz(clazz).withCodeModel(codeModel).withNameModifier("Matcher");

        JDefinedClass definedClass = classModel.getDefinedClass();
        definedClass._extends(codeModel.ref(BaseMatcher.class).narrow(classModel.getPojoClass()));

        matcherField = definedClass.field(JMod.PRIVATE, codeModel.ref(Matcher.class).narrow(clazz), "matcher", matchers_instanceOf(classModel.getPojoClass()));

        classModel.getProperties().forEach(prop -> createWithMethod(classModel, prop));

        createMatchesMethod(classModel);
        createDescribeToMethod(classModel);

        buildFile();
    }

    private void createWithMethod(ClassModel classModel, CodeModelProperty property) {
        JDefinedClass definedClass = classModel.getDefinedClass();
        JMethod withMethod = definedClass.method(JMod.PUBLIC, definedClass, "with" + property.getFieldName());
        withMethod.param(property.getJType(), property.getFieldNameCamelCase());
        withMethod.body().assign(matcherField,
                matchers_allOf(
                        matcherField,
                        matchers_hasProperty(
                                property.getFieldNameCamelCase(),
                                matchers_is(property.getFieldNameCamelCase())
                        )
                )

        );
        withMethod.body()._return(JExpr._this());
    }

    private void createDescribeToMethod(ClassModel classModel) {
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

    private JInvocation matchers_instanceOf(JClass jClass) {
        return codeModel.ref(Matchers.class).staticInvoke("instanceOf").arg(jClass.dotclass());
    }

    private void buildFile() throws IOException {
        File file = new File(outputDir);
        file.mkdirs();
        codeModel.build(file);
    }

    private JInvocation matchers_is(String expected) {
        return codeModel.ref(Matchers.class).staticInvoke("is").arg(ref(expected));
    }

    private JInvocation matchers_allOf(JExpression matcher1, JExpression matcher2) {
        return codeModel.ref(Matchers.class).staticInvoke("allOf").arg(matcher1).arg(matcher2);
    }

    private JInvocation matchers_hasProperty(String propertyName, JExpression matcher) {
        return codeModel.ref(Matchers.class).staticInvoke("hasProperty").arg(JExpr.lit(propertyName)).arg(matcher);
    }
}
