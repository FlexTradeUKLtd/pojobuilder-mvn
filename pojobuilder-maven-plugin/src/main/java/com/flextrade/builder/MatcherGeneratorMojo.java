package com.flextrade.builder;

import com.flextrade.builder.builder.MatcherGenerator;

/**
 * Echos an object string to the output screen.
 * @goal build-matchers
 * @requiresProject true
 */
public class MatcherGeneratorMojo extends AbstractBuilderMojo {

    public MatcherGeneratorMojo() {
        super(new MatcherGenerator());
    }
}
