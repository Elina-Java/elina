package instrumentation.processor;

import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;

import instrumentation.definitions.DistRedTask;

public class DistRedInterfaceVisitor implements ElementVisitor<Set<String>, Object> {

	Set<String> mrMethods = new HashSet<String>();
	
	@Override
	public Set<String> visit(Element e, Object p) {
		for (Element ee : e.getEnclosedElements()) {
			ee.accept(this, p);
		}
		return this.mrMethods;
	}

	@Override
	public Set<String> visit(Element e) {
		return this.mrMethods;
	}

	@Override
	public Set<String> visitPackage(PackageElement e, Object p) {
		for (Element ee : e.getEnclosedElements()) {
			ee.accept(this, p);
		}
		return this.mrMethods;
	}

	@Override
	public Set<String> visitType(TypeElement e, Object p) {
		for (Element ee : e.getEnclosedElements()) {
			ee.accept(this, p);
		}
		return this.mrMethods;
	}

	@Override
	public Set<String> visitVariable(VariableElement e, Object p) {
		for (Element ee : e.getEnclosedElements()) {
			ee.accept(this, p);
		}
		return this.mrMethods;
	}

	@Override
	public Set<String> visitExecutable(ExecutableElement e, Object p) {
		for (Element ee : e.getEnclosedElements()) {
			ee.accept(this, p);
		}
		if(e.getAnnotation(DistRedTask.class)!=null)
			this.mrMethods.add(e.getSimpleName().toString());
		return this.mrMethods;
	}

	@Override
	public Set<String> visitTypeParameter(TypeParameterElement e, Object p) {
		for (Element ee : e.getEnclosedElements()) {
			ee.accept(this, p);
		}
		return this.mrMethods;
	}

	@Override
	public Set<String> visitUnknown(Element e, Object p) {
		for (Element ee : e.getEnclosedElements()) {
			ee.accept(this, p);
		}
		return this.mrMethods;
	}

}
