package com.cafe24.kye1898.library.MyBookshelf.MyBook;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.cafe24.kye1898.library.BasicInfo;
import com.cafe24.kye1898.library.BookDatabase;
import com.cafe24.kye1898.library.BottomNavigationViewHelper;
import com.cafe24.kye1898.library.MainActivity;
import com.cafe24.kye1898.library.MyBookshelf.SharedBook.LoginActivity;
import com.cafe24.kye1898.library.R;
import com.cafe24.kye1898.library.Search.NearLibSearch.MapsActivity2;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class MyBookshelfActivity extends AppCompatActivity {


    public static String TABLE_BOOK_INFO = "BOOK_INFO";
    private static final String TAG = "MyBookshelfActivity";
    BookDatabase database;
    BookAdapter bookAdapter = null;
    GridView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookshelf);
        showBottom();

        useDB();

        listView = (GridView)findViewById(R.id.listview);
        displayListView();

        showInfo();


        final Button share_books=(Button)findViewById(R.id.share_books);
        share_books.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharedBookCom=new Intent(MyBookshelfActivity.this,LoginActivity.class);
                startActivity(sharedBookCom);
                finish();
            }
        });


    }

    private void showInfo(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book item = bookAdapter.getItem(position);
                //Toast.makeText(getApplicationContext(),"선택 : "+item.getName(),Toast.LENGTH_SHORT).show();
                Log.d(TAG,item.getContents());

                Intent infoIntent = new Intent(getApplicationContext(),InfoActivity.class);
                infoIntent.putExtra("bookdata",item);
                startActivity(infoIntent);
            }
        });
    }

    public void onDeleteButtonClicked(View v){
        Intent delete = new Intent(getApplicationContext(),DeleteActivity.class);
        startActivity(delete);
    }

    public void onInsertButtonClicked(View v){
        Intent add = new Intent(getApplicationContext(),AddActivity.class);
        add.putExtra("flag",0);
        startActivity(add);
    }

    private   void useDB(){
        // open database
        if (database != null) {
            database.close();
            database = null;
            Log.d("book","yoyo");
        }

        database = BookDatabase.getInstance(this);
        boolean isOpen = database.open();
        if (isOpen) {
            Log.d(TAG, "Book database is open.");
        } else {
            Log.d(TAG, "Book database is not open.");
        }
    }

    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        displayListView();
        super.onResume();
    }


    /**
     * 사진 데이터 URI 가져오기
     */
    public String getPhotoUriStr(String id_photo) {
        String photoUriStr = null;
        if (id_photo != null && !id_photo.equals("-1")) {
            String SQL = "select URI from " + database.TABLE_PHOTO + " where _ID = " + id_photo + "";
            Cursor photoCursor=null;
            try{
                photoCursor = database.rawQuery(SQL);
                Log.d("photo",photoCursor+"");
                if (photoCursor.moveToNext()) {
                    photoUriStr = photoCursor.getString(0);
                }
                photoCursor.close();
            }catch(Exception e){
                Log.e(TAG, "Exception in executing insert SQL.", e);
            } finally{
                if(photoCursor!=null &&!photoCursor.isClosed())
                photoCursor.close();
            }

        } else if(id_photo == null || id_photo.equals("-1")) {
            photoUriStr = "";
        }

        return photoUriStr;
    }

    public void displayListView() {
        ArrayList<Book> bookList = new ArrayList<Book>();

        Book book;
        int count=0;
        Cursor cursor=null;
        try{
            cursor = database.rawQuery("select NAME, AUTHOR, PUBLISHER, CONTENTS, DATE, BOOKMARK, STAR, PHOTO from " + TABLE_BOOK_INFO+
                    " order by DATE DESC");

            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                String name = cursor.getString(0);
                String author = cursor.getString(1);
                String publisher = cursor.getString(2);
                String contents = cursor.getString(3);
                String sdate = cursor.getString(4);
                Date date = Date.valueOf(sdate);
                int bookmark = cursor.getInt(5);
                String photoId = cursor.getString(7);
                String photoUriStr = getPhotoUriStr(photoId);
                Log.d("view",i+"");

                book = new Book(name, author,publisher,date,contents,bookmark,photoUriStr);
                bookList.add(book);


            }
            cursor.close();
        }catch(Exception e){
            Log.e(TAG, "Exception in executing insert SQL.", e);
        } finally{
            if(cursor!=null && !cursor.isClosed())
                cursor.close();
        }


        bookAdapter = new BookAdapter(this, R.layout.booklist_layout,bookList);
        listView.setAdapter(bookAdapter);

        //bookAdapter.notifyDataSetChanged();


    }

    private class BookAdapter extends ArrayAdapter<Book> {
        private ArrayList<Book> bookList;


        public BookAdapter (Context context, int id, ArrayList<Book> bookList){
            super(context, id, bookList);
            this.bookList=new ArrayList<Book>();
            this.bookList.addAll(bookList);
        }

        public class ViewHolder{
            TextView name;
            CheckBox ck;
            ImageView bookCover;
        }

        @Override
        public void add(Book object) {
            super.add(object);
        }


        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if(convertView==null){
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.booklist_layout,null);


                holder = new ViewHolder();
                holder.name=(TextView)convertView.findViewById(R.id.name);
                holder.ck=(CheckBox)convertView.findViewById(R.id.checkBox);
                holder.bookCover=(ImageView)convertView.findViewById(R.id.bookCover);
                convertView.setTag(holder);

                holder.ck.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        Book book = (Book) cb.getTag();
                        book.setSelected(cb.isChecked());
                    }
                });

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Book book = bookList.get(position);

            holder.name.setText(book.getName());
            holder.ck.setChecked(book.isSelected());
            holder.ck.setTag(book);
            String data=book.getPhoto();

            //holder.bookCover.setImageResource(R.drawable.bookcover);
            Log.d("chchchk",data);

            Bitmap bitmap=null;
            if (data == null || data.equals("-1") || data.equals("")) {
                holder.bookCover.setImageResource(R.drawable.bookcover);
			} else {

                File imgFile = new  File(BasicInfo.FOLDER_PHOTO+data);

                if(imgFile.exists()){
                    String imagePath = imgFile.getAbsolutePath();
                    Bitmap myBitmap = BitmapFactory.decodeFile(imagePath);
                    holder.bookCover.setImageBitmap(myBitmap);

                }

            }
            holder.bookCover.setScaleType(ImageView.ScaleType.FIT_XY);

            return convertView;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

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
