package com.flextrade.pojobuilderplugin;

public class Pojo extends PojoSuper {

    private String stringField;
    private boolean booleanField;

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
}
