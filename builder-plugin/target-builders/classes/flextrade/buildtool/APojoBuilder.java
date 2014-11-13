
package flextrade.buildtool;

import java.io.BufferedReader;

public class APojoBuilder {

    BufferedReader myReader;
    String myField;

    public APojoBuilder withMyReader(String myReader) {
        this.myReader = myReader;
        return this;
    }

    public APojoBuilder withMyField(String myField) {
        this.myField = myField;
        return this;
    }

    public APojoBuilder build() {
        APojoBuilder result = new APojo();
        result.setMyReader(myReader);
        result.setMyField(myField);
        return result;
    }

}
