package com.flextrade.builder.builder;

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr.ref;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

import com.flextrade.builder.model.ClassModel;
import com.flextrade.builder.model.CodeModelProperty;

public class BuilderGenerator implements Builder {

    @Override
    public JCodeModel fromClass(Class<?> clazz) {
        final ClassModel classModel = new ClassModel(clazz, "Builder");

        Stream<FieldSetter> fieldSetters = classModel.getProperties().map(p -> createFieldFor(p, classModel));

        createBuildMethod(classModel, fieldSetters);

        return classModel.getCodeModel();
    }

    private FieldSetter createFieldFor(CodeModelProperty property, ClassModel classModel) {
        Method setter = property.getSetter();
        String fieldNameCamelCase = property.getFieldNameCamelCase();

        Class[] params = setter.getParameterTypes();
        assert params.length == 1 : "Cannot create builder if setter has multiple params";

        JType fieldType = property.getJType();

        JFieldVar field = classModel.getDefinedClass().field(JMod.PRIVATE, fieldType, fieldNameCamelCase);

        createWithMethod(property, field, classModel);

        return new FieldSetter(field, setter);
    }

    private void createWithMethod(CodeModelProperty property, JFieldVar field, ClassModel classModel) {
        JMethod withMethod = classModel.getDefinedClass().method(JMod.PUBLIC, classModel.getDefinedClass(), "with" + property.getFieldName());
        withMethod.param(property.getJType(), property.getFieldNameCamelCase());
        withMethod.body().assign(JExpr._this().ref(field), ref(property.getFieldNameCamelCase()));
        withMethod.body()._return(JExpr._this());
    }

    private void createBuildMethod(final ClassModel classModel, Stream<FieldSetter> fieldSetters) {
        JClass pojoClass = classModel.getPojoClass();

        JMethod builderMethod = classModel.getDefinedClass().method(JMod.PUBLIC, pojoClass, "build");
        JBlock methodBody = builderMethod.body();

        JVar builtPojo = methodBody.decl(pojoClass, "result", _new(pojoClass));
        fieldSetters.forEach(property ->
                        invokeSetterOnPojo(methodBody, builtPojo, property)
        );

        methodBody._return(builtPojo);
    }

    private JInvocation invokeSetterOnPojo(JBlock methodBody, JVar builtPojo, FieldSetter property) {
        return methodBody.invoke(builtPojo, property.getSetter().getName()).arg(property.getFieldVar());
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
