package flextrade.buildtool.builder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JType;

public class PropertyTranslater {

    private final JCodeModel codeModel;

    public PropertyTranslater(JCodeModel codeModel) {
        this.codeModel = codeModel;
    }

    public JType getJType(Type type) {
        if(type instanceof Class)
            return codeModel._ref((Class)type);

        if(type instanceof ParameterizedType) {
            return new ParamerizedJTypeEncoder(type).getJClass();
        }

        throw new IllegalArgumentException("Cannot handle non-class types");
    }

    private class ParamerizedJTypeEncoder {

        JClass raw;

        public ParamerizedJTypeEncoder(Type type) {
            ParameterizedType param = (ParameterizedType) type;

            raw = codeModel.ref((Class) param.getRawType());
            Arrays.stream(param.getActualTypeArguments()).map(t -> getJType(t)).forEach(t-> raw = raw.narrow(t));
        }

        public JClass getJClass() {
            return raw;
        }
    }
}
