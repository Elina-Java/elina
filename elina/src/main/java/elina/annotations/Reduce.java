package elina.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
public @interface Reduce {
	ReduceOps value() default ReduceOps.SUM;
}
