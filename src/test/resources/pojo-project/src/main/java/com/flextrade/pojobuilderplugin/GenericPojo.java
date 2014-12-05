package com.flextrade.pojobuilderplugin;

import java.util.List;

public class GenericPojo<T> {

    private List<T> listOfT;
    private T tVal;

    public T gettVal() {
        return tVal;
    }

    public void settVal(T tVal) {
        this.tVal = tVal;
    }

    public List<T> getListOfT() {
        return listOfT;
    }

    public void setListOfT(List<T> listOfT) {
        this.listOfT = listOfT;
    }
}
