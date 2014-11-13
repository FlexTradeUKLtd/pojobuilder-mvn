package flextrade.buildtool;

import java.util.Locale;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

class MyProcessingEnvironment implements ProcessingEnvironment {

    @Override
    public Map<String, String> getOptions() {
        //TODO implement method body
        throw new UnsupportedOperationException();
    }

    @Override
    public Messager getMessager() {
        //TODO implement method body
        throw new UnsupportedOperationException();
    }

    @Override
    public Filer getFiler() {
        //TODO implement method body
        throw new UnsupportedOperationException();
    }

    @Override
    public Elements getElementUtils() {
        //TODO implement method body
        throw new UnsupportedOperationException();
    }

    @Override
    public Types getTypeUtils() {
        //TODO implement method body
        throw new UnsupportedOperationException();
    }

    @Override
    public SourceVersion getSourceVersion() {
        //TODO implement method body
        throw new UnsupportedOperationException();
    }

    @Override
    public Locale getLocale() {
        //TODO implement method body
        throw new UnsupportedOperationException();
    }
}
