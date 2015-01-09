package flextrade.buildtool.builder;

import java.io.IOException;

import com.sun.codemodel.JClassAlreadyExistsException;

public class MatcherBuilder implements Builder {
    private String outputDir;
    private Class clazz;

    @Override
    public Builder fromClass(Class clazz) {
        this.clazz = clazz;
        return this;
    }

    @Override
    public Builder outputTo(String outputDir) {
        this.outputDir = outputDir;
        return this;
    }

    @Override
    public void build() throws IOException, JClassAlreadyExistsException {
        new PojoMatcherBuilder(clazz, outputDir);
    }
}
