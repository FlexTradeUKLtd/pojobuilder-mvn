package flextrade.buildtool;

import java.io.BufferedReader;

import com.dyuproject.protostuff.Schema;

public class APojo extends Pojo {


    private String myField;
    private BufferedReader myReader;

    public String getMyField() {
        return myField;
    }

    public void setMyField(String myField) {
        this.myField = myField;
    }

    public BufferedReader getMyReader() {
        return myReader;
    }

    public void setMyReader(BufferedReader myReader) {
        this.myReader = myReader;
    }

    @Override
    public Schema cachedSchema() {
        //TODO implement method body
        throw new UnsupportedOperationException();
    }
}
