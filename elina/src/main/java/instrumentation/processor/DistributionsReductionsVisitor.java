package instrumentation.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import instrumentation.Constants;

public class DistributionsReductionsVisitor implements ElementVisitor<Object, Object>{

	static Map<String,String> distributions = new HashMap<String,String>();
	static Map<String,String> reductions = new HashMap<String,String>();
	
	private String name;
	private String superclass;
	private List<String> interfaces;
	
	/*public static String to_String(Map<?,?> s) {
		String out="[";
		for (Object object : s.keySet()) {
			out+=object.toString()+", ";
		}
		if(out.length()>1)
			out=out.substring(0,out.length()-2);
		return out+"]";
	}*/
	
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
		this.name = e.getQualifiedName().toString();
		this.superclass = e.getSuperclass().toString();
		
		if (this.superclass.startsWith(Constants.ABSTRACT_DISTRIBUTION))
			distributions.put(this.name, this.superclass);
		
		if (this.superclass.startsWith(Constants.ABSTRACT_REDUCTION))
			reductions.put(this.name, this.superclass);
		
		if(reductions.containsKey(this.superclass)){
			reductions.put(this.name, reductions.get(this.superclass));
		}
		
		if(distributions.containsKey(this.superclass)){
			distributions.put(this.name, distributions.get(this.superclass));
		}
		
		this.interfaces = new ArrayList<String>(e.getInterfaces().size());
		for (TypeMirror t : e.getInterfaces()) {
			String type=t.toString();
			if(type.startsWith(Constants.REDUCTION))
				reductions.put(this.name,type);
			if(type.startsWith(Constants.DISTRIBUTION))
				distributions.put(this.name,type);
			
			this.interfaces.add(type);
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

	public static int getNReds() {
		return reductions.size();
	}

	public static int getNDists() {
		return distributions.size();
	}
}
