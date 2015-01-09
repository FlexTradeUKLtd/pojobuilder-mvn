package flextrade.buildtool.builder;

import java.io.IOException;

import com.sun.codemodel.JClassAlreadyExistsException;

public class BuilderBuilder implements Builder {

    private Class clazz;
    private String outputDir;

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
        if((clazz == null) || (outputDir == null))
            throw new NullPointerException("Values not set in builder");

        new PojoBuilderBuilder(clazz, outputDir);
    }

}
