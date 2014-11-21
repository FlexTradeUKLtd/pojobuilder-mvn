package flextrade.buildtool;

import flextrade.buildtool.builder.MatcherBuilder;

/**
 * Echos an object string to the output screen.
 * @goal build-matchers
 * @requiresProject true
 */
public class MatcherBuilderMojo extends AbstractBuilderMojo {

    public MatcherBuilderMojo() {
        super(new MatcherBuilder());
    }
}
