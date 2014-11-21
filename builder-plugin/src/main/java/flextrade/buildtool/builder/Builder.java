package flextrade.buildtool.builder;

import java.io.IOException;

import com.sun.codemodel.JClassAlreadyExistsException;

public interface Builder {

    public Builder fromClass(Class clazz);

    public Builder outputTo(String outputDir);

    public void build() throws IOException, JClassAlreadyExistsException;
}
