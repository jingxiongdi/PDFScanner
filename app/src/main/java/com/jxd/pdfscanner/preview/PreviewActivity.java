package com.jxd.pdfscanner.preview;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jxd.pdfscanner.R;
import com.jxd.pdfscanner.util.JXDLog;
import com.jxd.pdfscanner.util.ToastUtil;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class PreviewActivity extends AppCompatActivity {
    private List<File> photoFolderList = null;
    private Spinner spinner = null;
    private Button createPDFBtn = null;
    private RecyclerView recyclerView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        File folder = new File(Environment.getExternalStorageDirectory()+File.separator+"PDFScanner"+File.separator+"Photo");
        if(!folder.exists() || folder.listFiles()==null){
            //创建根目录文件夹
            ToastUtil.getInstance().showToast("您本地还没有可用于生成pdf的图片！");
            return;
        }


        File[] photoFolderArr = folder.listFiles();
        photoFolderList = Arrays.asList(photoFolderArr);
        Collections.sort(photoFolderList, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                return (int)(f2.lastModified() - f1.lastModified());
            }
        });


        setViews();

    }

    private void setViews() {
        spinner = findViewById(R.id.spinner);
        LinkedList<String> linkedList = new LinkedList<>();
        for (File file:photoFolderList){
            linkedList.add(file.getName());
        }
        // 建立Adapter并且绑定数据源
        ArrayAdapter<String> _Adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, linkedList);
        //绑定 Adapter到控件
        spinner.setAdapter(_Adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                JXDLog.d("spinner item "+i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        createPDFBtn = findViewById(R.id.create_pdf_btn);
        createPDFBtn.setOnClickListener(view -> {
            createPDFFuction();
        });

        recyclerView = findViewById(R.id.recycle_view);

    }

    private void createPDFFuction() {
        JXDLog.d("createPDFFuction====");
    }

    private void initData() {
        if(photoFolderList.size()<=0){
            ToastUtil.getInstance().showToast("当前文件夹没有文件");
            return;
        }
        File folder = photoFolderList.get(0);
        List<PhotoBean> beanList = new LinkedList<>();
        File[] phtotFiles = folder.listFiles();
        for(File file:phtotFiles){
            PhotoBean photoBean = new PhotoBean();
            photoBean.setPhotoFile(file);
            photoBean.setPhotoName(file.getName());
            photoBean.setPhotoCheckStatus(false);
            beanList.add(photoBean);
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        RecycleviewAdapter recycleviewAdapter = new RecycleviewAdapter(beanList,PreviewActivity.this);
        recyclerView.setAdapter(recycleviewAdapter);
        JXDLog.d("setAdapter====");
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }
}
