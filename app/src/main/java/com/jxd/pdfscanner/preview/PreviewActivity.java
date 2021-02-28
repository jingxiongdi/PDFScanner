package com.jxd.pdfscanner.preview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jxd.pdfscanner.R;
import com.jxd.pdfscanner.util.JXDLog;
import com.jxd.pdfscanner.util.ToastUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PreviewActivity extends AppCompatActivity {
    private List<File> photoFolderList = null;
    private Spinner spinner = null;
    private Button createPDFBtn = null;
    private RecyclerView recyclerView = null;
    private Button selectAllBtn = null;
    private Button selectReverseBtn = null;
    private RecycleviewAdapter recycleviewAdapter = null;
    private ImageView pdfImg = null;

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
        Collections.sort(photoFolderList, (f1, f2) -> (int)(f1.lastModified() - f2.lastModified()));


        setViews();

    }

    private void setViews() {
        pdfImg = findViewById(R.id.preview_pic_pdf);
        pdfImg.setVisibility(View.GONE);

        selectAllBtn = findViewById(R.id.select_all_btn);
        selectAllBtn.setOnClickListener(v -> selectAll());

        selectReverseBtn = findViewById(R.id.select_reverse_btn);
        selectReverseBtn.setOnClickListener(v -> selectReverse());

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
                File folder = photoFolderList.get(i);
                List<PhotoBean> beanList = new LinkedList<>();
                File[] phtotFiles = folder.listFiles();
                for(File file:phtotFiles){
                    PhotoBean photoBean = new PhotoBean();
                    photoBean.setPhotoFile(file);
                    photoBean.setPhotoName(file.getName());
                    photoBean.setPhotoCheckStatus(false);
                    beanList.add(photoBean);
                }
                recycleviewAdapter.refreshData(beanList);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        createPDFBtn = findViewById(R.id.create_pdf_btn);
        createPDFBtn.setOnClickListener(view -> {
            pdfImg.setVisibility(View.VISIBLE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    createPDFFuction();
                }
            }).start();

        });

        recyclerView = findViewById(R.id.recycle_view);



    }

    private void selectReverse() {
        recycleviewAdapter.selectReverse();
    }

    private void selectAll() {
        recycleviewAdapter.selectAll();
    }

    private void createPDFFuction() {
        List<PhotoBean> photoBeanList = recycleviewAdapter.getSelectedPhotoList();
        JXDLog.d("createPDFFuction===="+photoBeanList.size());
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(768, 1280, photoBeanList.size()).create();
        PdfDocument document = new PdfDocument();
        for(PhotoBean bean:photoBeanList){
            Bitmap bitmap = BitmapFactory.decodeFile(bean.getPhotoFile().getAbsolutePath());
            runOnUiThread(() -> pdfImg.setImageBitmap(bitmap));

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                PdfDocument.Page page = document.startPage(pageInfo);
                View content = pdfImg.getRootView();
                content.draw(page.getCanvas());
                document.finishPage(page);
            });

        }

        // add more pages
        File file = new File(getExternalCacheDir()+File.separator+"a.pdf");
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            // write the document content
            document.writeTo(outputStream);
            // clse the document
            document.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JXDLog.d("createPDFFuction====succ");

        runOnUiThread(() -> pdfImg.setVisibility(View.GONE));
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
        recycleviewAdapter = new RecycleviewAdapter(beanList,PreviewActivity.this);
        recyclerView.setAdapter(recycleviewAdapter);
        JXDLog.d("setAdapter====");
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }
}
