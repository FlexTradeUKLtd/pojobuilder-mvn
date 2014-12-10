package flextrade.buildtool.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class FieldFinder {

    private final HashMap<String, MethodWrapper> getters = new HashMap<>();
    private final HashMap<String, MethodWrapper> setters = new HashMap<>();

    private final Stream<Property> fields;

    public FieldFinder(Class clazz) {
        fields = createFields(clazz);
    }

    private Stream<Property> createFields(Class clazz) {
        Arrays.asList(clazz.getMethods()).stream().map(MethodWrapper::new).forEach(methodWrapper -> findGettersAndSetters(methodWrapper));

        Set<String> fieldNames = new HashSet<>(getters.keySet());
        fieldNames.retainAll(setters.keySet());

        return fieldNames.stream().map(fieldName -> new Property(getters.get(fieldName), setters.get(fieldName)));
    }

    private void findGettersAndSetters(MethodWrapper methodWrapper) {
        if(methodWrapper.isGetter())
            getters.put(methodWrapper.getFieldName(), methodWrapper);
        else if(methodWrapper.isSetter())
            setters.put(methodWrapper.getFieldName(), methodWrapper);
    }

    public Stream<Property> getFields() {
        return fields;
    }
}
