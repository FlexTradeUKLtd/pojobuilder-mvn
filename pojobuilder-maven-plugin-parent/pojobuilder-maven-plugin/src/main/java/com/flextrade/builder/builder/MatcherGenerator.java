package com.flextrade.builder.builder;

import static com.sun.codemodel.JExpr.ref;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

import com.flextrade.builder.model.ClassModel;
import com.flextrade.builder.model.CodeModelProperty;

public class MatcherGenerator implements Builder {

    private final JCodeModel codeModel = new JCodeModel();

    @Override
    public JCodeModel fromClass(Class<?> clazz) {
        final ClassModel classModel = new ClassModel(clazz, "Matcher");

        JDefinedClass definedClass = classModel.getDefinedClass();
        definedClass._extends(codeModel.ref(BaseMatcher.class).narrow(classModel.getPojoClass()));

        final JFieldVar matcherField = definedClass.field(JMod.PRIVATE, codeModel.ref(Matcher.class).narrow(clazz), "matcher", matchers_instanceOfPojo(classModel));

        final WithMethodCreator withMethodCreator = new WithMethodCreator(classModel, matcherField);

        classModel.getProperties().forEach(withMethodCreator::createWithMethod);

        createMatchesMethod(classModel, matcherField);
        createDescribeToMethod(classModel, matcherField);

        return classModel.getCodeModel();
    }

    private void createDescribeToMethod(ClassModel classModel, JFieldVar matcherField) {
        JMethod describeToMethod = classModel.getDefinedClass().method(JMod.PUBLIC, classModel.getCodeModel().VOID, "describeTo");
        JVar descriptionVar = describeToMethod.param(Description.class, "description");
        describeToMethod.annotate(Override.class);
        describeToMethod.body().invoke(matcherField, "describeTo").arg(descriptionVar);
    }

    private void createMatchesMethod(ClassModel classModel, JFieldVar matcherField) {
        JMethod matchesMethod = classModel.getDefinedClass().method(JMod.PUBLIC, classModel.getCodeModel().BOOLEAN, "matches");
        JVar param = matchesMethod.param(Object.class, "o");
        matchesMethod.annotate(Override.class);
        matchesMethod.body()._return(JExpr.invoke(matcherField, "matches").arg(param));
    }

    private JInvocation matchers_instanceOfPojo(ClassModel classModel) {
        return classModel.getCodeModel().ref(Matchers.class).staticInvoke("instanceOf").arg(classModel.getPojoClass().dotclass());
    }

    private class WithMethodCreator {
        private final ClassModel classModel;
        private final JFieldVar matcherField;

        public WithMethodCreator(ClassModel classModel, JFieldVar matcherField) {
            this.classModel = classModel;
            this.matcherField = matcherField;
        }

        private void createWithMethod(CodeModelProperty property) {
            JDefinedClass definedClass = classModel.getDefinedClass();
            JMethod withMethod = definedClass.method(JMod.PUBLIC, definedClass, "with" + property.getFieldName());
            withMethod.param(property.getJType(), property.getFieldNameCamelCase());
            withMethod.body().assign(matcherField,
                    matchers_allOf(
                            matcherField,
                            matchers_hasProperty(
                                    property.getFieldNameCamelCase(),
                                    matchers_is(property.getFieldNameCamelCase(), classModel),
                                    classModel
                            ),
                            classModel
                    )

            );
            withMethod.body()._return(JExpr._this());
        }

        private JInvocation matchers_is(String expected, ClassModel classModel) {
            return classModel.getCodeModel().ref(Matchers.class).staticInvoke("is").arg(ref(expected));
        }

        private JInvocation matchers_allOf(JExpression matcher1, JExpression matcher2, ClassModel classModel) {
            return classModel.getCodeModel().ref(Matchers.class).staticInvoke("allOf").arg(matcher1).arg(matcher2);
        }

        private JInvocation matchers_hasProperty(String propertyName, JExpression matcher, ClassModel classModel) {
            return classModel.getCodeModel().ref(Matchers.class).staticInvoke("hasProperty").arg(JExpr.lit(propertyName)).arg(matcher);
        }
    }

}
