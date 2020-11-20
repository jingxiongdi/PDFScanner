package com.jxd.pdfscanner.preview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.jxd.pdfscanner.R;
import com.jxd.pdfscanner.util.Constants;
import com.jxd.pdfscanner.util.JXDLog;
import com.yalantis.ucrop.UCrop;

import java.io.File;

public class PhotoDetailActivity extends AppCompatActivity {
    private ImageView imageView = null;
    private  File photoFile = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        setViews();

        photoFile = (File) getIntent().getSerializableExtra(Constants.PHOTO_DETAIL_PATH);
        JXDLog.d(photoFile.getAbsolutePath());

        //需要裁剪的图片路径
        Uri sourceUri = Uri.fromFile(photoFile);

        //裁剪完毕的图片存放路径
        Uri destinationUri = Uri.fromFile(new File(photoFile.getParent() , photoFile.getName().replace(".jpg","_new.jpg")));

        UCrop.of(sourceUri, destinationUri) //定义路径
                .withAspectRatio(3, 4) //定义裁剪比例 4:3 ， 16:9
                .withMaxResultSize(1080, 1920) //定义裁剪图片宽高最大值
                .start(this);
    }

    private void setViews() {
        imageView = findViewById(R.id.photo_detail_img);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //裁剪成功后调用
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            //设置裁剪完成后的图片显示
            imageView.setImageURI(resultUri);
            //删除原文件
            photoFile.delete();
            //出错时进入该分支
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }

}
