package flextrade.buildtool;


import java.io.IOException;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.dyuproject.protostuff.Message;
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
     * @parameter property = "output.dir", defaultValue = "/target/generated-sources/"
     */
    private String outputDirectory;

    private final Builder builder;

    public AbstractBuilderMojo(Builder builder) {
        this.builder = builder;
    }

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        ClassFinder classFinder = new ClassFinder(project, getLog());

        try {
//            Set<Class<?>> classes = classFinder.findSubClassesOf("com.dyuproject.protostuff.Message");
            Set<Class<?>> classes = classFinder.findSubClassesOf(Message.class.getName());

            builder.outputTo(outputDirectory);

            for(Class<?> clazz : classes) {
                getLog().info("creating builder for " + clazz);

                builder.fromClass(clazz).build();
            }

            getLog().info("builders created");

        } catch (JClassAlreadyExistsException | IOException | ClassNotFoundException e){
            getLog().error(e);
        }
    }


}
