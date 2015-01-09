package com.flextrade.builder.model;

import java.lang.reflect.Method;

import com.sun.codemodel.JType;

public class CodeModelProperty {

    private final String fieldName;
    private final JType type;
    private final Property property;

    public CodeModelProperty(Property property, PropertyTranslator propertyTranslator) {
        this.property = property;
        this.fieldName = property.getFieldName();
        this.type = propertyTranslator.getJType(property.getType());
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldNameCamelCase() {
        return convertFirstCharToLowercase(getFieldName());
    }

    private String convertFirstCharToLowercase(String string) {
        return string.isEmpty() ? string : string.substring(0, 1).toLowerCase() + string.substring(1);
    }

    public Method getSetter() {
        return property.setter.method;
    }

    public Method getGetter() {
        return property.getter.method;
    }

    public JType getJType() {
        return type;
    }
}
