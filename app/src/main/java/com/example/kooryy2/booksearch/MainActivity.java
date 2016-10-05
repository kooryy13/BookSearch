package com.example.kooryy2.booksearch;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    // Tao ten thu muc chua anh va video
    private static final String IMAGE_DIRECTORY_NAME = "ScreenShoot Android";
    private Uri fileUri; //file url store image & video


    private Button btnImage, btnVideo;
    private ImageView imageView;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imgPreview);
        videoView = (VideoView) findViewById(R.id.videoPreview);

        btnImage = (Button) findViewById(R.id.btnTakeScreenShoot);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();
            }
        });

        btnVideo = (Button) findViewById(R.id.btnVideoRecord);
        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureVideo();
            }
        });
        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }
    }

    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = this.getOutPutMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    private void captureVideo() {
        Intent intent1 = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        fileUri = this.getOutPutMediaFileUri(MEDIA_TYPE_VIDEO);
        //Set độ phân giải cho video 1 là chất lượng cao
        intent1.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent1.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent1, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                displayImage();
            } else if (resultCode == RESULT_CANCELED) {
                // Hủy Việc Chụp Ảnh
                Toast.makeText(getApplicationContext(), "Hủy Việc Chụp Ảnh", Toast.LENGTH_SHORT).show();
            } else {
                // Failed to capture image
                Toast.makeText(getApplicationContext(), "Failed to Take A PhoTo", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // video successfully recorded
                // preview the recorded video
                previewVideo();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void previewVideo() {
        try {
            imageView.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoPath(fileUri.getPath());
            videoView.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayImage() {
        try {
            // Hide video preview
            videoView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);

            BitmapFactory.Options options = new BitmapFactory.Options();
            // Giảm chất lượng ảnh tránh tràn bộ nhớ
            options.inSampleSize = 8;
            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
            imageView.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /*
    Hàm này để chứ file url tránh nullpoiter khi chụp xong
     */

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    /*
            ham nay de tao và nhận file từ thẻ SD
             */
    public Uri getOutPutMediaFileUri(int type) {
        return Uri.fromFile(getOutPutMediaFile(type));
    }

    /*
    trả về image , video
     */
    private static File getOutPutMediaFile(int type) {
        //vị trí của External SD card
        File mediaStoreDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);

        // Create Media File Name
        String timeStamp = new SimpleDateFormat("yyyyMMđd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStoreDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStoreDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }
}