package flextrade.buildtool;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.dyuproject.protostuff.Message;

public class ClassFinder {


    private final MavenProject project;
    private final Log log;

    public ClassFinder(MavenProject project, Log log) {
        this.project = project;
        this.log = log;
    }

    public Set<Class<? extends Message>> findClasses() throws MojoFailureException {
        List<URL> urls = project.getDependencyArtifacts().stream().map(Artifact::getFile).map(new ToURL()).collect(Collectors.<URL>toList());

        log.info("got " + urls.size() + " urls");
        URLClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[0]), this.getClass().getClassLoader());
        log.info("got classLoader");

        Arrays.asList(classLoader.getURLs()).stream().forEach(url -> log.info(url.toString()));

        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setScanners(new SubTypesScanner())
                        .setUrls(ClasspathHelper.forClassLoader(classLoader))
                        .addClassLoader(classLoader));


        log.info("got reflections");

        Set<Class<? extends Message>> classes = reflections.getSubTypesOf(Message.class);

        log.info("got " + classes.size() + " classes");
        return classes;
    }

    private static class ToURL implements Function<File, URL> {
        @Override
        public URL apply(File file) {
            try {
                return file.toURL();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
