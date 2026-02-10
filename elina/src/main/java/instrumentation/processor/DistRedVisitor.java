package instrumentation.processor;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;

import instrumentation.definitions.DistRed;

public class DistRedVisitor implements ElementVisitor<Object, Object>{

	public static Map<String,TypeElement> distRedTypes = new HashMap<String,TypeElement>();
	
	@Override
	public Object visit(Element e, Object p) {
		return null;
	}

	@Override
	public Object visit(Element e) {
		return null;
	}

	@Override
	public Object visitPackage(PackageElement e, Object p) {
		return null;
	}

	@Override
	public Object visitType(TypeElement e, Object p) {
		if(e.getAnnotation(DistRed.class)!=null){
			distRedTypes.put(e.getQualifiedName().toString(), e);
		}
		return null;
	}

	@Override
	public Object visitVariable(VariableElement e, Object p) {
		return null;
	}

	@Override
	public Object visitExecutable(ExecutableElement e, Object p) {
		return null;
	}

	@Override
	public Object visitTypeParameter(TypeParameterElement e, Object p) {
		return null;
	}

	@Override
	public Object visitUnknown(Element e, Object p) {
		return null;
	}

}
