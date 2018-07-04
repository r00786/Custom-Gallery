package com.customgallery.galleryapp;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class Utility {
    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static void manipulateVisibility(float slideOffset, RecyclerView recyclerView, Toolbar toolbar, ImageView ivCapture, ImageView ivFlashOn, ImageView ivFlashOff, ImageView ivSwitchCamera) {
        recyclerView.setAlpha(slideOffset);
        toolbar.setAlpha(slideOffset);
        ivCapture.setAlpha(1 - slideOffset);
        ivFlashOn.setAlpha(1 - slideOffset);
        ivFlashOff.setAlpha(1 - slideOffset);
        ivSwitchCamera.setAlpha(1 - slideOffset);
        if ((slideOffset) > 0 && recyclerView.getVisibility() == View.INVISIBLE) {
            recyclerView.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);
            ivCapture.setVisibility(View.INVISIBLE);
            ivFlashOn.setVisibility(View.INVISIBLE);
            ivFlashOff.setVisibility(View.INVISIBLE);
            ivSwitchCamera.setVisibility(View.INVISIBLE);
        } else if (recyclerView.getVisibility() == View.VISIBLE && (slideOffset) == 0) {
            recyclerView.setVisibility(View.INVISIBLE);
            toolbar.setVisibility(View.INVISIBLE);
            ivCapture.setVisibility(View.VISIBLE);
            ivFlashOn.setVisibility(View.VISIBLE);
            ivFlashOff.setVisibility(View.VISIBLE);
            ivSwitchCamera.setVisibility(View.VISIBLE);
        }
    }

    public static File writeImage(byte[] jpeg) {
        File dir = new File(Environment.getExternalStorageDirectory(), "/DCIM/Camera");
        if (!dir.exists())
            dir.mkdir();
        File photo = new File(dir, "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmSS", Locale.ENGLISH).format(new Date()) + ".jpg");
        if (photo.exists()) {
            photo.delete();
        }
        try {
            FileOutputStream fos = new FileOutputStream(photo.getPath());
            fos.write(jpeg);
            fos.close();
        } catch (Exception ignored) {
        }
        return photo;
    }

    public static String getDateInString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);

    }

    public static void hideViews(View... views) {
        for (View obj : views) {
            fadeAnimation(obj, false);
        }
    }

    public static void showViews(View... views) {
        for (View obj : views) {
            fadeAnimation(obj, true);
        }
    }

    public static void fadeAnimation(final View view, final boolean fadeIn) {
        Animation fade;
        if (fadeIn) {
            fade = new AlphaAnimation(0, 1);
        } else {
            fade = new AlphaAnimation(1, 0);
        }
        fade.setInterpolator(new DecelerateInterpolator()); //add this
        fade.setDuration(500);
        fade.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!fadeIn) {
                    view.setVisibility(View.GONE);
                } else {
                    view.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fade.setFillAfter(true);
        view.startAnimation(fade);
    }
}
