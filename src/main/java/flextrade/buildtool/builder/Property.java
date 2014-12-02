package flextrade.buildtool.builder;

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

    public String getFieldNameCamelCase() {
        return convertFirstCharToLowercase(getFieldName());
    }

    private String convertFirstCharToLowercase(String string) {
        return string.isEmpty() ? string : string.substring(0, 1).toLowerCase() + string.substring(1);
    }

    public Type getType() {
        return getter.method.getGenericReturnType();
    }
}
