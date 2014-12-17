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

    private final Class<?> clazz;
    private final Map<String, JTypeVar> typeVars = new HashMap<>();
    private final PropertyTranslator propertyTranslator;
    private final JCodeModel codeModel = new JCodeModel();
    private final String nameTail;
    private final JDefinedClass definedClass;
    private final Stream<CodeModelProperty> properties;

    public ClassModel(Class<?> clazz, String nameTail) {
        this.clazz = clazz;
        this.nameTail = nameTail;
        propertyTranslator = new PropertyTranslator(codeModel, typeVars);

        try {
            definedClass = codeModel._class(clazz.getName() + nameTail);
        } catch (JClassAlreadyExistsException e) {
            throw new RuntimeException(e);
        }

        populateTypeVars(clazz);
        properties = new FieldFinder(clazz).getFields().map(p -> new CodeModelProperty(p, propertyTranslator));
    }

    private void populateTypeVars(Class<?> clazz) {
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
    }


    public JDefinedClass getDefinedClass() {
        return definedClass;
    }

    public Stream<CodeModelProperty> getProperties() {
        return properties;
    }

    private void init() {

    }

    public JCodeModel getCodeModel() {
        return codeModel;
    }

    public JClass getPojoClass() {
        JClass pojoClass = codeModel.ref(clazz);
        for(TypeVariable typeParam : clazz.getTypeParameters()) {
            pojoClass = pojoClass.narrow(typeVars.get(typeParam.getName()));
        }

        return pojoClass;
    }
}
