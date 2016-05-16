package angtrim.com.sampleapp;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import angtrim.com.fivestarslibrary.FiveStarsDialog;
import angtrim.com.fivestarslibrary.NegativeReviewListener;
import angtrim.com.fivestarslibrary.ReviewListener;

/**
 * Created by hesk on 16/5/16.
 */
public class Openarticles extends AppCompatActivity implements NegativeReviewListener, ReviewListener {
    private static final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gole);
        /*

        FiveStarsDialog fiveStarsDialog = new FiveStarsDialog(this,"angelo.gallarello@gmail.com");
        fiveStarsDialog.setRateText("Your custom text")
                .setTitle("Your custom title")
                .setForceMode(false)
                .setUpperBound(2)
                .setNegativeReviewListener(this)
                .setReviewListener(this)
                .showAfter(0);

        */

        FiveStarsDialog fiveStarsDialog = new FiveStarsDialog(this);
        fiveStarsDialog.setRateText("Your custom text")
                .setPolicyTrackVersionCode(false, BuildConfig.VERSION_CODE)
                .setApplicationId(Build.MODEL)
                .setTitle("Rating this app")
                .setForceMode(false)
                .useTranslation("HYPEBEAST")
                .setUpperBound(4)
                .setEnableAppStoreRating()
                .disableNotNowButton()
                .setNegativeReviewListener(this)
                .setReviewListener(this)
                .showAfter(3);

        TextView diaplay = (TextView) findViewById(R.id.view_num_display);
        diaplay.setText(fiveStarsDialog.getNOA() + "");

    }


    @Override
    public void onNegativeReview(int stars) {
        Log.d(TAG, "Negative review " + stars);
        Toast.makeText(this, "You gave my app a bad review, bas***!!11!!!", Toast.LENGTH_LONG);
    }

    @Override
    public void onReview(int stars) {
        Log.d(TAG, "Review " + stars);
    }
}
