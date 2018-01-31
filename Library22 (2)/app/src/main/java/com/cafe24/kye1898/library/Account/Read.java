package com.cafe24.kye1898.library.Account;

/**
 * Created by YeEun on 2017-09-04.
 */

public class Read {
    String bookname;
    Boolean done=false;


    public Read(String bookname, Boolean done) {
        this.bookname = bookname;
        this.done = done;
    }


    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

}
