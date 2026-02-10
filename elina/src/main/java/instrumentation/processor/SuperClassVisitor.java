package instrumentation.processor;

import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.IntersectionType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.type.UnionType;
import javax.lang.model.type.WildcardType;

public class SuperClassVisitor implements TypeVisitor<Object, Object> {

	@Override
	public Object visit(TypeMirror t, Object p) {

		return null;
	}

	@Override
	public Object visit(TypeMirror t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitPrimitive(PrimitiveType t, Object p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitNull(NullType t, Object p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitArray(ArrayType t, Object p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitDeclared(DeclaredType t, Object p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitError(ErrorType t, Object p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTypeVariable(TypeVariable t, Object p) {
		System.out.println("--_ type var --- " + t);
		return null;
	}

	@Override
	public Object visitWildcard(WildcardType t, Object p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitExecutable(ExecutableType t, Object p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitNoType(NoType t, Object p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitUnknown(TypeMirror t, Object p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitUnion(UnionType t, Object p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitIntersection(IntersectionType t, Object p) {
		// TODO Auto-generated method stub
		return null;
	}

}
