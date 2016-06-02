package cl.uai.modlab.activitymonitor.notifications;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by gohucan on 16-04-16.
 */
@Singleton
public final class NotificationGenerator {

    private EventBus eventBus;

    // based on I’ll be there for you: Quantifying Attentiveness towards Mobile Messaging
    // online at http://pielot.org/pubs/Dingler2015-MobileHCI-Attentiveness.pdf
    private double[] meanTimes = {24.76796407,
            61.91991018, 123.8398204, 247.6796407, 495.3592814, 330.239521, 82.55988024,
            41.27994012, 27.51996008, 19.81437126, 16.24128792, 14.56939063, 14.15312233,
            15.47997754, 15.01088732, 16.79184005, 16.24128792, 14.15312233, 15.97933166,
            14.15312233, 16.79184005, 15.01088732, 14.15312233, 15.97933166};

    @Inject
    public NotificationGenerator() {
        this.eventBus = EventBus.getDefault();
    }

    public void generate() {
        // step 1: get the mean according to time of day
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        double mean = meanTimes[hour]; // in minutes
        // step 2: get the waiting time using the poisson fn
        long interval = (long)poissonRandomInterarrivalDelay(mean*1000.0); //60*
        // step 3: set the timer for the next time
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                NotificationEvent event = new NotificationEvent();
                event.setTitle("Activity Monitor");
                event.setMessage("Es tiempo de notificación");
                NotificationGenerator.this.eventBus.post(event);
            }
        };
        timer.schedule(timerTask, interval);
    }

    // Note L == 1 / lambda
    private double poissonRandomInterarrivalDelay(double L) {
        return Math.log(Math.random())/-L;
    }
}
