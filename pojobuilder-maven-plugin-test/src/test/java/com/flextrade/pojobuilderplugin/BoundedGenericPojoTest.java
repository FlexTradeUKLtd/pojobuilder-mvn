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

public class BoundedGenericPojoTest {

    @Rule public FixtureRule fixtureRule = FixtureRule.initFixtures();

    @Fixture private Integer intVal1;
    @Fixture private Integer intVal2;
    @Fixture private Integer otherIntVal;

    BoundedGenericPojo<Integer> boundedGenericPojo;

    @Test
    public void tVal_should_be_matchable() {
        boundedGenericPojo = createBuilder().withtVal(intVal1).build();

        assertThat(boundedGenericPojo.gettVal(), is(intVal1));

        assertThat(boundedGenericPojo, createMatcher().withtVal(intVal1));
        assertThat(boundedGenericPojo, createMatcher().withtVal(is(intVal1)));

        assertThat(boundedGenericPojo, not(createMatcher().withtVal(otherIntVal)));
        assertThat(boundedGenericPojo, createMatcher().withtVal(not(is(otherIntVal))));
    }

    @Test
    public void listOfT_should_be_matchable() {
        List<Integer> list = asList(intVal1, intVal2);
        boundedGenericPojo = createBuilder().withListOfT(list).build();

        assertThat(boundedGenericPojo.getListOfT(), is(list));

        assertThat(boundedGenericPojo, createMatcher().withListOfT(list));
        assertThat(boundedGenericPojo, createMatcher().withListOfT(is(list)));

        assertThat(boundedGenericPojo, not(createMatcher().withListOfT(asList(otherIntVal))));
        assertThat(boundedGenericPojo, createMatcher().withListOfT((Matcher) not(contains(otherIntVal))));
    }

    private BoundedGenericPojoMatcher<Integer> createMatcher() {
        return new BoundedGenericPojoMatcher<Integer>();
    }

    private BoundedGenericPojoBuilder<Integer> createBuilder() {
        return new BoundedGenericPojoBuilder<Integer>();
    }
}