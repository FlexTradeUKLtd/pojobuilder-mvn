package flextrade.buildtool;

import flextrade.buildtool.builder.MatcherGenerator;

/**
 * Echos an object string to the output screen.
 * @goal build-matchers
 * @requiresProject true
 * @resolveDependencies true
 */
public class MatcherGeneratorMojo extends AbstractBuilderMojo {

    public MatcherGeneratorMojo() {
        super(new MatcherGenerator());
    }
}
