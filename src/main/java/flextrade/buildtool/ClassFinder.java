package flextrade.buildtool;

import static com.google.common.collect.Sets.newHashSet;

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

    public Set<Class<?>> findClasses(String className) throws MojoFailureException {
        List<URL> urls = project.getDependencyArtifacts().stream().map(Artifact::getFile).map(new ToURL()).collect(Collectors.<URL>toList());

        log.info("got " + urls.size() + " urls");
        URLClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[0]), this.getClass().getClassLoader());
        log.info("got classLoader");

        try {
            Class<?> superClass = Class.forName(className, true, classLoader);


            return newHashSet(findClasses(classLoader, superClass));


        } catch (ClassNotFoundException e) {
            log.error(e);
            throw new MojoFailureException("Unable to find pojo classes");
        }

    }

    private <T> Set<Class<? extends T>> findClasses(URLClassLoader classLoader, Class<T> superClass) {
        Arrays.asList(classLoader.getURLs()).stream().forEach(url -> log.info(url.toString()));

        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setScanners(new SubTypesScanner())
                        .setUrls(ClasspathHelper.forClassLoader(classLoader))
                        .addClassLoader(classLoader));

        log.info("got reflections");

        Set<Class<? extends T>> classes = reflections.getSubTypesOf(superClass);

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
