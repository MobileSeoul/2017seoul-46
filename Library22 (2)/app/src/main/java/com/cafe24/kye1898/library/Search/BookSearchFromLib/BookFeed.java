package com.cafe24.kye1898.library.Search.BookSearchFromLib;

import java.io.Serializable;

public class BookFeed implements Serializable {

    private String manage_code;
    private String title_info;
    private String author_info;
    private String pub_info;
    private String manage_name;
    private String detailLink;
    private String id;

    public BookFeed() {
    }

    public BookFeed(String manage_code, String title_info, String author_info, String pub_info, String manage_name,
                    String detailLink, String id) {
        this.manage_code = manage_code;
        this.title_info = title_info;
        this.author_info = author_info;
        this.pub_info = pub_info;
        this.manage_name = manage_name;
        this.detailLink = detailLink;
        this.id = id;
    }

    public String getManage_code() {
        return manage_code;
    }

    public String getTitle_info() {
        return title_info;
    }

    public String getAuthor_info() {
        return author_info;
    }

    public String getPub_info() {
        return pub_info;
    }

    public String getManage_name() {
        return manage_name;
    }

    public String getDetailLink() {
        return detailLink;
    }

    public String getId() {
        return id;
    }


    public void setManage_code(String manage_code) {
        this.manage_code = manage_code;
    }

    public void setTitle_info(String title_info) {
        this.title_info = title_info;
    }

    public void setAuthor_info(String author_info) {
        this.author_info = author_info;
    }

    public void setPub_info(String pub_info) {
        this.pub_info = pub_info;
    }

    public void setManage_name(String manage_name) {
        this.manage_name = manage_name;
    }

    public void setDetailLink(String detailLink) {
        this.detailLink = detailLink;
    }

    public void setId(String id) {
        this.id = id;
    }
}