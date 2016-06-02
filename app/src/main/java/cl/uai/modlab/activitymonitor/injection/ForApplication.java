package cl.uai.modlab.activitymonitor.injection;

/**
 * Based on https://raw.githubusercontent.com/square/dagger/master/examples/android-simple/src/main/java/com/example/dagger/simple/ForApplication.java
 * Created by gohucan on 17-04-16.
 */
import java.lang.annotation.Retention;
import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Qualifier @Retention(RUNTIME)
public @interface ForApplication {
}
