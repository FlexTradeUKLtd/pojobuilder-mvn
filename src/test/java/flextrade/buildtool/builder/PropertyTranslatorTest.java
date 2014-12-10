package flextrade.buildtool.builder;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.reflect.TypeToken;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JType;

import flextrade.buildtool.model.PropertyTranslator;

public class PropertyTranslatorTest {
    PropertyTranslator propertyTranslator;

    JCodeModel codeModel = new JCodeModel();

    @Before
    public void setupPropertyTranslater() {
        propertyTranslator = new PropertyTranslator(codeModel, new HashMap<>());
    }

    @Test
    public void given_Integer_then_returned_type_is_integer() {
        JType returned = propertyTranslator.getJType(Integer.class);

        assertThat(returned.name(), is("Integer"));
    }

    @Test
    public void given_Optional_Integer_then_returned_type_is_optional_integer() {
        JType returned = propertyTranslator.getJType( new TypeToken<Optional<Integer>>(){}.getType() );

        assertThat(returned.name(), is("Optional<Integer>"));
    }

    @Test
    public void given_Optional_anything_then_returned_type_is_optional_anything_that_extends_Object() {
        JType returned = propertyTranslator.getJType( new TypeToken<Optional<?>>(){}.getType() );

        assertThat(returned.name(), is("Optional<? extends Object>"));
    }

    @Test
    public void given_Optional_of_something_that_extends_Number_then_returned_type_is_optional_anything_that_extends_Number() {
        JType returned = propertyTranslator.getJType( new TypeToken<Optional<? extends Number>>(){}.getType() );

        assertThat(returned.name(), is("Optional<? extends Number>"));
    }

    @Test @Ignore //<X super Y> not supported by JCodeModel :(
    public void given_Optional_of_something_that_is_a_super_of_Integer_then_returned_type_is_correct() {
        JType returned = propertyTranslator.getJType( new TypeToken<Optional<? super Integer>>(){}.getType() );

        assertThat(returned.name(), is("Optional<? super Integer>"));
    }

    @Test
    public void given_Map_of_String_to_Int_then_returned_class_is_correct() {
        JType returned = propertyTranslator.getJType( new TypeToken<Map<String, Integer>>(){}.getType() );

        assertThat(returned.name(), is("Map<String,Integer>"));
    }

    @Test
    public void given_Map_of_anything_String_to_anything_Int_then_returned_class_is_correct() {
        JType returned = propertyTranslator.getJType( new TypeToken<Map<? extends String, ? extends Integer>>(){}.getType() );

        assertThat(returned.name(), is("Map<? extends String,? extends Integer>"));
    }

    @Test
    public void given_an_boolean_then_returned_JClass_is_correct() {
        JType returned = propertyTranslator.getJType( Integer.TYPE );

        assertThat(returned.name(), is("int"));
    }
}