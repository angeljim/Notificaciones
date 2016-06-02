package cl.uai.modlab.activitymonitor;

import android.*;

/**
 * Created by gohucan on 31-05-16.
 */
public class ActivityMonitorConstants {
    // preferences
    public static final String PREFERENCE_USER_ID = "_user_id";
    public static final String PREFERENCE_USER_ID_DEFAULT = "test-user-id";
    public static final String PREFERENCE_DEVICE_ID = "_device_id";
    public static final String PREFERENCE_DEVICE_ID_DEFAULT = "test-device-id";
    // intent actions
    public static final String ACTION_CONTEXT_CAPTURE = "cl.uai.modlab.activitymonitor.intents.action.CONTEXT_CAPTURE";
    //extras for intents
    public static final String EXTRAS_NOTIFICATION_ID = "_notification_id";
    public static final String EXTRAS_ACTION_TYPE = "_action_type";
    public static final String EXTRAS_ACTION_TYPE_DISMISS = "dismiss";
    public static final String EXTRAS_ACTION_TYPE_VIEW = "view";
    public static final String EXTRAS_FEELING = "_feeling";
    // permissions
    public static final int PERMISSIONS_REQUEST = 1;
}
