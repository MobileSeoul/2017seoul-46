package com.cafe24.kye1898.library;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cafe24.kye1898.library.Account.AccountActivity;
import com.cafe24.kye1898.library.MyBookshelf.MyBook.MyBookshelfActivity;
import com.cafe24.kye1898.library.Search.NearLibSearch.MapsActivity2;

import java.io.File;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static String ACCOUNT_TABLE = "ACCOUNT";
    public static String TABLE_BOOK_INFO = "BOOK_INFO";

    private BackPressCloseSystem backPressCloseSystem;
    BookDatabase database;

    ProgressBar progressBar;
    TextView progressText;
    Button goAccountButton;

    TextView lastBook, lastBookmark;

    private static int ONE_MINUTE = 5626;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showBottom();

        goAccountButton = (Button)findViewById(R.id.goAccount);
        useDB();
        lastBook = (TextView)findViewById(R.id.lastBook);
        lastBookmark = (TextView)findViewById(R.id.lastBookmark);
        setLstBookInfo();

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressText = (TextView)findViewById(R.id.progressText);

        setProgress();
        checkSD();

        new AlarmHATT(getApplicationContext()).Alarm();


        backPressCloseSystem = new BackPressCloseSystem(this);
    }

    @Override
    protected void onResume() {
        setProgress();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        backPressCloseSystem.onBackPressed();
    }

    public void checkSD(){
        // SD Card checking
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "SD 카드가 없습니다. SD 카드를 넣은 후 다시 실행하십시오.", Toast.LENGTH_LONG).show();
            return;
        } else {
            String externalPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            if (!BasicInfo.ExternalChecked && externalPath != null) {
                BasicInfo.ExternalPath = externalPath + File.separator;
                Log.d(TAG, "ExternalPath : " + BasicInfo.ExternalPath);

                BasicInfo.FOLDER_PHOTO = BasicInfo.ExternalPath + BasicInfo.FOLDER_PHOTO;

                BasicInfo.ExternalChecked = true;
                Log.d("path",BasicInfo.ExternalPath);
                Log.d("path",BasicInfo.FOLDER_PHOTO);
            }
        }
    }

    public void onAccountClicked(View v){
        Intent accountIntent = new Intent(getApplicationContext(), AccountActivity.class);
        startActivity(accountIntent);
    }

    public class AlarmHATT {
        private Context context;

        public AlarmHATT(Context context) {
            this.context = context;
        }

        public void Alarm() {
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(MainActivity.this, BroadcastD.class);

            PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

            Calendar calendar = Calendar.getInstance();
            //알람시간 calendar에 set해주기

            Log.d("aa",calendar.get(Calendar.MONTH)+"");
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE),
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), 0);

            Cursor cursor = database.rawQuery("select ALARM from " + ACCOUNT_TABLE);
            cursor.moveToNext();
            int al=cursor.getInt(0);

            calendar.add(Calendar.DATE,al);
            Log.d("a",calendar+"");
            //알람 예약
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);

        }
    }


    public void setLstBookInfo(){
        String name="";
        int page=0;
        try{
            Cursor cursor = database.rawQuery("select BOOK from " + ACCOUNT_TABLE);
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                name = cursor.getString(0);
            }

        }catch(Exception e){
            Log.e(TAG, "Exception in executing insert SQL.", e);
        }
        if(name.equals("없음")){
            name="책 상세보기(BOOK)에서 설정해 주세요";
        }
        try{
            Cursor cursor = database.rawQuery("select BOOKMARK from " + TABLE_BOOK_INFO + " where NAME = '"+name+"'");
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                page = cursor.getInt(0);
            }

        }catch(Exception e){
            Log.e(TAG, "Exception in executing insert SQL.", e);
        }

        lastBook.setText(name);
        lastBookmark.setText(page+"쪽");
    }

    public void setProgress(){

        int goal=0;
        int done=0;

        try{
            Cursor cursor = database.rawQuery("select GOAL from " + ACCOUNT_TABLE);
            cursor.moveToNext();
            goal = cursor.getInt(0);

        }catch(Exception e){
            Log.e(TAG, "Exception in executing insert SQL.", e);
        }

        try{
            Cursor cursor2 = database.rawQuery("select count(*) from " + TABLE_BOOK_INFO);
            cursor2.moveToNext();
            done = cursor2.getInt(0);

        }catch(Exception e){
            Log.e(TAG, "Exception in executing insert SQL.", e);
        }

        int good=0;
        if(goal != 0) {
            good = done * 100 / goal;
        }else{
            good=100;
        }


        progressBar.setProgress(good);
        //progressText.setText(good+"%");
        progressText.setText(done+"권 / "+goal+"권");
    }

    private   void useDB(){
        // open database

        if (database != null) {
            database.close();
            database = null;
        }

        database = BookDatabase.getInstance(this);
        boolean isOpen2 = database.open();
        if (isOpen2) {
            Log.d(TAG, "Book database is open.");
        } else {
            Log.d(TAG, "Book database is not open.");
        }

    }


    private void showBottom(){

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        final View view = bottomNavigationView.findViewById(R.id.action_home);
        view.performClick();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.action_search:
                        view.performClick();
                        Intent searchIntent = new Intent(getApplicationContext(),MapsActivity2.class);
                        startActivity(searchIntent);
                        //finish();

                        return true;

                    case R.id.action_home:
                        Intent homeIntent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(homeIntent);
                        finish();
                        return true;

                    case R.id.action_book:
                        view.performClick();
                        Intent myIntent = new Intent(getApplicationContext(), MyBookshelfActivity.class);
                        startActivity(myIntent);
                        //finish();

                        return true;
                }
                return false;
            }
        });
    }
}
