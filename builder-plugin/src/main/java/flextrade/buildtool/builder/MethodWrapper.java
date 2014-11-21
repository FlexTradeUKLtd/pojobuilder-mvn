package flextrade.buildtool.builder;

import java.lang.reflect.Method;

public class MethodWrapper {

    public final Method method;

    public MethodWrapper(Method method) {
        this.method = method;
    }

    public boolean isGetter() {
        return method.getName().startsWith("get") && !method.getReturnType().equals(Void.TYPE) && (method.getParameterCount() == 0);
    }

    public boolean isSetter() {
        return method.getName().startsWith("set") && (method.getParameterCount() == 1);
    }

    public String getFieldName() {
        return method.getName().substring(3);
    }
}
