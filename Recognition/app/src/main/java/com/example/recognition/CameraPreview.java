package com.example.recognition;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder mSurfaceHolder;
    public String name;
    public String count;
    Camera mCamera;
    Context context;
    public CameraPreview(Context context) {
        super(context);
        this.context=context;
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        EditText nametext = new EditText(context);
        new AlertDialog.Builder(context).setTitle("name").setIcon(
                android.R.drawable.ic_dialog_info).setView(
                nametext).setPositiveButton("確認", null)
                .setNegativeButton("取消", null).show();
        name=nametext.getText().toString();
        Log.d(name,name);
        try {
            int openCameraType = Camera.CameraInfo.CAMERA_FACING_BACK;
            if (openCameraType <= Camera.getNumberOfCameras()) {
                mCamera = Camera.open(openCameraType);
                mCamera.setPreviewDisplay(holder);
            } else {
                Log.d("CameraSample", "cannot bind camera.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        msetPreviewSize(width, height);
        mCamera.startPreview();
    }

   protected void msetPreviewSize(int width, int height) {
        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> supported = params.getSupportedPreviewSizes();
        if (supported != null) {
            for (Camera.Size size : supported) {
                //Log.d("tiaoshi", "" + size.width + size.height);
               if (size.width <= width && size.height<= height) {
                   params.setPictureSize(320,240);
                   params.setPreviewSize(320,240);

                    mCamera.setParameters(params);
                    break;
                }
            }
        }
    }

    private Camera.ShutterCallback mShutterListener =
            new Camera.ShutterCallback() {
                public void onShutter() {
                    // TODO Auto-generated method stub
                }
            };
    public int c=0;
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mCamera != null) {
                mCamera.takePicture(mShutterListener, null, mPictureListener);
                c++;
                count=c+"枚";
                Toast.makeText(context,count, Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }


    private Camera.PictureCallback mPictureListener =
            new Camera.PictureCallback() {
                public void onPictureTaken(byte[] data, Camera camera) {
                    if (data != null) {
                        try {
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss ");
                            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                            String realtime = formatter.format(curDate);
                            String fullname=name+c;
                            File imageFile = new File("/sdcard/"+fullname+".jpg");
                            FileOutputStream outStream = new FileOutputStream(imageFile);
                            outStream.write(data);
                            outStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        camera.startPreview();
                    }
                }
            };

}


/*
    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        public void onPictureTaken(final byte[] imageData, Camera c) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        uploadPhoto(imageData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            mCamera.startPreview();
        }


    };
*/


  /*  private void showDialog(String mess)
    {  final Activity mActivity = (Activity)this.getContext();
        new AlertDialog.Builder(mActivity).setTitle("Message")
                .setMessage(mess)
                .setNegativeButton("确定",new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                })
                .show();
    }
    }
}
*/