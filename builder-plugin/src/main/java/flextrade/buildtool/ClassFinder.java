package flextrade.buildtool;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.dyuproject.protostuff.Message;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class ClassFinder {


    private final MavenProject project;
    private final Log log;

    public ClassFinder(MavenProject project, Log log) {
        this.project = project;
        this.log = log;
    }

    public Set<Class<? extends Message>> findClasses() throws MojoFailureException {
        List<URL> urls;
        try {
            for(String s : project.getCompileClasspathElements()) {
                log.info(s);
            }

            urls = Lists.transform(project.getCompileClasspathElements(), new ToURL());
        } catch (DependencyResolutionRequiredException e) {
            log.error(e);
            throw new MojoFailureException("Unable to get runtimeClasspathElements", e);
        }


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
