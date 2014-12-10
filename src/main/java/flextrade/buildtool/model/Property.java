package flextrade.buildtool.model;

import java.lang.reflect.Type;

public class Property {
    public final MethodWrapper getter;
    public final MethodWrapper setter;

    public Property(MethodWrapper getter, MethodWrapper setter) {
        this.getter = getter;
        this.setter = setter;
        assert setter.getFieldName().equals(getter.getFieldName());
    }

    public String getFieldName() {
        return setter.getFieldName();
    }

    public Type getType() {
        return getter.method.getGenericReturnType();
    }
}
