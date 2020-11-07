package com.jxd.pdfscanner.preview;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.jxd.pdfscanner.R;
import com.jxd.pdfscanner.util.Constants;
import com.jxd.pdfscanner.util.JXDLog;

import java.io.File;

public class PhotoDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        File photoFile = (File) getIntent().getSerializableExtra(Constants.PHOTO_DETAIL_PATH);
        JXDLog.d(photoFile.getAbsolutePath());
    }
}
