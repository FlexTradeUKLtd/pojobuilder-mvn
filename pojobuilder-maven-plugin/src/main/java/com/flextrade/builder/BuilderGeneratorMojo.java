package com.flextrade.builder;

import com.flextrade.builder.builder.BuilderGenerator;

/**
 * Echos an object string to the output screen.
 * @goal build-builders
 * @requiresProject true
 */
public class BuilderGeneratorMojo extends AbstractBuilderMojo {

    public BuilderGeneratorMojo() {
        super(new BuilderGenerator());
    }
}
