package flextrade.buildtool;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

public class ClassFinder {

    private final MavenProject project;
    private final Log log;

    private Optional<MalformedURLException> urlException = Optional.empty();

    public ClassFinder(MavenProject project, Log log) {
        this.project = project;
        this.log = log;
    }

    public Set<Class<?>> findClassesWhichExtend(String className) throws MojoFailureException {
        URLClassLoader classLoader = getDependencyClassLoader();
        log.debug("got classLoader");

        try {
            Class<?> superClass = Class.forName(className, true, classLoader);
            return newHashSet(findClassesWhichExtend(superClass, classLoader));
        } catch (ClassNotFoundException e) {
            log.error(e);
            throw new MojoFailureException("Unable to find pojo classes");
        }
    }

    private URLClassLoader getDependencyClassLoader() throws MojoFailureException {
        List<URL> urls = project.getDependencyArtifacts().stream().map(Artifact::getFile).map(f -> toURL(f)).collect(toList());

        log.debug("got " + urls.size() + " urls");

        urls.forEach(url -> log.debug(url.toString()));

        if(urlException.isPresent()) {
            throw new MojoFailureException("Unable to parse dependency URLs", urlException.get());
        }

        return new URLClassLoader(urls.toArray(new URL[0]), this.getClass().getClassLoader());
    }

    private <T> Set<Class<? extends T>> findClassesWhichExtend(Class<T> superClass, URLClassLoader classLoader) {
        Arrays.asList(classLoader.getURLs()).forEach(url -> log.info(url.toString()));

        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setScanners(new SubTypesScanner())
                        .setUrls(ClasspathHelper.forClassLoader(classLoader))
                        .addClassLoader(classLoader));

        log.debug("got reflections");

        Set<Class<? extends T>> classes = reflections.getSubTypesOf(superClass);

        log.debug("got " + classes.size() + " classes");
        return classes;
    }

    private URL toURL(File file) {
        try {
            return file.toURL();
        } catch (MalformedURLException e) {
            log.error(e);
            urlException = Optional.of(e);
            return null;
        }
    }

    private URL toURL(String path) {
        try {
            return new URL(path);
        } catch (MalformedURLException e) {
            log.error(e);
            urlException = Optional.of(e);
            return null;
        }
    }
}
