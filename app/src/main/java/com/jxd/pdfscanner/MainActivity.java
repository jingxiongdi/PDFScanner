package com.jxd.pdfscanner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.jxd.pdfscanner.util.JXDLog;
import com.jxd.pdfscanner.util.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    private String[] permissionArr = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private static final int PERMISSION = 0;
    private TextureView textureView = null;
    private CaptureRequest.Builder mPreviewBuilder;
    private Handler mHandler;
    private HandlerThread mThreadHandler;

    private CameraCaptureSession captureSession = null;

    private Button takePhotoBtn = null;
    private ImageReader imageReader = null;
    private String currentFolderName = String.valueOf(System.currentTimeMillis());
    private static final SparseIntArray ORIENTATION = new SparseIntArray();

    static {
        ORIENTATION.append(Surface.ROTATION_0, 90);
        ORIENTATION.append(Surface.ROTATION_90, 0);
        ORIENTATION.append(Surface.ROTATION_180, 270);
        ORIENTATION.append(Surface.ROTATION_270, 180);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requstPermission();

        setViews();

        initData();
    }

    private void initData() {
        mThreadHandler = new HandlerThread("CAMERA2");
        mThreadHandler.start();
        mHandler = new Handler(mThreadHandler.getLooper());
    }

    private void setViews() {
        textureView = findViewById(R.id.texture);
        textureView.setSurfaceTextureListener(this);
        takePhotoBtn = findViewById(R.id.take_photo_btn);
        takePhotoBtn.setOnClickListener(view -> {
            if(captureSession == null){
                ToastUtil.getInstance().showToast("相机还没准备好");
            }else {
                try {
                    final CaptureRequest.Builder mCaptureBuilder = captureSession.getDevice().createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                    //获取屏幕方向
                    int rotation = getWindowManager().getDefaultDisplay().getRotation();
                    //设置CaptureRequest输出到mImageReader
                    mCaptureBuilder.addTarget(imageReader.getSurface());
                    //设置拍照方向
                    mCaptureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATION.get(rotation));
                    //停止预览
                    captureSession.stopRepeating();
                    //开始拍照，然后回调上面的接口重启预览，因为mCaptureBuilder设置ImageReader作为target，所以会自动回调ImageReader的onImageAvailable()方法保存图片
                    captureSession.capture(mCaptureBuilder.build(), mImageSavedCallback, null);
                    JXDLog.d("capture===>  end");
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    //这个回调接口用于拍照结束时重启预览，因为拍照会导致预览停止
    CameraCaptureSession.CaptureCallback mImageSavedCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            //重启预览
            JXDLog.d("capture===>  restartPreview");
            restartPreview();
        }
    };

    private void restartPreview() {
        try {
            //执行setRepeatingRequest方法就行了，注意mCaptureRequest是之前开启预览设置的请求
            captureSession.setRepeatingRequest(mPreviewBuilder.build(), null, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    protected ImageReader.OnImageAvailableListener onImageAvailableListener = reader -> {
        JXDLog.d("onImageAvailable=== ");
        Image img = reader.acquireLatestImage();
        if (img != null) {
            savePhoto(img);
        }
    };

    private void savePhoto(Image img) {
        JXDLog.d("savePhoto=== ");
        ToastUtil.getInstance().showToast("保存成功");
        new Thread(new ImageSaver(img,System.currentTimeMillis()+"")).start();
    }

    private void requstPermission() {
        requestPermissions(permissionArr, PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        for(int i=0;i<grantResults.length;i++){
//            JXDLog.d("permissions "+permissions[i]+" "+grantResults[i]);
//        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            String[] CameraIdList = cameraManager.getCameraIdList();//获取可用相机列表
            CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(CameraIdList[0]);//获取某个相机(摄像头特性)
            cameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);//检查支持
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requstPermission();
                return;
            }
            cameraManager.openCamera(CameraIdList[0], mCameraDeviceStateCallback, mHandler);
            imageReader = ImageReader.newInstance(960, 1280, ImageFormat.JPEG, 2 /* images buffered */);
            imageReader.setOnImageAvailableListener(onImageAvailableListener, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    private CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice cameraDevice) {
            startPreview(cameraDevice);
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {

        }

        @Override
        public void onError(CameraDevice cameraDevice, int i) {

        }
    };

    private void startPreview(CameraDevice camera) {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            texture.setDefaultBufferSize(textureView.getHeight(),textureView.getWidth());
            Surface surface = new Surface(texture);
            //CameraRequest表示一次捕获请求，用来对z照片的各种参数设置，比如对焦模式、曝光模式等。CameraRequest.Builder用来生成CameraRequest对象
            mPreviewBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            mPreviewBuilder.addTarget(surface);
            camera.createCaptureSession(Arrays.asList(surface,imageReader.getSurface()), mSessionStateCallback, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    //CameraCaptureSession 这个对象控制摄像头的预览或者拍照
    //setRepeatingRequest()开启预览，capture()拍照
    //StateCallback监听CameraCaptureSession的创建
    private CameraCaptureSession.StateCallback mSessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession session) {
            JXDLog.d("相机创建成功！");
            try {
                captureSession = session;
                session.setRepeatingRequest(mPreviewBuilder.build(), null, mHandler);//返回结果
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
            JXDLog.e("相机创建失败！");
        }
    };

    public class ImageSaver implements Runnable {
        private Image mImage;
        private String imgName;
        public ImageSaver(Image image,String imgName) {
            mImage = image;
            this.imgName=imgName;
        }
        @Override
        public void run() {
            //我们可以将这帧数据转成字节数组，类似于Camera1的PreviewCallback回调的预览帧数据
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            File folder = new File(Environment.getExternalStorageDirectory()+File.separator+"PDFScanner"+File.separator+"Photo");
            if(!folder.exists()){
                //创建根目录文件夹
                folder.mkdirs();
            }
            File currentFolder = new File(folder.getAbsolutePath()+File.separator+currentFolderName);
            if(!currentFolder.exists()){
                //创建当前拍照PDF文件夹
                currentFolder.mkdirs();
            }
            File mImageFile = new File(currentFolder.getAbsolutePath()+File.separator+imgName+".jpg");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(mImageFile);
                fos.write(data, 0 ,data.length);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImageFile = null;
                if (fos != null) {
                    try {
                        fos.close();
                        fos = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mImage.close();
            }
        }
    }

}
