package com.flextrade.pojobuilderplugin;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Optional;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.flextrade.jfixture.annotations.Fixture;
import com.flextrade.jfixture.rules.FixtureRule;


public class PojoTest {

    @Rule public FixtureRule fixtureRule = FixtureRule.initFixtures();

    @Fixture boolean booolVal;
    @Fixture String stringVal1;
    @Fixture String stringVal2;
    @Fixture String otherStringVal;
    @Fixture int integerVal1;
    @Fixture int integerVal2;
    @Fixture int otherIntegerVal;

    Pojo pojo;
    
    private Optional<Integer> optionalIntegerVal;
    private Optional<Integer> optionalIntegerVal1;
    private Optional<Integer> otherOptionalIntegerVal;

    @Before
    public void setup() {
        optionalIntegerVal = Optional.of(integerVal1);
        optionalIntegerVal1 = Optional.of(integerVal2);
        otherOptionalIntegerVal = Optional.of(otherIntegerVal);
    }

    @Test
    public void empty_pojo_should_be_matchable() {
        pojo = newBuilder().build();

        assertThat(pojo, newMatcher());
        assertThat(new Object(), (Matcher) not(newMatcher()));
    }

    @Test
    public void string_field_should_be_matchable() {
        pojo = newBuilder().withStringField(stringVal1).build();

        assertThat(pojo, newMatcher().withStringField(stringVal1));
        assertThat(pojo, newMatcher().withStringField(is(stringVal1)));

        assertThat(pojo, not(newMatcher().withStringField(otherStringVal)));
        assertThat(pojo, newMatcher().withStringField(not(otherStringVal)));
    }

    @Test
    public void boolean_field_should_be_matchable() {
        pojo = newBuilder().withBooleanField(booolVal).build();

        assertThat(pojo.isBooleanField(), is(booolVal));

        assertThat(pojo, newMatcher().withBooleanField(booolVal));
        assertThat(pojo, newMatcher().withBooleanField(is(booolVal)));

        assertThat(pojo, not(newMatcher().withBooleanField(!booolVal)));
        assertThat(pojo, newMatcher().withBooleanField(not(!booolVal)));
    }

    @Test
    public void optional_filed_should_be_matchable() {
        pojo = newBuilder().withOptionalInteger(optionalIntegerVal).build();

        assertThat(pojo.getOptionalInteger(), is(optionalIntegerVal));

        assertThat(pojo, newMatcher().withOptionalInteger(optionalIntegerVal));
        assertThat(pojo, newMatcher().withOptionalInteger(is(optionalIntegerVal)));

        assertThat(pojo, not(newMatcher().withOptionalInteger(otherOptionalIntegerVal)));
        assertThat(pojo, newMatcher().withOptionalInteger(not(otherOptionalIntegerVal)));
    }

    @Test
    public void listOfStrings_should_be_matchable() {
        pojo = newBuilder().withListOfStrings(asList(stringVal1, stringVal2)).build();

        assertThat(pojo.getListOfStrings(), contains(stringVal1, stringVal2));

        assertThat(pojo, newMatcher().withListOfStrings(asList(stringVal1, stringVal2)));
        assertThat(pojo, newMatcher().withListOfStrings((Matcher) contains(stringVal1, stringVal2)));

        assertThat(pojo, not(newMatcher().withListOfStrings(asList(otherStringVal, stringVal1))));
        assertThat(pojo, newMatcher().withListOfStrings(not((Matcher) hasItem(otherStringVal))));
    }

    @Test
    public void listOfOptionalIntegers_should_be_matchable() {
        pojo = newBuilder().withListOfOptionalIntegers(asList(optionalIntegerVal, optionalIntegerVal1)).build();

        assertThat(pojo.getListOfOptionalIntegers(), contains(optionalIntegerVal, optionalIntegerVal1));

        assertThat(pojo, newMatcher().withListOfOptionalIntegers(asList(optionalIntegerVal, optionalIntegerVal1)));
        assertThat(pojo, newMatcher().withListOfOptionalIntegers((Matcher) contains(optionalIntegerVal, optionalIntegerVal1)));

        assertThat(pojo, not(newMatcher().withListOfOptionalIntegers(asList(optionalIntegerVal1, optionalIntegerVal))));
        assertThat(pojo, newMatcher().withListOfOptionalIntegers(not((Matcher) hasItem(otherOptionalIntegerVal))));
    }

    @Test
    public void intArray_should_be_matchable() {
        pojo = newBuilder().withIntArray(toArray(integerVal1, integerVal2)).build();

        assertThat(pojo.getIntArray(), is(toArray(integerVal1, integerVal2)));

        assertThat(pojo, newMatcher().withIntArray(toArray(integerVal1, integerVal2)));
        assertThat(pojo, newMatcher().withIntArray(is(toArray(integerVal1, integerVal2))));

        assertThat(pojo, not(newMatcher().withIntArray(toArray(integerVal2, integerVal1))));
        assertThat(pojo, newMatcher().withIntArray(not(is(toArray(otherIntegerVal)))));
    }

    @Test
    public void listOfAnything_should_be_matchable() {
        List<Object> listVal = asList(optionalIntegerVal, stringVal1, integerVal1);
        pojo = newBuilder().withListOfAnything(listVal).build();

        assertThat((List<Object>)pojo.getListOfAnything(), is(listVal));

        assertThat(pojo, newMatcher().withListOfAnything(listVal));
        assertThat(pojo, newMatcher().withListOfAnything((Matcher) is(listVal)));

        assertThat(pojo, not(newMatcher().withListOfAnything(asList(otherOptionalIntegerVal, otherStringVal, otherIntegerVal))));
        assertThat(pojo, newMatcher().withListOfAnything((Matcher) not(is(asList(otherOptionalIntegerVal, otherStringVal, otherIntegerVal)))));
    }

    protected PojoMatcher newMatcher() {
        return new PojoMatcher();
    }

    protected PojoBuilder newBuilder() {
        return new PojoBuilder();
    }

    private int[] toArray(int... vals) {
        return vals;
    }

}
