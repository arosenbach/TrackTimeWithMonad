package logmonad.aspect;

import com.google.common.base.Stopwatch;
import logmonad.Timed;
import logmonad.TimerCollector;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class TrackTimeAspect {

    @Around("@annotation(trackTimeAnnotation)")
    public <A> Timed<A> advice(ProceedingJoinPoint proceedingJoinPoint, TrackTime trackTimeAnnotation) throws Throwable{
        final Stopwatch stopwatch = Stopwatch.createStarted();
        final A value = (A) proceedingJoinPoint.proceed();
        stopwatch.stop();
        return Timed.of(TimerCollector.of(trackTimeAnnotation.value(), stopwatch), value);
    }
}
