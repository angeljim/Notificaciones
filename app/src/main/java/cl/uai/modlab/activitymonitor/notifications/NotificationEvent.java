package cl.uai.modlab.activitymonitor.notifications;

/**
 * Created by gohucan on 17-04-16.
 */
public class NotificationEvent {

    private int identifier;
    private String title;
    private String message;

    public int getIdentifier() {
        return identifier;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
