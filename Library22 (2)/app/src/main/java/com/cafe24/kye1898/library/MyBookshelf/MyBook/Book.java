package com.cafe24.kye1898.library.MyBookshelf.MyBook;

import java.io.Serializable;
import java.sql.Date;

/**
 * Created by YeEun on 2017-06-22.
 */

public class Book implements Serializable {
    String name=null;
    String author=null;
    String publisher=null;
    Date date;
    String contents=null;
    int bookmark=0;
    boolean star=false;
    boolean selected = false;
    String photo="";

    public Book(String name) {
        this.name = name;
    }

    public Book(String name, String photo) {
        this.name = name;
        this.photo = photo;
    }

    public Book(String name, String author, String publisher, Date date, String contents, int bookmark) {
        this.name = name;
        this.author = author;
        this.publisher = publisher;
        this.date = date;
        this.contents = contents;
        this.bookmark = bookmark;
    }

    public Book(String name, String author, String publisher, Date date, String contents, int bookmark, String photo) {
        this.name = name;
        this.author = author;
        this.publisher = publisher;
        this.date = date;
        this.contents = contents;
        this.bookmark = bookmark;
        this.photo = photo;
    }


    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getContents() {
        return contents;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getBookmark() {
        return bookmark;
    }

    public void setBookmark(int bookmark) {
        this.bookmark = bookmark;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isStar() {
        return star;
    }

    public void setStar(boolean star) {
        this.star = star;
    }
}



