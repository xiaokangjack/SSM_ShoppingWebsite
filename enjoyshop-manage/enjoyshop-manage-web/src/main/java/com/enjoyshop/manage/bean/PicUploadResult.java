package com.enjoyshop.manage.bean;

public class PicUploadResult {
    
    //上传是否成功的判断标识，0-成功，1-失败
    private Integer error;
    
    private String url;
    
    private String width;
    
    private String height;

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }
    
    

}
