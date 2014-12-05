package com.flextrade.pojobuilderplugin;

import java.util.List;
import java.util.Optional;

public class Pojo extends PojoSuper {

    private String stringField;
    private boolean booleanField;
    private Optional<Integer> optionalInteger;
    private List<String> listOfStrings;

    private List<Optional<Integer>> listOfOptionalIntegers;
    private int[] intArray;
    private List<?> listOfAnything;

    public void setStringField(String stringField) {
        this.stringField = stringField;
    }
    public String getStringField() {
        return stringField;
    }


    public boolean isBooleanField() {
        return booleanField;
    }
    public void setBooleanField(boolean booleanField) {
        this.booleanField = booleanField;
    }


    public Optional<Integer> getOptionalInteger() {
        return optionalInteger;
    }

    public void setOptionalInteger(Optional<Integer> optionalInteger) {
        this.optionalInteger = optionalInteger;
    }

    public List<String> getListOfStrings() {
        return listOfStrings;
    }

    public void setListOfStrings(List<String> listOfStrings) {
        this.listOfStrings = listOfStrings;
    }

    public List<Optional<Integer>> getListOfOptionalIntegers() {
        return listOfOptionalIntegers;
    }

    public void setListOfOptionalIntegers(List<Optional<Integer>> listOfOptionalIntegers) {
        this.listOfOptionalIntegers = listOfOptionalIntegers;
    }

    public int[] getIntArray() {
        return intArray;
    }

    public void setIntArray(int[] intArray) {
        this.intArray = intArray;
    }

    public List<?> getListOfAnything() {
        return listOfAnything;
    }

    public void setListOfAnything(List<?> listOfAnything) {
        this.listOfAnything = listOfAnything;
    }
}
