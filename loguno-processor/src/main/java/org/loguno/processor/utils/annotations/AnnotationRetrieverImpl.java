package org.loguno.processor.utils.annotations;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.tools.javac.tree.JCTree;
import org.loguno.processor.handlers.HandlersProvider;
import org.loguno.processor.utils.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class AnnotationRetrieverImpl implements AnnotationRetriever {

	@Override
	public Stream<? extends Annotation> getTreeAnnotations(ModifiersTree modifiers) {

		java.util.List<? extends AnnotationTree> annotations = modifiers.getAnnotations();
		Set<Annotation> result = new HashSet<>();

		annotations.forEach(o -> {
			JCTree atype = ((JCTree.JCAnnotation) o).annotationType;
			String annName = (atype.toString() != null) ? atype.type.toString() : atype.toString();

			Optional<Class<? extends Annotation>> annotationClass = HandlersProvider.instance().getAnnotationClassByName(annName);

			Annotation anno = AnnotationUtils.createAnnotationInstance(o, annotationClass.get());
			result.add(anno);

		});
		return result.stream();
	}
}
