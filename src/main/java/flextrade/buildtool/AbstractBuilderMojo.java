package flextrade.buildtool;


import java.io.IOException;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.sun.codemodel.JClassAlreadyExistsException;

import flextrade.buildtool.builder.Builder;

public abstract class AbstractBuilderMojo extends AbstractMojo {

    /**
     * The project currently being build.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter property = "output.dir", defaultValue = "/target/fail/generated-sources/"
     */
    private String outputDirectory;

    /**
     * @parameter property
     */
    private String subClassesOf;

    private final Builder builder;

    public AbstractBuilderMojo(Builder builder) {
        this.builder = builder;
    }

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        ClassFinder classFinder = new ClassFinder(project, getLog());

        Set<Class<?>> classes = classFinder.findClassesWhichExtend(subClassesOf);

        builder.outputTo(outputDirectory);

        for(Class<?> clazz : classes) {
            getLog().info("creating builder for " + clazz);
            try {
                builder.fromClass(clazz).build();
            } catch (JClassAlreadyExistsException | IOException e){
                getLog().error(e);
            }
        }

        getLog().info("builders created");
    }


}
