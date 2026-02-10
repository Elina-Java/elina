package instrumentation.definitions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;


@Target(ElementType.METHOD)
public @interface ReductionPolicy {
	Class<? extends core.collective.Reduction<?>> reduction();
	String[] params() default {};
	Class<?>[] type() default {};
}
