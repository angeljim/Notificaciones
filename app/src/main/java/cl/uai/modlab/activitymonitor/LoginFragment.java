package cl.uai.modlab.activitymonitor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tumblr.remember.Remember;

import cl.uai.modlab.activitymonitor.services.ContextCaptureService;
import cl.uai.modlab.activitymonitor.services.NotificationService;


public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final EditText userEmailEditText = (EditText)getActivity().findViewById(R.id.email_input);
        String previousEmail = Remember.getString(ActivityMonitorConstants.PREFERENCE_USER_EMAIL, "");
        assert userEmailEditText != null;
        userEmailEditText.setText(previousEmail);
        final EditText userIdEditText = (EditText)getActivity().findViewById(R.id.id_input);
        String previousId = Remember.getString(ActivityMonitorConstants.PREFERENCE_USER_ID, "+569");
        assert userIdEditText != null;
        userIdEditText.setText(previousId);
        final Button saveButton = (Button) getActivity().findViewById(R.id.save_button);
        assert saveButton != null;
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = userIdEditText.getText().toString().trim();
                String userEmail = userEmailEditText.getText().toString().trim();
                if (isValidMail(userEmail) && isValidMobile(userId)) {
                    Remember.putString(ActivityMonitorConstants.PREFERENCE_USER_ID, userId);
                    Remember.putString(ActivityMonitorConstants.PREFERENCE_USER_EMAIL, userEmail);
                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.login_fragment, new ThankYouFragment());
                    ft.commit();
                } else {
                    if (!isValidMobile(userId))
                        Toast.makeText(getActivity(), R.string.login_id_required, Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getActivity(), R.string.login_email_required, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

}
