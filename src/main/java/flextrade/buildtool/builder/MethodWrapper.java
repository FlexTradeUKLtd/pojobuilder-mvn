package flextrade.buildtool.builder;

import java.lang.reflect.Method;

public class MethodWrapper {

    public final Method method;

    public MethodWrapper(Method method) {
        this.method = method;
    }

    public boolean isGetter() {
        return methodHasNoArgs() && (isBooleanGetter() || isNonBooleanGetter());
    }

    private boolean isBooleanGetter() {
        return methodName().startsWith("is") && Boolean.TYPE.equals(getReturnType());
    }

    private Class<?> getReturnType() {
        return method.getReturnType();
    }

    private boolean isNonBooleanGetter() {
        return methodName().startsWith("get") && !methodReturnsVoid();
    }

    private String methodName() {
        return method.getName();
    }

    private boolean methodHasNoArgs() {
        return method.getParameterCount() == 0;
    }

    private boolean methodReturnsVoid() {
        return getReturnType().equals(Void.TYPE);
    }

    public boolean isSetter() {
        return methodName().startsWith("set") && hasOneArg();
    }

    private boolean hasOneArg() {
        return method.getParameterCount() == 1;
    }

    public String getFieldName() {
        if(isBooleanGetter())
            return methodName().substring(2);

        return methodName().substring(3);
    }
}
