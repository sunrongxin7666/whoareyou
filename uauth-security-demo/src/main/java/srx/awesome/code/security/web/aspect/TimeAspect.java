package srx.awesome.code.security.web.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Date;

//@Aspect
//@Component
public class TimeAspect {

    @Around("execution(* srx.awesome.code.security.web.controller.UseController.*(..))")
    public Object handleControllerMethod(ProceedingJoinPoint point) throws Throwable {
        System.out.println("aspect start:");
        long time = new Date().getTime();

        Object[] args = point.getArgs();
        for(Object arg : args){
            System.out.println("arg : "+arg);
        }
        Object proceed = point.proceed();


        System.out.println("aspect run time:"+(new Date().getTime()-time));
        System.out.println("aspect Finish");
        return proceed;
    }
}
