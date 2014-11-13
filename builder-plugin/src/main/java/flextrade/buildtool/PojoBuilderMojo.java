package flextrade.buildtool;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.dyuproject.protostuff.Message;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.sun.codemodel.JClassAlreadyExistsException;
import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

import flextrade.buildtool.builder.PojoBuilderBuilder;

/**
 * Echos an object string to the output screen.
 * @goal compile
 * @requiresProject true
 * @requiresDependencyResolution test
 */
public class PojoBuilderMojo extends AbstractMojo
{
    public static final String TARGET_BUILDERS_SOURCES = "./target/generated-sources/builders/";
    public static final String TARGET_BUILDERS_CLASSES = "./target/builders/classes";
    public static final String $_PROJECT_BUILD_DIRECTORY = "${project.build.directory}/";
    /**
     * the "target" directory of the build
     * @parameter expression="project.build.directory"
     */
    private String buildDirectory;

//    /**
//     * @parameter expression="project"
//     */
//    private MavenProject project;

    /**
     * The project currently being build.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * The current Maven session.
     *
     * @parameter expression="${session}"
     * @required
     * @readonly
     */
    private MavenSession mavenSession;


    /**
     * The Maven BuildPluginManager component.
     *
     * @component
     * @required
     */
    private BuildPluginManager pluginManager;

    public void execute() throws MojoExecutionException, MojoFailureException
    {

        Set<Class<? extends Message>> classes = findClasses();

        for(Class<? extends Message> clazz : classes) {
            getLog().info("creating builder for " + clazz);
            try {
                new PojoBuilderBuilder(clazz).build();
            } catch (JClassAlreadyExistsException | IOException e){
                getLog().error(e);
            }
        }

        getLog().info("builders created");

        Plugin compilerPlugin = plugin(
                groupId("org.apache.maven.plugins"),
                artifactId("maven-compiler-plugin"),
                version("3.2")
        );

//        Dependency dependency = new Dependency();
//
//        compilerPlugin.addDependency(dependency);

        String outputDirectory = TARGET_BUILDERS_CLASSES;
        getLog().info("outputDirectory = " + outputDirectory);
        getLog().info($_PROJECT_BUILD_DIRECTORY + TARGET_BUILDERS_SOURCES);


        String sourcesToInclude = TARGET_BUILDERS_SOURCES + "/**/*.java";
       getLog().info("sourcesToInclude = " + sourcesToInclude);

        executeMojo(
                compilerPlugin,
                goal("compile"),
                configuration(
                        element("skipMain", "true"),
                        element("verbose", "true"),
                        element("source", "1.6"),
                        element("target", "1.6"),
                        element("outputDirectory", outputDirectory),
//                        element("generatedSourcesDirectory", $_PROJECT_BUILD_DIRECTORY + TARGET_BUILDERS_SOURCES)
                        element("includes",
                                element("include", sourcesToInclude)
                        )
                ),
                executionEnvironment(project, mavenSession, pluginManager)
        );

    }

    private Set<Class<? extends Message>> findClasses() throws MojoFailureException {
        List<URL> urls;
        try {
            for(String s : project.getCompileClasspathElements()) {
                getLog().info( s );
            }


            urls = Lists.transform(project.getCompileClasspathElements(), new ToURL());
                } catch (DependencyResolutionRequiredException e) {
            getLog().error(e);
            throw new MojoFailureException("Unable to get runtimeClasspathElements", e);
        }


        getLog().info("got " + urls.size() + " urls");
        URLClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[0]), this.getClass().getClassLoader());

        getLog().info("got classLoader");

        Arrays.asList(classLoader.getURLs()).stream().forEach(url -> getLog().info(url.toString()));

        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                    .setScanners(new SubTypesScanner())
                    .setUrls(ClasspathHelper.forClassLoader(classLoader))
                    .addClassLoader(classLoader));


        getLog().info("got reflections");

        Set<Class<? extends Message>> classes = reflections.getSubTypesOf(Message.class);

        getLog().info("got " + classes.size() + " classes");
        return classes;
    }

    private static class ToURL implements Function<String, URL> {
        @Override
        public URL apply(@Nullable String s) {
            try {
                return new File(s).toURL();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
