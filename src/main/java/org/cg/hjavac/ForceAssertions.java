package org.cg.hjavac;

import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * @author CG
 * @version 0.1
 *          Created at 8/05/13 17:50
 */

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ForceAssertions extends AbstractProcessor {
    private int tally;
    private Trees trees;
    private TreeMaker make;
    private Name.Table names;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        trees = Trees.instance(env);
        Context context = ((JavacProcessingEnvironment) env).getContext();
        make = TreeMaker.instance(context);
        names = Name.Table.instance(context);
        tally = 0;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        if (!roundEnvironment.processingOver()) {
            Set<? extends Element> elements = roundEnvironment.getRootElements();
            for (Element each : elements) {
                if (each.getKind() == ElementKind.CLASS) {
                    JCTree tree = (JCTree) trees.getTree(each);
                    TreeTranslator visitor = new Inliner();
                    tree.accept(visitor);
                }
            }
        } else {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, tally + " assertions inlined.");
        }
        return false;
    }

    public class Inliner extends TreeTranslator {
        @Override
        public void visitAssert(JCTree.JCAssert tree) {
            super.visitAssert(tree);
            JCTree.JCStatement newNode = makeIfThrowException(tree);
            result = newNode;
            tally++;
        }

        private JCTree.JCStatement makeIfThrowException(JCTree.JCAssert node) {
            List<JCTree.JCExpression> args = node.getDetail() == null ? List.<JCTree.JCExpression>nil() : List.of(node.detail);

            JCTree.JCExpression expr = make.NewClass(null, null, make.Ident(names.fromString("AssertionError")), args, null);

            return make.If(make.Unary(JCTree.NOT, node.cond), make.Throw(expr), null);
        }
    }
}
