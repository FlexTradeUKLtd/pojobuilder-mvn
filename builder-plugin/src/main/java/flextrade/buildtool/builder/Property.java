package flextrade.buildtool.builder;

import java.lang.reflect.Method;

import com.sun.codemodel.JFieldVar;


public class Property {

    private final JFieldVar fieldVar;
    private final Method setter;

    public Property(JFieldVar fieldVar, Method setter) {
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
