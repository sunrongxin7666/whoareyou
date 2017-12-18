package srx.awesome.code.security.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MyConstraintValidator implements ConstraintValidator<MyConstraint,Object> {
    @Override
    public void initialize(MyConstraint myConstraint) {
        System.out.println("my constraint init.");
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        System.out.println(o.toString()+" interesting!");

        return false;
    }
}
