package flextrade.buildtool.builder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class FieldFinder {

    public Stream<Property> getFields(Class clazz) {
        HashMap<String, MethodWrapper> getters = new HashMap<>();
        HashMap<String, MethodWrapper> setters = new HashMap<>();

        Arrays.asList(clazz.getMethods()).stream().map(MethodWrapper::new).forEach(new Consumer<MethodWrapper>() {

            @Override
            public void accept(MethodWrapper methodWrapper) {
                if(methodWrapper.isGetter())
                    getters.put(methodWrapper.getFieldName(), methodWrapper);
                else if(methodWrapper.isSetter())
                    setters.put(methodWrapper.getFieldName(), methodWrapper);
            }
        });

        Set<String> fieldNames = new HashSet<>(getters.keySet());
        fieldNames.retainAll(setters.keySet());

        return fieldNames.stream().map(fieldName -> new Property(getters.get(fieldName), setters.get(fieldName)));
    }

}
