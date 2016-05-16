package angtrim.com.fivestarslibrary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;


/**
 * Created by angtrim on 12/09/15.
 */
public class FiveStarsDialog implements DialogInterface.OnClickListener {

    private final static String DEFAULT_TITLE = "title1";
    private final static String DEFAULT_TITLE_2 = "title2";
    private final static String DIALOG_TEXT_1 = "question 1?";
    private final static String DIALOG_TEXT_2 = "question 2?";
    private final static String DEFAULT_POSITIVE = "Rate";
    private final static String DEFAULT_NEGATIVE = "Not Now";
    private final static String DEFAULT_NOTHANKS = "No Thanks";
    private final static String DEFAULT_NEVER = "Never";
    private final static String SP_NUM_OF_ACCESS = "numOfAccess";
    private final static String SP_BUILDCODE = "rating_buildcode";
    private static final String SP_DISABLED = "disabled";
    private static final String TAG = FiveStarsDialog.class.getSimpleName();
    private final Context context;

    private boolean
            isForceMode = false,
            isNotNowEnabled = true,
            track_version = false,
            appstoreRateEnabled = false;


    private SharedPreferences sharedPrefs;
    private String supportEmail;
    private TextView contentTextView;
    private RatingBar ratingBar;
    private String title = null;
    private String title2 = null;
    private String rateText = null;
    private String rate = null;
    private String sure = null;
    private String dialog_content_1 = null;
    private String nothanks = null;
    private String application_id = null;
    private AlertDialog alertDialog;
    private View dialogView;
    private int upperBound = 4, build_code;
    private NegativeReviewListener negativeReviewListener;
    private ReviewListener reviewListener;

    public FiveStarsDialog(Context context) {
        this.context = context;
        sharedPrefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public FiveStarsDialog setApplicationId(String id) {
        application_id = id;
        sharedPrefs = context.getSharedPreferences(id, Context.MODE_PRIVATE);
        return this;
    }

    public FiveStarsDialog setPolicyTrackVersionCode(boolean isTrackVersionCode, int buildcode) {
        track_version = isTrackVersionCode;
        build_code = buildcode;
        return this;
    }

    public FiveStarsDialog useTranslation(String AppName) {
        //the
        title2 = context.getString(R.string.rating_enjoy);
        rateText = context.getString(R.string.rating_invitation);
        dialog_content_1 = String.format(context.getString(R.string.rating_rate_experience), AppName);
        nothanks = context.getString(R.string.rating_refuse);
        sure = context.getString(R.string.rating_positive);
        rate = context.getString(R.string.rating_button_rate);
        title = context.getString(R.string.rating_tell_us);
        return this;
    }

    public FiveStarsDialog disableNotNowButton() {
        isNotNowEnabled = false;
        return this;
    }

    public FiveStarsDialog setEnableAppStoreRating() {
        appstoreRateEnabled = true;
        return this;
    }

    private AlertDialog buildSecondD() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle((title2 == null) ? DEFAULT_TITLE_2 : title2);
        builder.setMessage((rateText == null) ? DIALOG_TEXT_2 : rateText);
        builder.setCancelable(false);
        builder.setPositiveButton((sure == null) ? DEFAULT_POSITIVE : sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openMarket();
            }
        });
        builder.setNegativeButton((nothanks == null) ? DEFAULT_NOTHANKS : nothanks, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }

    private void build() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        dialogView = inflater.inflate(R.layout.stars, null);
        String titleToAdd = (title == null) ? DEFAULT_TITLE : title;
        String textToAdd = (dialog_content_1 == null) ? DIALOG_TEXT_1 : dialog_content_1;
        contentTextView = (TextView) dialogView.findViewById(R.id.text_content);
        contentTextView.setText(textToAdd);
        ratingBar = (RatingBar) dialogView.findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                Log.d(TAG, "Rating changed : " + v);
                if (isForceMode && v >= upperBound) {
                    openMarket();
                    if (reviewListener != null)
                        reviewListener.onReview((int) ratingBar.getRating());
                }
            }
        });


        try {
            LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
        } catch (Exception e) {

        }

        if (isNotNowEnabled) {
            //ratingBar.setCol
            alertDialog = builder.setTitle(titleToAdd)
                    .setView(dialogView)
                    .setNegativeButton(DEFAULT_NEGATIVE, this)
                    .setPositiveButton((rate == null) ? DEFAULT_POSITIVE : rate, this)
                    .setNeutralButton(DEFAULT_NEVER, this)
                    .create();
        } else {
            alertDialog = builder.setTitle(titleToAdd)
                    .setView(dialogView)
                    .setNegativeButton((nothanks == null) ? DEFAULT_NOTHANKS : nothanks, this)
                    .setPositiveButton((rate == null) ? DEFAULT_POSITIVE : rate, this)
                    .create();
        }
    }

    private void disable() {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(SP_DISABLED, true);
        editor.apply();
    }

    private String getAppliedId() {
        return (application_id == null) ? context.getPackageName() : application_id;
    }

    private void openMarket() {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getAppliedId())));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getAppliedId())));
        }
    }

    private void sendEmail() {
        if (supportEmail != null) {
            final Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/email");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{supportEmail});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "App Report (" + context.getPackageName() + ")");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "");
            context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        }
    }

    private void show() {
        boolean disabled = sharedPrefs.getBoolean(SP_DISABLED, false);
        if (!disabled) {
            build();
            alertDialog.show();
        }
    }

    public void showAfter(int numberOfAccess) {
        build();
        SharedPreferences.Editor editor = sharedPrefs.edit();
        int numOfAccess = sharedPrefs.getInt(SP_NUM_OF_ACCESS, 0);
        int buildcodej = sharedPrefs.getInt(SP_BUILDCODE, 0);
        editor.putInt(SP_NUM_OF_ACCESS, numOfAccess + 1);
        if (track_version) {
            if (build_code != buildcodej) {
                editor.putInt(SP_NUM_OF_ACCESS, 0);
            }
        }
        editor.apply();
        if (numOfAccess + 1 >= numberOfAccess) {
            show();
        }
        // debug only
        // show();
        // alertDialog.show();
    }

    public int getNOA() {
        return sharedPrefs.getInt(SP_NUM_OF_ACCESS, 0);
    }


    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == DialogInterface.BUTTON_POSITIVE) {
            if (ratingBar.getRating() < upperBound) {
                if (negativeReviewListener == null) {
                    sendEmail();
                } else {
                    negativeReviewListener.onNegativeReview((int) ratingBar.getRating());
                }
            } else if (!isForceMode) {
                // positive rating
                if (appstoreRateEnabled) {
                    //dialogInterface.dismiss();
                    buildSecondD().show();
                } else {
                    openMarket();
                }
            }
            disable();
            if (reviewListener != null)
                reviewListener.onReview((int) ratingBar.getRating());
        } else if (i == DialogInterface.BUTTON_NEUTRAL) {
            disable();
        } else if (i == DialogInterface.BUTTON_NEGATIVE) {
            if (isNotNowEnabled) {
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putInt(SP_NUM_OF_ACCESS, 0);
                editor.apply();
            } else {
                disable();
            }
        }
        alertDialog.hide();
    }

    public FiveStarsDialog setTitle(String title) {
        this.title = title;
        return this;

    }

    public FiveStarsDialog setSupportEmail(String supportEmail) {
        this.supportEmail = supportEmail;
        return this;
    }

    public FiveStarsDialog setRateText(String rateText) {
        this.rateText = rateText;
        return this;
    }

    /**
     * Set to true if you want to send the user directly to the market
     *
     * @param isForceMode na
     * @return na
     */
    public FiveStarsDialog setForceMode(boolean isForceMode) {
        this.isForceMode = isForceMode;
        return this;
    }

    /**
     * Set the upper bound for the rating.
     * If the rating is greater or equal to of the bound, the market is opened.
     *
     * @param bound the upper bound
     * @return the dialog
     */
    public FiveStarsDialog setUpperBound(int bound) {
        this.upperBound = bound;
        return this;
    }

    /**
     * Set a custom listener if you want to OVERRIDE the default "send email" action when the user gives a negative review
     *
     * @param listener na
     * @return na
     */
    public FiveStarsDialog setNegativeReviewListener(NegativeReviewListener listener) {
        this.negativeReviewListener = listener;
        return this;
    }

    /**
     * Set a listener to get notified when a review (positive or negative) is issued, for example for tracking purposes
     *
     * @param listener na
     * @return na
     */
    public FiveStarsDialog setReviewListener(ReviewListener listener) {
        this.reviewListener = listener;
        return this;
    }

}
