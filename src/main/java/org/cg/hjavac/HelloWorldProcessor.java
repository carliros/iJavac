package org.cg.hjavac;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * @author CG
 * @version 0.1
 *          Created at 8/05/13 17:18
 */

@SupportedAnnotationTypes("HelloWorld")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class HelloWorldProcessor extends AbstractProcessor {
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        if (!roundEnvironment.processingOver()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Hi Carlos Gomez");
        }

        return true;
    }
}
