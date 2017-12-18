package srx.awesome.code.security.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})//作用域
@Retention(RetentionPolicy.RUNTIME)//作用时间
@Constraint(validatedBy = MyConstraintValidator.class)
public @interface MyConstraint {

    String message() default "MyConstraint";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
