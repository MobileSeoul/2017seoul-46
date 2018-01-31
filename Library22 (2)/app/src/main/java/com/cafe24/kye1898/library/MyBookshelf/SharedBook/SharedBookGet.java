package com.cafe24.kye1898.library.MyBookshelf.SharedBook;

/**
 * Created by nahej on 2017-08-20.
 */

public class SharedBookGet {

    int sid;
    String userID;
    String sTitle;
    String author;
    String publisher;
    String sContent;
    String likeCount2;

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getsTitle() {
        return sTitle;
    }

    public void setsTitle(String sTitle) {
        this.sTitle = sTitle;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getsContent() {
        return sContent;
    }

    public void setsContent(String sContent) {
        this.sContent = sContent;
    }

    public String getLikeCount2() {
        return likeCount2;
    }

    public void setLikeCount2(String likeCount2) {
        this.likeCount2 = likeCount2;
    }
    public SharedBookGet(int sid, String userID, String sTitle, String author,
                       String publisher,String sContent, String likeCount2){
        this.sid=sid;
        this.userID=userID;
        this.sTitle=sTitle;
        this.author=author;
        this.publisher=publisher;
        this.sContent=sContent;
        this.likeCount2=likeCount2;
    }
}
