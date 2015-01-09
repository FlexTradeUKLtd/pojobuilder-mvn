package com.flextrade.builder;


import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.flextrade.builder.builder.Builder;
import com.sun.codemodel.JCodeModel;

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
     * @parameter property = "outputDirectory", defaultValue = "/target/fail/generated-sources/"
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

        for(Class<?> clazz : classes) {
            getLog().info("creating builder for " + clazz);
            try {
                buildFile(builder.fromClass(clazz));
            } catch (Exception e){
                getLog().error(e);
            }
        }

        getLog().info("builders created");
    }

    private void buildFile(JCodeModel codeModel) throws IOException {
        File file = new File(outputDirectory);
        file.mkdirs();
        codeModel.build(file);
    }
}
