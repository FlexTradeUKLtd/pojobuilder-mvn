package com.flextrade.pojobuilderplugin;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;

import com.flextrade.jfixture.annotations.Fixture;
import com.flextrade.jfixture.rules.FixtureRule;

public class GenericPojoTest {

    @Rule public FixtureRule fixtureRule = FixtureRule.initFixtures();

    @Fixture private String stringVal1;
    @Fixture private String stringVal2;
    @Fixture private String otherStringVal;

    GenericPojo<String> GenericPojo;

    @Test
    public void tVal_should_be_matchable() {
        GenericPojo = createBuilder().withtVal(stringVal1).build();

        assertThat(GenericPojo.gettVal(), is(stringVal1));

        assertThat(GenericPojo, createMatcher().withtVal(stringVal1));
        assertThat(GenericPojo, createMatcher().withtVal(is(stringVal1)));

        assertThat(GenericPojo, not(createMatcher().withtVal(otherStringVal)));
        assertThat(GenericPojo, createMatcher().withtVal(not(is(otherStringVal))));
    }

    @Test
    public void listOfT_should_be_matchable() {
        List<String> list = asList(stringVal1, stringVal2);
        GenericPojo = createBuilder().withListOfT(list).build();

        assertThat(GenericPojo.getListOfT(), is(list));

        assertThat(GenericPojo, createMatcher().withListOfT(list));
        assertThat(GenericPojo, createMatcher().withListOfT(is(list)));

        assertThat(GenericPojo, not(createMatcher().withListOfT(asList(otherStringVal))));
        assertThat(GenericPojo, createMatcher().withListOfT((Matcher) not(contains(otherStringVal))));
    }

    private GenericPojoMatcher<String> createMatcher() {
        return new GenericPojoMatcher<String>();
    }

    private GenericPojoBuilder<String> createBuilder() {
        return new GenericPojoBuilder<String>();
    }
}