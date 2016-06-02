package cl.uai.modlab.activitymonitor;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.TextView;

import com.github.pwittchen.swipe.library.Swipe;
import com.github.pwittchen.swipe.library.SwipeEvent;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.ubhave.sensormanager.sensors.SensorUtils;

import java.util.Arrays;
import java.util.Random;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class SurveyActivity extends AppCompatActivity implements EmojiconGridFragment.OnEmojiconClickedListener{

    private static final SwipeEvent[] SWIPE_EVENTS = {SwipeEvent.SWIPED_DOWN, SwipeEvent.SWIPED_LEFT, SwipeEvent.SWIPED_UP, SwipeEvent.SWIPED_RIGHT};

    private Random rnd = new Random();

    private Swipe swipe;
    private Subscription subscription;

    private EmojiconTextView emojiconTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        emojiconTextView = (EmojiconTextView)findViewById(R.id.survey_emojicon);
        assert emojiconTextView != null;
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, r.getDisplayMetrics());
        emojiconTextView.setEmojiconSize((int)Math.floor(px));

        final int index = rnd.nextInt(4);
        swipe = new Swipe();
        subscription = swipe.observe()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SwipeEvent>() {
                    @Override public void call(final SwipeEvent swipeEvent) {
                        if (swipeEvent == SWIPE_EVENTS[index]) {
                            int id = -1;
                            Intent intent = SurveyActivity.this.getIntent();
                            if (intent != null) {
                                id = intent.getIntExtra(ActivityMonitorConstants.EXTRAS_NOTIFICATION_ID, id);
                            }
                            Intent viewIntent = new Intent(SensorUtils.INTERACTION_ACTION);
                            viewIntent.putExtra(ActivityMonitorConstants.EXTRAS_NOTIFICATION_ID, id);
                            viewIntent.putExtra(ActivityMonitorConstants.EXTRAS_FEELING, emojiconTextView.getText());
                            viewIntent.putExtra(ActivityMonitorConstants.EXTRAS_ACTION_TYPE, ActivityMonitorConstants.EXTRAS_ACTION_TYPE_VIEW);
                            SurveyActivity.this.sendBroadcast(viewIntent);
                            SurveyActivity.this.finish();
                        }
                    }
                });

        TextView hint = (TextView) findViewById(R.id.survey_swipe_text);
        assert hint != null;
        String[] hints = getResources().getStringArray(R.array.swipe_hints);
        hint.setText(hints[index]);
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        emojiconTextView.setText(emojicon.getEmoji());
    }

    @Override public boolean dispatchTouchEvent(MotionEvent event) {
        swipe.dispatchTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    @Override protected void onPause() {
        super.onPause();
        safelyUnsubscribe(subscription);
    }

    private void safelyUnsubscribe(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
