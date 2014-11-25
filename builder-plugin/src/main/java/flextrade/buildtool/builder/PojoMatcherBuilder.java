package flextrade.buildtool.builder;

import static com.sun.codemodel.JExpr.ref;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import com.dyuproject.protostuff.Message;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class PojoMatcherBuilder<T extends Message> {

    private final JCodeModel codeModel = new JCodeModel();
    private final Class<T> clazz;
    private final String outputDir;
    private final JDefinedClass definedClass;
    private final JFieldVar matcherField;
    private final Stream<Property> fields;

    public PojoMatcherBuilder(Class<T> clazz, String outputDir) throws JClassAlreadyExistsException, IOException {
        this.clazz = clazz;
        this.outputDir = outputDir;
        definedClass = codeModel._class(clazz.getName() + "Matcher");
        definedClass._extends(codeModel.ref(BaseMatcher.class).narrow(clazz));
        matcherField = definedClass.field(JMod.PRIVATE, codeModel.ref(Matcher.class).narrow(clazz), "matcher", isInstanceOfClazz());
        fields = new FieldFinder().getFields(clazz);

        createMatchesMethod();

        createDescriptToMethod();

        fields.forEach(new CreateWithMethod());

        buildFile();
    }


    private void createDescriptToMethod() {
        JMethod describeToMethod = definedClass.method(JMod.PUBLIC, codeModel.VOID, "describeTo");
        JVar descriptionVar = describeToMethod.param(Description.class, "description");
        describeToMethod.annotate(Override.class);
        describeToMethod.body().invoke(matcherField, "describeTo").arg(descriptionVar);
    }

    private void createMatchesMethod() {
        JMethod matchesMethod = definedClass.method(JMod.PUBLIC, codeModel.BOOLEAN, "matches");
        JVar param = matchesMethod.param(Object.class, "o");
        matchesMethod.annotate(Override.class);
        matchesMethod.body()._return(JExpr.invoke(matcherField, "matches").arg(param));
    }

    private JInvocation isInstanceOfClazz() {
        return codeModel.ref(Matchers.class).staticInvoke("instanceOf").arg(codeModel.ref(clazz).dotclass());
    }

    private void buildFile() throws IOException {
        File file = new File(outputDir);
        file.mkdirs();
        codeModel.build(file);
    }

    private class CreateWithMethod implements Consumer<Property> {
        @Override
        public void accept(Property property) {
            JMethod withMethod = definedClass.method(JMod.PUBLIC, definedClass, "with" + property.getFieldName());
            withMethod.param(property.getType(), property.getFieldNameCamelCase());
            withMethod.body().assign(matcherField,
                    matchers_allOf(
                            matcherField,
                            matchers_hasProperty(
                                    property.getFieldNameCamelCase(),
                                    is(property.getFieldNameCamelCase())
                            )
                    )

            );
            withMethod.body()._return(JExpr._this());
        }

        private JInvocation is(String expected) {
            return codeModel.ref(Matchers.class).staticInvoke("is").arg(ref(expected));
        }
    }

    private JInvocation matchers_allOf(JExpression matcher1, JExpression matcher2) {
        return codeModel.ref(Matchers.class).staticInvoke("allOf").arg(matcher1).arg(matcher2);
    }

    private JInvocation matchers_hasProperty(String propertyName, JExpression matcher) {
        return codeModel.ref(Matchers.class).staticInvoke("hasProperty").arg(JExpr.lit(propertyName)).arg(matcher);
    }
}
