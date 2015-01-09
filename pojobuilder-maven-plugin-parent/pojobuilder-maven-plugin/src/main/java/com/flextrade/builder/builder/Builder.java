package com.flextrade.builder.builder;

import com.sun.codemodel.JCodeModel;

public interface Builder {
    public JCodeModel fromClass(Class<?> clazz);
}
