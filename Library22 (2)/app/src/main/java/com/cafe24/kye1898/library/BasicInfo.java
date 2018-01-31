package com.cafe24.kye1898.library;

import android.Manifest;

/**
 * Created by YeEun on 2017-08-31.
 */

public class BasicInfo {

    /**
     * 외장 메모리 패스
     */
    public static String ExternalPath ="sdcard/";

    /**
     * 외장 메모리 패스 체크 여부
     */
    public static boolean ExternalChecked = false;

    /**
     * 사진 저장 위치
     */
    public static String FOLDER_PHOTO 		= "Library/photo/";

    //public static String DATABASE_NAME = "Library/book.db";

    public static final int REQ_PHOTO_CAPTURE_ACTIVITY = 1501;
    public static final int REQ_PHOTO_SELECTION_ACTIVITY = 1502;

    public static final int CONTENT_PHOTO = 2001;
    public static final int CONTENT_PHOTO_EX = 2005;
    public static final int CONFIRM_TEXT_INPUT = 3002;
    public static final int IMAGE_CANNOT_BE_STORED = 1002;
    public static final int DATE_DIALOG_ID = 1000;



    //========== 메모 모드 상수 ==========//

    public static final String MODE_MODIFY = "MODE_MODIFY";


}
