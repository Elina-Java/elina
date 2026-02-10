package instrumentation.definitions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

//@Target({ElementType.PARAMETER,ElementType.METHOD})
@Target({ElementType.PARAMETER})
public @interface DistributionPolicy {
	Class<? extends core.collective.Distribution<?>> distribution();
	String[] params() default {};
}
