package flextrade.buildtool.builder;

import java.lang.reflect.Method;

public class MethodWrapper {

    public final Method method;

    public MethodWrapper(Method method) {
        this.method = method;
    }

    public boolean isGetter() {
        return method.getName().startsWith("get") && !methodReturnsVoid() && methodHasNoArgs();
    }

    private boolean methodHasNoArgs() {
        return method.getParameterCount() == 0;
    }

    private boolean methodReturnsVoid() {
        return method.getReturnType().equals(Void.TYPE);
    }

    public boolean isSetter() {
        return method.getName().startsWith("set") && hasOneArg();
    }

    private boolean hasOneArg() {
        return method.getParameterCount() == 1;
    }

    public String getFieldName() {
        return method.getName().substring(3);
    }
}
