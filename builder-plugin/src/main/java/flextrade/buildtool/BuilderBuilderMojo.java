package flextrade.buildtool;

import flextrade.buildtool.builder.BuilderBuilder;

/**
 * Echos an object string to the output screen.
 * @goal build-builders
 * @requiresProject true
 * @resolveDependencies true
 */
public class BuilderBuilderMojo extends AbstractBuilderMojo {

    public BuilderBuilderMojo() {
        super(new BuilderBuilder());
    }
}
