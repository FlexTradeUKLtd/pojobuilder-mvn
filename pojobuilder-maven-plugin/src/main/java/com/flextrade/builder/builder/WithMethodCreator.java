package com.flextrade.builder.builder;

import static com.sun.codemodel.JExpr.ref;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import com.flextrade.builder.model.ClassModel;
import com.flextrade.builder.model.CodeModelProperty;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;

class WithMethodCreator {
    private final ClassModel classModel;
    private final JFieldVar matcherField;

    public WithMethodCreator(ClassModel classModel, JFieldVar matcherField) {
        this.classModel = classModel;
        this.matcherField = matcherField;
    }

    void createWithMethods(CodeModelProperty property) {
        createWithValueMethod(property);
        createWithMatcherMethod(property);
    }

    private void createWithMatcherMethod(CodeModelProperty property) {
        createWithMethod(property,
                classModel.getCodeModel().ref(Matcher.class).narrow(property.getJType()),
                ref(property.getFieldNameCamelCase())
        );
    }

    private void createWithValueMethod(CodeModelProperty property) {
        createWithMethod(property,
                property.getJType(),
                matchers_is(property.getFieldNameCamelCase(), classModel));
    }

    private void createWithMethod(CodeModelProperty property,  JType paramType, JExpression expression) {
        JMethod withMethod = definedClass().method(JMod.PUBLIC, definedClass(), "with" + property.getFieldName());
        withMethod.param(paramType, property.getFieldNameCamelCase());
        withMethod.body().assign(matcherField,
                matchers_allOf(
                        matcherField,
                        matchers_hasProperty(
                                property.getFieldNameCamelCase(),
                                expression,
                                classModel),
                        classModel
                )
        );

        withMethod.body()._return(JExpr._this());
    }

    private JDefinedClass definedClass() {
        return classModel.getDefinedClass();
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
