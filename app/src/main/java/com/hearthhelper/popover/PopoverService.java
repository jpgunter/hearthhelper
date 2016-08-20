package com.hearthhelper.popover;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.Image;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.hearthhelper.MatchRecordViewController;
import com.hearthhelper.R;

import java.util.zip.Inflater;

/**
 * Created by gunterj on 8/19/16.
 */
public class PopoverService extends Service {
    private WindowManager windowManager;
    private WindowManager.LayoutParams popoverParams;
    private int windowHeight;
    private int windowWidth;
    private ImageView popoverView;

    private boolean popoverShowing = false;
    private View matchRecordView;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();

        this.windowManager = (WindowManager) getSystemService(Service.WINDOW_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        popoverView = (ImageView) new ImageView(getApplicationContext());
        popoverView.setImageResource(R.drawable.popover);

        showPopover();

        return START_STICKY;
    }

    private void showPopover() {
        this.popoverParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displaymetrics);
        this.windowHeight = displaymetrics.heightPixels;
        this.windowWidth = displaymetrics.widthPixels;

        popoverParams.gravity = Gravity.TOP | Gravity.LEFT;
        popoverParams.x = 0;
        popoverParams.y = windowHeight / 2 - 30;

        windowManager.addView(popoverView, popoverParams);
        setPopoverTouchListener();
    }

    private void setPopoverTouchListener() {
        popoverView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN :
                        if(popoverShowing){
                            removeMatchRecordView();
                        } else {
                            addMatchRecordView();
                        }
                        popoverShowing = !popoverShowing;
                        break;
                }
                return false;
            }
        });
    }

    private void addMatchRecordView() {
        WindowManager.LayoutParams matchParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.OPAQUE);
        matchParams.gravity = Gravity.TOP | Gravity.LEFT;
        matchParams.x = 100;
        matchParams.y = windowHeight / 2 - 100;

        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        if(matchRecordView == null) {
            matchRecordView = inflater.inflate(R.layout.match_record, null);
            new MatchRecordViewController(matchRecordView);
        }
        windowManager.addView(matchRecordView, matchParams);
    }

    private void removeMatchRecordView(){
        windowManager.removeView(matchRecordView);
    }
}
