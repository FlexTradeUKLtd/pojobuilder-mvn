package flextrade.buildtool.builder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JType;

public class PropertyTranslator {

    private final JCodeModel codeModel;

    public PropertyTranslator(JCodeModel codeModel) {
        this.codeModel = codeModel;
    }

    public JType getJType(Type type) {
        if(type instanceof Class && ((Class)type).isPrimitive())
            return codeModel._ref((Class)type);

        return getJClass(type);
    }

    private JClass getJClass(Type type) {
        if(type instanceof Class)
            return codeModel.ref((Class) type);

        if(type instanceof ParameterizedType) {
            return new ParameterizedJTypeEncoder(type).getJClass();
        }

        throw new IllegalArgumentException("Cannot handle non-class types " + type.getTypeName());
    }

    private class ParameterizedJTypeEncoder {

        JClass raw;

        public ParameterizedJTypeEncoder(Type type) {
            ParameterizedType param = (ParameterizedType) type;

            raw = codeModel.ref((Class) param.getRawType());
            Arrays.stream(param.getActualTypeArguments()).forEach(t -> narrow(t));
        }

        private void narrow(Type type) {
            if(type instanceof WildcardType) {
                if(((WildcardType) type).getUpperBounds().length > 0) {
                    JClass upperBound = PropertyTranslator.this.getJClass(((WildcardType) type).getUpperBounds()[0]);
                    upperBound = upperBound.wildcard();
                    raw = raw.narrow(upperBound);
                } else {
                    raw = raw.narrow(codeModel.wildcard());
                }
            } else {
                raw = raw.narrow(getJType(type));
            }
        }

        public JClass getJClass() {
            return raw;
        }
    }


}
