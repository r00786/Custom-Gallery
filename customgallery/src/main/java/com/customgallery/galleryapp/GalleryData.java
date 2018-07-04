package com.customgallery.galleryapp;

import java.io.File;
import java.util.Date;

public class GalleryData {
    private Date date;
    private File file;
    private boolean header;

    public GalleryData() {
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isHeader() {
        return header;
    }

    public void setHeader(boolean header) {
        this.header = header;
    }
}
