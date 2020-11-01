package com.jxd.pdfscanner.preview;

import java.io.File;

public class PhotoBean {
    private String photoName;//照片名称
    private boolean photoCheckStatus;//照片选中状态
    private File photoFile;//照片对象

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public boolean isPhotoCheckStatus() {
        return photoCheckStatus;
    }

    public void setPhotoCheckStatus(boolean photoCheckStatus) {
        this.photoCheckStatus = photoCheckStatus;
    }

    public File getPhotoFile() {
        return photoFile;
    }

    public void setPhotoFile(File photoFile) {
        this.photoFile = photoFile;
    }

    @Override
    public String toString() {
        return "PhotoBean{" +
                "photoName='" + photoName + '\'' +
                ", photoCheckStatus=" + photoCheckStatus +
                ", photoFile=" + photoFile.getAbsolutePath() +
                '}';
    }
}
