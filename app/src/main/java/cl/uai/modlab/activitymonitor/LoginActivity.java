package cl.uai.modlab.activitymonitor;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
        final EditText editText = (EditText)findViewById(R.id.user_input);
        String previousId = Remember.getString(ActivityMonitorConstants.PREFERENCE_USER_ID, "");
        assert editText != null;
        editText.setText(previousId);
        final Button saveButton = (Button) findViewById(R.id.save_button);
        assert saveButton != null;
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = editText.getText().toString().trim();
                if (userId.length() > 0) {
                    Remember.putString(ActivityMonitorConstants.PREFERENCE_USER_ID, userId);
                    Intent startNotificationServiceIntent = new Intent(LoginActivity.this, NotificationService.class);
                    startService(startNotificationServiceIntent);
                    Intent startContextCaptureServiceIntent = new Intent(LoginActivity.this, ContextCaptureService.class);
                    startService(startContextCaptureServiceIntent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, R.string.login_id_required, Toast.LENGTH_LONG).show();
                }
            }
        });
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
        }
    }


}
