package com.cafe24.kye1898.library.MyBookshelf.MyBook;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cafe24.kye1898.library.BasicInfo;
import com.cafe24.kye1898.library.BookDatabase;
import com.cafe24.kye1898.library.BottomNavigationViewHelper;
import com.cafe24.kye1898.library.MainActivity;
import com.cafe24.kye1898.library.MyBookshelf.SharedBook.LoginActivity;
import com.cafe24.kye1898.library.R;
import com.cafe24.kye1898.library.Search.NearLibSearch.MapsActivity2;

import java.io.File;
import java.util.ArrayList;

public class DeleteActivity extends AppCompatActivity {

    public static String TABLE_BOOK_INFO = "BOOK_INFO";
    public static String TABLE_PHOTO = "PHOTO";
    public static String ACCOUNT_TABLE = "ACCOUNT";
    private static final String TAG = "DeleteActivity";
    BookDatabase database;
    BookAdapter bookAdapter = null;
    GridView listView;
    String starBook="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        showBottom();
        useDB();
        displayListView();

        starDB();

        final Button share_books=(Button)findViewById(R.id.share_books);
        Button myshelf=(Button)findViewById(R.id.my_shelf);
        share_books.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharedBookCom=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(sharedBookCom);
                finish();
            }
        });
        myshelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void starDB(){
        try{
            Cursor cursor = database.rawQuery("select BOOK from " + ACCOUNT_TABLE);
            cursor.moveToNext();
            starBook = cursor.getString(0);
            Log.d("bookname",starBook);

        }catch(Exception e){
            Log.e(TAG, "Exception in executing insert SQL.", e);
        }
    }

    public void onDeleteButtonClicked(View v){
        StringBuffer responseText = new StringBuffer();
        responseText.append("아래의 책들이 삭제되었습니다\n");

        ArrayList<Book> bookList = bookAdapter.bookList;
        for(int i=0;i<bookList.size();i++){
            Book book = bookList.get(i);
            if(book.isSelected()){
                responseText.append("\n" + book.getName());
                Log.d(TAG, book.getName()+"");

                try {
                    database.execSQL("delete from "+TABLE_BOOK_INFO+" where NAME='"+book.getName()+"';");
                    database.execSQL("delete from "+TABLE_PHOTO+" where URI='"+book.getPhoto()+"';");
                    if(book.getName().equals(starBook)){
                        String query = "update "+ACCOUNT_TABLE+" set BOOK = '없음' where BOOK = '"+starBook+"'";
                        database.rawQuery(query);
                        starBook="없음";
                    }

                } catch(Exception ex) {
                    Log.e(TAG, "Exception in executing delete SQL.", ex);
                }
            }
        }

        displayListView();
        Toast.makeText(getApplicationContext(), responseText, Toast.LENGTH_LONG).show();
        finish();
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


    public String getPhotoUriStr(String id_photo) {
        String photoUriStr = null;
        if (id_photo != null && !id_photo.equals("-1")) {
            String SQL = "select URI from " + database.TABLE_PHOTO + " where _ID = " + id_photo + "";
            Cursor photoCursor = database.rawQuery(SQL);
            if (photoCursor.moveToNext()) {
                photoUriStr = photoCursor.getString(0);
            }
            photoCursor.close();
        } else if(id_photo == null || id_photo.equals("-1")) {
            photoUriStr = "";
        }

        return photoUriStr;
    }

    private  void displayListView() {
        ArrayList<Book> bookList = new ArrayList<Book>();

        Book book;
        Cursor cursor=null;
        try{

            cursor = database.rawQuery("select NAME, PHOTO from " + TABLE_BOOK_INFO+" order by DATE DESC");
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                String name = cursor.getString(0);
                String photoId = cursor.getString(1);
                String photoUriStr = getPhotoUriStr(photoId);

                book = new Book(name, photoUriStr);
                bookList.add(book);

            }
        }catch(Exception e){
            Log.e(TAG, "Exception in executing insert SQL.", e);
        }

        bookAdapter = new BookAdapter(this, R.layout.booklist_layout,bookList);
        listView = (GridView)findViewById(R.id.listview);
        listView.setAdapter(bookAdapter);

    }



    private class BookAdapter extends ArrayAdapter<Book> {
        private ArrayList<Book> bookList;

        public BookAdapter (Context context, int id, ArrayList<Book> bookList){
            super(context, id, bookList);
            this.bookList=new ArrayList<Book>();
            this.bookList.addAll(bookList);
        }

        private class ViewHolder{
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
                holder.ck.setVisibility(View.VISIBLE);


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
            Log.d("de",data);
            if (data == null || data.equals("-1") || data.equals("")) {
                holder.bookCover.setImageResource(R.drawable.bookcover);
                holder.bookCover.setScaleType(ImageView.ScaleType.FIT_XY);
            } else {
                File imgFile = new File(BasicInfo.FOLDER_PHOTO+data);

                if(imgFile.exists()){
                    String imagePath = imgFile.getAbsolutePath();
                    Bitmap myBitmap = BitmapFactory.decodeFile(imagePath);
                    holder.bookCover.setImageBitmap(myBitmap);
                    holder.bookCover.setScaleType(ImageView.ScaleType.FIT_XY);
                }

            }

            return convertView;
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
