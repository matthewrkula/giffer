package com.mattkula.giffer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.ImageView;

import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;
import com.mattkula.giffer.activities.SearchActivity;

/**
 * Created by matt on 12/4/14.
 */
public class Utils {

    public static final int NOTIFICATION_ID = 1;

    public static void showNotification(Context context) {
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, SearchActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context)
                .setContentIntent(pendingIntent)
                .setContentTitle("Search for gifs")
                .setContentText("Click here to find a gif to share")
                .setSmallIcon(R.drawable.ic_launcher);

        Notification n;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            n = builder.build();
        } else {
            n = builder.getNotification();
        }

        n.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

        manager.notify(NOTIFICATION_ID, n);
    }

    public static void hideNotification(Context context) {
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(NOTIFICATION_ID);
    }

    public static void showGiphyLogo(Context context, ImageView imageView, boolean lightBackground) {
        Ion.with(context)
                .load("file://android_asset/" + (lightBackground ? "giphy_logo_white.gif" : "giphy_logo.gif"))
                .withBitmap()
                .animateGif(AnimateGifMode.ANIMATE_ONCE)
                .error(context.getResources().getDrawable(android.R.color.holo_red_dark))
                .intoImageView(imageView);
    }
}
