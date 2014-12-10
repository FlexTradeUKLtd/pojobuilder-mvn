package flextrade.buildtool.model;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JType;
import com.sun.codemodel.JTypeVar;

public class ClassModel {

    private Class<?> clazz;
    private final Map<String, JTypeVar> typeVars = new HashMap<>();
    private PropertyTranslator propertyTranslator;
    private JCodeModel codeModel;
    private String nameTail;
    private JDefinedClass definedClass;
    private Stream<CodeModelProperty> properties;

    public ClassModel withClazz(Class<?> clazz) {
        this.clazz = clazz;
        return this;
    }

    public ClassModel withCodeModel(JCodeModel codeModel) {
        this.codeModel = codeModel;
        return this;
    }

    public ClassModel withNameTail(String nameTail) {
        this.nameTail = nameTail;
        return this;
    }


    public JDefinedClass getDefinedClass() {
        if(definedClass == null) {
            init();
        }

        return definedClass;
    }

    public Stream<CodeModelProperty> getProperties() {
        if(definedClass == null) {
            init();
        }

        return properties;
    }

    private void init() {
        try {
            propertyTranslator = new PropertyTranslator(codeModel, typeVars);
            definedClass = codeModel._class(clazz.getName() + nameTail);
            for (TypeVariable typeParam : clazz.getTypeParameters()) {
                String name = typeParam.getName();

                JTypeVar typeVar;
                Type[] bounds = typeParam.getBounds();
                if (bounds.length > 0) {
                    JType bound = propertyTranslator.getJType(bounds[0]);
                    typeVar = definedClass.generify(name, (JClass) bound);
                } else {
                    typeVar = definedClass.generify(name);
                }
                typeVars.put(name, typeVar);
            }

            properties = new FieldFinder(clazz).getFields().map(p -> new CodeModelProperty(p, propertyTranslator));
        } catch (JClassAlreadyExistsException e) {
            throw new RuntimeException(e);
        }
    }

    public JClass getPojoClass() {
        JClass pojoClass = codeModel.ref(clazz);
        for(TypeVariable typeParam : clazz.getTypeParameters()) {
            pojoClass = pojoClass.narrow(typeVars.get(typeParam.getName()));
        }

        return pojoClass;
    }
}
