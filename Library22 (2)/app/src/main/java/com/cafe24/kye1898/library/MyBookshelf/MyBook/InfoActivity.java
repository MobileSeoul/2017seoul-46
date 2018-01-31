package com.cafe24.kye1898.library.MyBookshelf.MyBook;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.cafe24.kye1898.library.BasicInfo;
import com.cafe24.kye1898.library.BookDatabase;
import com.cafe24.kye1898.library.BottomNavigationViewHelper;
import com.cafe24.kye1898.library.MainActivity;
import com.cafe24.kye1898.library.MyBookshelf.SharedBook.LoginActivity;
import com.cafe24.kye1898.library.MyBookshelf.SharedBook.ShareBookRequest;
import com.cafe24.kye1898.library.MyBookshelf.SharedBook.SharedBookComunity;
import com.cafe24.kye1898.library.R;
import com.cafe24.kye1898.library.Search.NearLibSearch.MapsActivity2;

import org.json.JSONObject;

import java.io.File;

public class InfoActivity extends AppCompatActivity {



    public static String TABLE_BOOK_INFO = "BOOK_INFO";
    private static final String TAG = "InfoActivity";
    public static String ACCOUNT_TABLE = "ACCOUNT";

    BookDatabase database;
    TextView bookText,authorText,publisherText,dateText,contentText;
    Button bookmark;
    ImageView bookCover,star;

    private AlertDialog dialog;

    String bookname;
    Book mybook;
    String userID;
    String sbook,sauthor,spub,scon;

    int changedbookmark;

    String starBook="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        SharedPreferences pref=getSharedPreferences("Game", Activity.MODE_PRIVATE);
        userID=pref.getString("userID",null);

        bookText = (TextView)findViewById(R.id.bookText);
        authorText = (TextView)findViewById(R.id.autorText);
        publisherText = (TextView)findViewById(R.id.publisherText);
        contentText = (TextView)findViewById(R.id.contentText);
        dateText = (TextView)findViewById(R.id.dateText);
        bookmark = (Button)findViewById(R.id.bookmark);
        bookCover = (ImageView)findViewById(R.id.bookCover);
        star = (ImageView)findViewById(R.id.star);

        useDB();
        showInfo();
        showBottom();


    }
    protected Dialog onCreateDialog(int id) {
        android.app.AlertDialog.Builder builder = null;
        builder = new android.app.AlertDialog.Builder(this);

        switch(id){
            case 1:
                builder = new android.app.AlertDialog.Builder(this);
                builder.setMessage("읽던 책을 '"+starBook+"'에서 '"+bookname+"'(으)로 바꾸시겠습니까?");
                builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        star.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_star_black_24dp));
                        String query = "update "+ACCOUNT_TABLE+" set BOOK = '"+bookname+"' where BOOK = '"+starBook+"'";
                        database.rawQuery(query);
                        starBook=bookname;

                    }
                });
                builder.setNegativeButton("아니요", null);

                break;

            case 2:
                builder = new android.app.AlertDialog.Builder(this);
                builder.setMessage("읽던책에서 취소하시겠습니까?");
                builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        star.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_star_border_black_24dp));
                        String query = "update "+ACCOUNT_TABLE+" set BOOK = '없음' where BOOK = '"+starBook+"'";
                        database.rawQuery(query);
                        starBook="없음";

                    }
                });
                builder.setNegativeButton("아니요", null);

                break;
            case 3:
                builder = new android.app.AlertDialog.Builder(this);
                builder.setMessage("로그인되어 있지 않습니다.\n로그인 하시겠습니까?");
                builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("아니요",null);
                break;

            default:
                break;
        }
        return builder.create();
    }


    public void onStarClicked(View v){

        if(bookname.equals(starBook)){
            showDialog(2);

        }else{
            showDialog(1);
        }
    }


    private void changeDividerColor(NumberPicker picker, int color) {

        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(picker, colorDrawable);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }



    public void onMarkClicked(View v){
        final Dialog d = new Dialog(InfoActivity.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.numberpicker3_layout);

        Button setButton = (Button)d.findViewById(R.id.cancel);
        Button cancelButton = (Button)d.findViewById(R.id.set);

        final NumberPicker np1 = (NumberPicker)d.findViewById(R.id.numberPicker1);
        final NumberPicker np2 = (NumberPicker)d.findViewById(R.id.numberPicker2);
        final NumberPicker np3 = (NumberPicker)d.findViewById(R.id.numberPicker3);

        np1.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np3.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        np1.setMaxValue(9); np2.setMaxValue(9); np3.setMaxValue(9);
        np1.setMinValue(0); np2.setMinValue(0); np3.setMinValue(0);

        int temp,temp1,temp2,temp3;

        temp=changedbookmark; //쪽수 받아오기
        temp1=temp/100;
        temp-=(temp1*100);
        temp2=temp/10;
        temp-=(temp2*10);
        temp3=temp;
        np1.setValue(temp1); np2.setValue(temp2); np3.setValue(temp3);

        np1.setWrapSelectorWheel(true); np2.setWrapSelectorWheel(true); np3.setWrapSelectorWheel(true);

        changeDividerColor(np1, Color.parseColor("#00ffffff"));
        changeDividerColor(np2, Color.parseColor("#00ffffff"));
        changeDividerColor(np3, Color.parseColor("#00ffffff"));

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changedbookmark=np1.getValue()*100+np2.getValue()*10+np3.getValue();

                try {
                    String q="update "+TABLE_BOOK_INFO+" set BOOKMARK = '"+changedbookmark+"' where NAME = '"+ bookname+"'";
                    database.rawQuery(q);
                }catch(Exception e){
                    Log.e(TAG, "Exception in executing update SQL.", e);
                }
                bookmark.setText(changedbookmark+"");
                d.dismiss();
            }
        });

        d.show();


    }

    public void onEditButtonClicked(View v){

        Intent intent = new Intent(getApplicationContext(),AddActivity.class);
        intent.putExtra("bookdata",mybook);
        intent.putExtra("flag",1);
        startActivity(intent);
        finish();

    }



    public void showInfo(){
        Intent intent = getIntent();
        mybook = (Book) intent.getSerializableExtra("bookdata");

        bookname=mybook.getName();
        bookText.setText(mybook.getName());
        authorText.setText(mybook.getAuthor());
        publisherText.setText(mybook.getPublisher());
        contentText.setText(mybook.getContents());
        dateText.setText(mybook.getDate()+"");
        bookmark.setText(mybook.getBookmark()+"");
        String data=mybook.getPhoto();
        changedbookmark=Integer.parseInt(bookmark.getText()+"");
        if (data == null || data.equals("-1") || data.equals("")) {
            bookCover.setImageResource(R.drawable.bookcover);
        } else {
            File imgFile = new  File(BasicInfo.FOLDER_PHOTO+data);

            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                bookCover.setImageBitmap(myBitmap);
                bookCover.setScaleType(ImageView.ScaleType.FIT_XY);
            }
        }


        try{
            Cursor cursor = database.rawQuery("select BOOK from " + ACCOUNT_TABLE);
            cursor.moveToNext();
            starBook = cursor.getString(0);
            Log.d("bookname",starBook);

        }catch(Exception e){
            Log.e(TAG, "Exception in executing insert SQL.", e);
        }

        if(starBook.equals(bookname)){
            star.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.ic_star_black_24dp));
        }
    }

    public void onShareButtonClicked(View v) {

        sbook = bookText.getText().toString();
        sauthor = authorText.getText().toString();
        spub = publisherText.getText().toString();
        scon = contentText.getText().toString();
        //title=etitle.getText().toString();
        //wContent=econtent.getText().toString();

        if(userID!=null) {
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if (success) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(InfoActivity.this);
                            dialog = builder.setMessage("공유 되었습니다.\n(주제와 무관한 내용과 욕설은 삭제될 수 있습니다.)\n")
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int id) {

                                            Intent goshare=new Intent(InfoActivity.this,SharedBookComunity.class);
                                            startActivity(goshare);
                                            finish();
                                        }
                                    })
                                    .create();
                            dialog.show();

                            //

                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(InfoActivity.this);
                            dialog = builder.setMessage("실패했습니다.")
                                    .setNegativeButton("확인", null)
                                    .create();
                            dialog.show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
            };
            Log.d("pass:", userID + sbook + sauthor + spub + scon);
            ShareBookRequest shareRequest = new ShareBookRequest(userID, sbook, sauthor, spub, scon, responseListener);
            RequestQueue queue = Volley.newRequestQueue(InfoActivity.this);
            queue.add(shareRequest);
        }
        else {
            Log.e("error is from this", "0000000000000000000000000");
            //Toast.makeText(InfoActivity.this,"hello!!!",Toast.LENGTH_SHORT).show();
            showDialog(3);

        }


    }

    private void useDB(){
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