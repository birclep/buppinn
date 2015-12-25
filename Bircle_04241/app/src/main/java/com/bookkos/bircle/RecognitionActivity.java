package com.bookkos.bircle;

import android.app.Activity;
import android.content.Context;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.LinearLayout;

import com.bookkos.bircle.camera.CameraManager;

public class RecognitionActivity extends Activity {
    private CameraManager cameraManager;
    private ViewfinderView viewfinderView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition);


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Context context = this;
        CameraPreview cameraPreview = new CameraPreview(this);
        this.setContentView(cameraPreview, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));



    }

}
