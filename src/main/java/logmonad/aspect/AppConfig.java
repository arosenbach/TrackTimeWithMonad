package logmonad.aspect;

import logmonad.example.ServiceA;
import logmonad.example.ServiceB;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAspectJAutoProxy
@Import({
        TrackTimeAspect.class,
        ServiceA.class,
        ServiceB.class
})
public class AppConfig {

}
