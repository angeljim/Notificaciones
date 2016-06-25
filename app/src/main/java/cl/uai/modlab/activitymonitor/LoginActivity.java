package cl.uai.modlab.activitymonitor;

import android.Manifest;
import android.app.ActivityManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.tumblr.remember.Remember;

import cl.uai.modlab.activitymonitor.services.ContextCaptureService;
import cl.uai.modlab.activitymonitor.services.NotificationService;

public class LoginActivity extends AppCompatActivity {

    private final static String[] PERMISSIONS = new String[]{
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.INTERNET,
        Manifest.permission.PROCESS_OUTGOING_CALLS,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.RECEIVE_BOOT_COMPLETED,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        "com.google.android.gms.permission.ACTIVITY_RECOGNITION"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (Remember.getString(ActivityMonitorConstants.PREFERENCE_USER_ID, "").trim().length() == 0)
            ft.replace(R.id.login_fragment, new LoginFragment());
        else
            ft.replace(R.id.login_fragment, new ThankYouFragment());
        ft.commit();

        checkPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case ActivityMonitorConstants.PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    TelephonyManager telephonyManager;
                    telephonyManager  =
                            (TelephonyManager)getSystemService( Context.TELEPHONY_SERVICE );

                    Remember.putString(ActivityMonitorConstants.PREFERENCE_DEVICE_ID, telephonyManager.getDeviceId());
                } else {
                    Remember.putString(ActivityMonitorConstants.PREFERENCE_DEVICE_ID, ActivityMonitorConstants.PREFERENCE_DEVICE_ID_DEFAULT);
                }
                if (!isMyServiceRunning(NotificationService.class)) {
                    Intent startNotificationServiceIntent = new Intent(LoginActivity.this, NotificationService.class);
                    startService(startNotificationServiceIntent);
                }
                if (!isMyServiceRunning(ContextCaptureService.class)) {
                    Intent startContextCaptureServiceIntent = new Intent(LoginActivity.this, ContextCaptureService.class);
                    startService(startContextCaptureServiceIntent);
                }
            }
        }
    }

    protected void checkPermissions() {
        boolean granted = true;
        for(String permission: PERMISSIONS) {
            granted &= (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED);
        }
        if (!granted) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {
                Toast.makeText(this, "Necesitamos permiso para acceder a la información del teléfono", Toast.LENGTH_LONG).show();
                checkPermissions();
            } else {
                ActivityCompat.requestPermissions(this, PERMISSIONS, ActivityMonitorConstants.PERMISSIONS_REQUEST);
            }
        } else {
            if (!isMyServiceRunning(NotificationService.class)) {
                Intent startNotificationServiceIntent = new Intent(LoginActivity.this, NotificationService.class);
                startService(startNotificationServiceIntent);
            }
            if (!isMyServiceRunning(ContextCaptureService.class)) {
                Intent startContextCaptureServiceIntent = new Intent(LoginActivity.this, ContextCaptureService.class);
                startService(startContextCaptureServiceIntent);
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
