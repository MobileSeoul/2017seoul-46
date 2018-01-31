package com.cafe24.kye1898.library.MyBookshelf.MyBook;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cafe24.kye1898.library.BasicInfo;
import com.cafe24.kye1898.library.BookDatabase;
import com.cafe24.kye1898.library.BottomNavigationViewHelper;
import com.cafe24.kye1898.library.MainActivity;
import com.cafe24.kye1898.library.MyBookshelf.MyBook.BookParsing.SearchName_bookParsing;
import com.cafe24.kye1898.library.R;
import com.cafe24.kye1898.library.Search.NearLibSearch.MapsActivity2;
import com.cafe24.kye1898.library.MyBookshelf.MyBook.PhotoCaptureActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.cafe24.kye1898.library.BookDatabase.TABLE_BOOK_INFO;

public class AddActivity extends AppCompatActivity {

    private static final String TAG = "AddActivity";
    public static String TABLE_PHOTO = "PHOTO";
    int mYear, mMonth, mDate;
    TextView dateText;
    BookDatabase database;
    EditText nameEdit,authorEdit,publisherEdit,contentsEdit,bookmarkEdit;
    Button addButton,searchButton;
    ImageView mPhoto;
    Intent intent;
    int flag; // 0-추가 1-편집
    String bookname;

    boolean isPhotoCaptured;
    boolean isPhotoFileSaved;
    boolean isPhotoCanceled;


    int mSelectdContentArray;
    int mChoicedArrayItem;

    Bitmap resultPhotoBitmap;
    String tempPhotoUri;

    String mMemoMode;
    String mMediaPhotoId;
    String mMediaPhotoUri;

    int fromSearch;


    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};
    private static final int MULTIPLE_PERMISSIONS = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        showBottom();

        useDB();
        checkPermissions();
        mPhoto = (ImageView)findViewById(R.id.insert_photo);
       dateText = (TextView) findViewById(R.id.dateEdit);
        nameEdit = (EditText)findViewById(R.id.nameEdit);
        authorEdit=(EditText)findViewById(R.id.authorEdit);
        publisherEdit=(EditText)findViewById(R.id.publisherEdit);
        contentsEdit=(EditText)findViewById(R.id.contentsEdit);
        bookmarkEdit=(EditText)findViewById(R.id.bookmarkEdit);
        addButton = (Button)findViewById(R.id.addButton);
        searchButton = (Button)findViewById(R.id.searchButton);

        intent = getIntent();
        flag=intent.getIntExtra("flag",0);
        fromSearch = intent.getIntExtra("fromSearch",0);
        Log.d("from",flag+"");

        if(fromSearch==1){
            edit();
        }else {

                Calendar calendar = Calendar.getInstance();
                mYear = calendar.get(Calendar.YEAR);
                mMonth = calendar.get(Calendar.MONTH);
                mDate = calendar.get(Calendar.DATE);

                updateDate();
            if (flag == 1) {
                edit();
                searchButton.setVisibility(View.GONE);
                mMemoMode= BasicInfo.MODE_MODIFY;
            }
        }

    }

    private boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(this, pm);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
    public void onInsertPhotoClicked(View v){
        mChoicedArrayItem=0;
        if(isPhotoCaptured||isPhotoFileSaved){
            showDialog(BasicInfo.CONTENT_PHOTO_EX);
        }else{
            showDialog(BasicInfo.CONTENT_PHOTO);
        }
    }
    public void onDateEditClicked(View v){
        //Toast.makeText(getApplicationContext(),"선택",Toast.LENGTH_SHORT).show();
        showDialog(BasicInfo.DATE_DIALOG_ID);
    }

    private  DatePickerDialog.OnDateSetListener dateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    mYear=year;
                    mMonth=month;
                    mDate=dayOfMonth;
                    updateDate();
                }
            };

    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = null;

        switch(id) {

            case BasicInfo.DATE_DIALOG_ID:
                return new DatePickerDialog(this,dateSetListener,mYear,mMonth,mDate);

            case BasicInfo.CONTENT_PHOTO:
                builder = new AlertDialog.Builder(this);

                mSelectdContentArray = R.array.array_photo;
                builder.setTitle("선택하세요");
                builder.setSingleChoiceItems(mSelectdContentArray, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mChoicedArrayItem = whichButton;
                    }
                });
                builder.setPositiveButton("선택", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(mChoicedArrayItem == 0 ) {
                            showPhotoCaptureActivity();
                        } else if(mChoicedArrayItem == 1) {
                            showPhotoSelectionActivity();
                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        Log.d(TAG, "whichButton3        ======        " + whichButton);
                    }
                });

                break;

            case BasicInfo.CONTENT_PHOTO_EX:
                builder = new AlertDialog.Builder(this);

                mSelectdContentArray = R.array.array_photo_ex;
                builder.setTitle("선택하세요");
                builder.setSingleChoiceItems(mSelectdContentArray, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mChoicedArrayItem = whichButton;
                    }
                });
                builder.setPositiveButton("선택", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(mChoicedArrayItem == 0) {
                            showPhotoCaptureActivity();
                        } else if(mChoicedArrayItem == 1) {
                            showPhotoSelectionActivity();
                        } else if(mChoicedArrayItem == 2) {
                            isPhotoCanceled = true;
                            isPhotoCaptured = false;

                            mPhoto.setImageResource(R.drawable.cover_add);
                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                break;

            default:
                break;
        }

        return builder.create();
    }

    public void showPhotoCaptureActivity() {
        Intent intent = new Intent(getApplicationContext(), PhotoCaptureActivity.class);
        startActivityForResult(intent, BasicInfo.REQ_PHOTO_CAPTURE_ACTIVITY);
    }

    public void showPhotoSelectionActivity() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), BasicInfo.REQ_PHOTO_SELECTION_ACTIVITY);

    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);

    }
    public int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public Bitmap rotate(Bitmap src, float degree) {

        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.postRotate(degree);
        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    /**
     * 다른 액티비티로부터의 응답 처리
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch(requestCode) {
            case BasicInfo.REQ_PHOTO_CAPTURE_ACTIVITY:  // 사진 찍는 경우
                Log.d(TAG, "onActivityResult() for REQ_PHOTO_CAPTURE_ACTIVITY.");

                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "resultCode : " + resultCode);

                    boolean isPhotoExists = checkCapturedPhotoFile();
                    if (isPhotoExists) {
                        Log.d(TAG, "image file exists : " + BasicInfo.FOLDER_PHOTO + "captured");

                        resultPhotoBitmap = BitmapFactory.decodeFile(BasicInfo.FOLDER_PHOTO + "captured");

                        tempPhotoUri = "captured";

                        mPhoto.setImageBitmap(resultPhotoBitmap);
                        isPhotoCaptured = true;

                        mPhoto.invalidate();
                    } else {
                        Log.d(TAG, "image file doesn't exists : " + BasicInfo.FOLDER_PHOTO + "captured");
                    }
                }

                break;

            case BasicInfo.REQ_PHOTO_SELECTION_ACTIVITY:  // 사진을 앨범에서 선택하는 경우
                Log.d(TAG, "onActivityResult() for REQ_PHOTO_LOADING_ACTIVITY.");

                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "resultCode : " + resultCode);

                    Uri getPhotoUri=intent.getData();
                    Log.d("adduri",getPhotoUri+"");
                    String imagePath = getRealPathFromURI(getPhotoUri); // path 경로
                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeStream(getContentResolver().openInputStream(getPhotoUri), null, options);

                        options.inSampleSize = calculateInSampleSize(options, 160*2, 100*2);
                        options.inJustDecodeBounds = false;

                        resultPhotoBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(getPhotoUri),null,options);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    ExifInterface exif = null;
                    try {
                        Log.d("add",imagePath+"");
                        exif = new ExifInterface(imagePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int exifDegree = exifOrientationToDegrees(exifOrientation);
                    Log.d("add",exifDegree+"");
                    resultPhotoBitmap=rotate(resultPhotoBitmap,exifDegree);

                    mPhoto.setImageBitmap(resultPhotoBitmap);
                    mPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
                    isPhotoCaptured = true;

                    mPhoto.invalidate();
                }

                break;

        }
    }

    /**
     * 저장된 사진 파일 확인
     */
    private boolean checkCapturedPhotoFile() {
        File file = new File(BasicInfo.FOLDER_PHOTO + "captured");
        if(file.exists()) {
            return true;
        }

        return false;
    }


    public void onSearchButtonClicked(View v){

        String name = nameEdit.getText().toString();
        String author = authorEdit.getText().toString();
        String publisher = publisherEdit.getText().toString();
        String sdate = dateText.getText().toString();
        java.sql.Date date = java.sql.Date.valueOf(sdate);
        int bookmark;
        if (bookmarkEdit.getText().toString().length() == 0) {
            bookmark = 0;
        } else {
            bookmark = Integer.parseInt(bookmarkEdit.getText().toString());
        }
        String contents = contentsEdit.getText().toString();

        Book nowbook= new Book(name,author,publisher,date,contents,bookmark);


        Intent searchBookIntent = new Intent(getApplicationContext(),SearchName_bookParsing.class);
        searchBookIntent.putExtra("find",1);
        searchBookIntent.putExtra("flag",flag);
        searchBookIntent.putExtra("bookdata",nowbook);
        startActivity(searchBookIntent);
        finish();

    }
    public void writeFile(InputStream is, OutputStream os) throws IOException
    {
        int c = 0;
        while((c = is.read()) != -1)
            os.write(c);
        os.flush();
    }

    public void downloadImage(String data){

        try {
            File photoFolder = new File(BasicInfo.FOLDER_PHOTO);

            //폴더가 없다면 폴더를 생성한다.
            if(!photoFolder.isDirectory()){
                Log.d(TAG, "creating photo folder : " + photoFolder);
                photoFolder.mkdirs();
            }

            String photoName = "captured";

            // 기존 이미지가 있으면 삭제
            File file = new File(BasicInfo.FOLDER_PHOTO + photoName);
            if(file.exists()) {
                file.delete();
            }

            InputStream inputStream = new URL(data).openStream();
            FileOutputStream outstream = new FileOutputStream(BasicInfo.FOLDER_PHOTO + photoName);
            writeFile(inputStream, outstream);
            outstream.close();
        }catch (Exception e){

        }

        resultPhotoBitmap = BitmapFactory.decodeFile(BasicInfo.FOLDER_PHOTO + "captured");

        tempPhotoUri = "captured";

        mPhoto.setImageBitmap(resultPhotoBitmap);
        mPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
        isPhotoCaptured = true;

        mPhoto.invalidate();

    }

    /**
     * 앨범의 사진을 사진 폴더에 복사한 후, PICTURE 테이블에 사진 정보 추가
     * 이미지의 이름은 현재 시간을 기준으로 한 getTime() 값의 문자열 사용
     *
     * @return 새로 추가된 이미지의 이름
     */

    private String insertPhoto() {
        String photoName = null;

        if (isPhotoCaptured) { // captured Bitmap
            try {
                if (mMemoMode != null && mMemoMode.equals(BasicInfo.MODE_MODIFY)) {
                    Log.d(TAG, "previous photo is newly created for modify mode.");


                    String SQL = "delete from " + TABLE_PHOTO +
                            " where _ID = '" + mMediaPhotoId + "'";
                    Log.d(TAG, "SQL : " + SQL);

                    if (database != null) {
                        database.execSQL(SQL);
                    }

                    File previousFile = new File(BasicInfo.FOLDER_PHOTO + mMediaPhotoUri);
                    if (previousFile.exists()) {
                        previousFile.delete();
                    }
                }

                File photoFolder = new File(BasicInfo.FOLDER_PHOTO);

                //폴더가 없다면 폴더를 생성한다.
                if(!photoFolder.isDirectory()){
                    Log.d(TAG, "creating photo folder : " + photoFolder);
                    photoFolder.mkdirs();
                }

                // Temporary Hash for photo file name
                photoName = createFilename();

                FileOutputStream outstream = new FileOutputStream(BasicInfo.FOLDER_PHOTO + photoName);

                resultPhotoBitmap.compress(Bitmap.CompressFormat.PNG, 100, outstream);
                outstream.close();


                if (photoName != null) {
                    Log.d(TAG, "isCaptured            : " +isPhotoCaptured);

                    // INSERT PICTURE INFO
                    String SQL = "insert into " + TABLE_PHOTO + "(URI) values(" + "'" + photoName + "')";
                    if (database != null) {
                        database.execSQL(SQL);
                    }
                }

            } catch (IOException ex) {
                Log.d(TAG, "Exception in copying photo : " + ex.toString());
            }


        }
        return photoName;
    }


    private String createFilename() {
        Date curDate = new Date();
        String curDateStr = String.valueOf(curDate.getTime());

        return curDateStr;
    }

    /**
     * 데이터베이스 레코드 수정
     */
    private void modifyInput() {

        Intent intent = getIntent();

        String photoFilename = insertPhoto();
        int photoId = -1;

        String SQL = null;

        if (photoFilename != null) {
            // query picture id
            SQL = "select _ID from " + TABLE_PHOTO + " where URI = '" + photoFilename + "'";
            Log.d(TAG, "SQL : " + SQL);
            if (database != null) {
                Cursor cursor = database.rawQuery(SQL);
                if (cursor.moveToNext()) {
                    photoId = cursor.getInt(0);
                }
                cursor.close();

                mMediaPhotoUri = photoFilename;

                SQL = "update " + TABLE_BOOK_INFO +
                        " set " +
                        " PHOTO = '" + photoId + "'" +
                        " where NAME = '" + bookname + "'";

                if (database != null) {
                    database.rawQuery(SQL);
                }

                mMediaPhotoId = String.valueOf(photoId);
            }
        } else if(isPhotoCanceled && isPhotoFileSaved) {
            SQL = "delete from " + TABLE_PHOTO +
                    " where _ID = '" + mMediaPhotoId + "'";
            Log.d(TAG, "SQL : " + SQL);
            if (database != null) {
                database.execSQL(SQL);
            }

            File photoFile = new File(BasicInfo.FOLDER_PHOTO + mMediaPhotoUri);
            if (photoFile.exists()) {
                photoFile.delete();
            }

            SQL = "update " + TABLE_BOOK_INFO +
                    " set " +
                    " PHOTO = '" + photoId + "'" +
                    " where NAME = '" + bookname + "'";

            if (database != null) {
                database.rawQuery(SQL);
            }

            mMediaPhotoId = String.valueOf(photoId);
        }

    }



    public void edit(){

        Book mybook = (Book) intent.getSerializableExtra("bookdata");

        bookname=mybook.getName();
        nameEdit.setText(mybook.getName());
        authorEdit.setText(mybook.getAuthor());
        publisherEdit.setText(mybook.getPublisher());
        contentsEdit.setText(mybook.getContents());
        dateText.setText(mybook.getDate()+"");
        bookmarkEdit.setText(mybook.getBookmark()+"");
        if(fromSearch==1){
            String data=intent.getStringExtra("url");
            downloadImage(data);
        }else{
            String data=mybook.getPhoto();
            setMediaImage(data);
        }


    }

    public void setMediaImage(String photoUri) {

        if(photoUri==null ||photoUri.equals("") || photoUri.equals("-1")) {
            mPhoto.setImageResource(R.drawable.cover_add);
        } else {
            isPhotoFileSaved = true;
            mPhoto.setImageURI(Uri.parse(BasicInfo.FOLDER_PHOTO + photoUri));
            mPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
        }

    }



    public void onAddButtonClicked(View v) {

        String name = nameEdit.getText().toString();
        String author = authorEdit.getText().toString();
        String publisher = publisherEdit.getText().toString();
        String date = dateText.getText().toString();
        int bookmark;
        if (bookmarkEdit.getText().toString().length() == 0) {
            bookmark = 0;
        } else {
            bookmark = Integer.parseInt(bookmarkEdit.getText().toString());
        }
        String contents = contentsEdit.getText().toString();



        if (flag == 0) {
            if(name.contains("'")){
                Toast.makeText(getApplicationContext(),"책제목에 '(작은따옴표)는 사용할 수 없습니다",Toast.LENGTH_LONG).show();
            }else if(author.contains("'")) {
                Toast.makeText(getApplicationContext(),"지은이에 '(작은따옴표)는 사용할 수 없습니다",Toast.LENGTH_LONG).show();
            }else if(publisher.contains("'")){
                Toast.makeText(getApplicationContext(),"출판사에 '(작은따옴표)는 사용할 수 없습니다",Toast.LENGTH_LONG).show();
            }else if(contents.contains("'")){
                Toast.makeText(getApplicationContext(),"독후감에 '(작은따옴표)는 사용할 수 없습니다",Toast.LENGTH_LONG).show();
            }
            else
            {
                if (database.checkExists(name)) {
                    Toast.makeText(getApplicationContext(), "이미 등록된 책입니다", Toast.LENGTH_SHORT).show();
                }else if(name.equals("")){
                    Toast.makeText(getApplicationContext(),"책제목을 입력해주세요",Toast.LENGTH_SHORT).show();
                }else {

                    String photoFilename = insertPhoto();
                    int photoId = -1;

                    String SQL = null;

                    if (photoFilename != null) {
                        // query picture id
                        SQL = "select _ID from " + TABLE_PHOTO + " where URI = '" + photoFilename + "'";
                        Log.d(TAG, "SQL : " + SQL);
                        if (database != null) {
                            Cursor cursor = database.rawQuery(SQL);
                            if (cursor.moveToNext()) {
                                photoId = cursor.getInt(0);
                            }
                            cursor.close();
                        }
                    }
                    try {
                        database.execSQL("insert into " + TABLE_BOOK_INFO +
                                "(NAME, AUTHOR, PUBLISHER, BOOKMARK, CONTENTS, DATE, PHOTO) values ('"
                                + name + "', '" + author + "', '" + publisher + "', " + bookmark + ", '" + contents + "', '" + date + "','" + photoId + "');");
                        Toast.makeText(getApplicationContext(), "추가되었습니다.", Toast.LENGTH_SHORT).show();
                    } catch (Exception ex) {
                        Log.e(TAG, "Exception in executing insert SQL.", ex);
                    }

                    finish();
                }
            }

        }else if(flag==1){

            modifyInput();
            //database.rawQuery("update "+TABLE_BOOK_INFO+ " set STAR = 'true' where NAME = '"+ bookname+ "'");
            String query = "update "+TABLE_BOOK_INFO+" set NAME = '"+name+"', AUTHOR = '"+author+"', PUBLISHER = '"+
                    publisher+ "', CONTENTS = '"+contents+"', DATE = '"+date+"', BOOKMARK = '"+bookmark +"' where NAME = '"+bookname+"';";


            try {
                database.execSQL(query);
                Toast.makeText(getApplicationContext(), "편집되었습니다.", Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
                Log.e(TAG, "Exception in executing insert SQL.", ex);
            }
            finish();
        }
    }

    private void updateDate(){
        String str="";
        str+=mYear;
        if(mMonth<9) str+="-0"+(mMonth+1);
        else str+="-"+(mMonth+1);
        if(mDate<10) str+="-0"+mDate;
        else str+="-"+mDate;

        dateText.setText(str);

    }



    private  void useDB(){
        // open database
        if (database != null) {
            database.close();
            database = null;
        }

        database = BookDatabase.getInstance(this);
        boolean isOpen = database.open();
        if (isOpen) {
            Log.d(TAG, "Book database is open.");
        } else {
            Log.d(TAG, "Book database is not open.");
        }
    }

    private void showBottom(){

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        View view = bottomNavigationView.findViewById(R.id.action_book);
        view.performClick();
        //bottomNavigation.setForceTitlesDisplay(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.action_search:
                        Intent searchIntent = new Intent(getApplicationContext(),MapsActivity2.class);
                        searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(searchIntent);
                        finish();
                        return true;

                    case R.id.action_home:
                        Intent homeIntent = new Intent(getApplicationContext(),MainActivity.class);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeIntent);
                        finish();
                        return true;

                    case R.id.action_book:
                        Intent myIntent = new Intent(getApplicationContext(), MyBookshelfActivity.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(myIntent);
                        finish();
                        return true;
                }
                return false;
            }
        });
    }

}
