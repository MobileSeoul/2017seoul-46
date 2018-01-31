package com.cafe24.kye1898.library.MyBookshelf.SharedBook;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.cafe24.kye1898.library.BottomNavigationViewHelper;
import com.cafe24.kye1898.library.MyBookshelf.MyBook.MyBookshelfActivity;
import com.cafe24.kye1898.library.MainActivity;
import com.cafe24.kye1898.library.R;

import com.cafe24.kye1898.library.Search.NearLibSearch.MapsActivity2;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Junyoung on 2016-06-23.
 */

public class SharedBookComunity extends AppCompatActivity {

    private RecyclerView mBlogList;
    String userID;
    private ListView wListView;
    private SharedThingsListAdapter adapter=null;
    private List<SharedBookGet> writeThingsList=new ArrayList<SharedBookGet>();

    //private static String url = "http://kye1898.cafe24.com/nsl/read_writeCon.php";
    TextView todayTopic,todayDate;
    Button button, logout;
    String ttid, ttopic, tdate;
    Button search_book;
    EditText search_name;
    Button loggout;
    TextView useruser;
    String sT1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shared_book);

        useruser=(TextView)findViewById(R.id.useruserid);

        logout=(Button) findViewById(R.id.loggout);
        logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent gotomain=new Intent(SharedBookComunity.this, LoginActivity.class);
                startActivity(gotomain);

                SharedPreferences pref =SharedBookComunity.this.getSharedPreferences("Game", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                //editor.clear()는 auto에 들어있는 모든 정보를 기기에서 지웁니다.
                editor.clear();
                editor.commit();

                Toast.makeText(SharedBookComunity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }


        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        View view1 = bottomNavigationView.findViewById(R.id.action_book);
        view1.performClick();

        final Button share_books=(Button)findViewById(R.id.share_books);
        Button myshelf=(Button)findViewById(R.id.my_shelf);
        share_books.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharedBookCom=new Intent(SharedBookComunity.this,LoginActivity.class);
                startActivity(sharedBookCom);
            }
        });
        myshelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Tomyshelf=new Intent(SharedBookComunity.this,MyBookshelfActivity.class);
                startActivity(Tomyshelf);
            }
        });


        search_book=(Button) findViewById(R.id.share_search_book_name);
        search_name=(EditText)findViewById(R.id.searchbookname_share);


        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
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


       // LinearLayoutManager layoutManager=
                //new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL,false);
        //RecyclerView myList=(RecyclerView)view.findViewById(R.id.rrr);
        //myList.setLayoutManager(layoutManager);
        wListView=(ListView)findViewById(R.id.sharedlist);
        adapter=new SharedThingsListAdapter(SharedBookComunity.this,writeThingsList);
        //courseListView=(ListView)getView().findViewById(R.id.courseListView);
        wListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        SharedPreferences pref=this.getSharedPreferences("Game", Activity.MODE_PRIVATE);
        userID=pref.getString("userID",null);

        useruser.setText(userID);

        new FirstPageTask().execute();
        adapter.notifyDataSetChanged();

        search_book.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View v){
                //Toast.makeText(SharedBookComunity.this,"you are", Toast.LENGTH_SHORT).show();
                sT1=search_name.getText().toString();
                try {
                    sT1 = URLEncoder.encode(sT1,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                new SecondPageTask().execute();

                //Toast.makeText(SharedBookComunity.this,"name:" +sT1,Toast.LENGTH_LONG).show();

               //ShareSearchNameRequest searchRequest=new ShareSearchNameRequest(sT, responseListener);
               // RequestQueue queue = Volley.newRequestQueue(SharedBookComunity.this.getApplicationContext());
               // queue.add(searchRequest);


            }
        });
    }


    class FirstPageTask extends AsyncTask<Void, Void, String>
    {

        String target;
        @Override
        protected void onPreExecute() {
            try {
                target = "http://kye1898.cafe24.com/nsl/share_get.php";
                Log.d("target",target);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids){
            try{
                URL url=new URL(target);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                InputStream inputStream=httpURLConnection.getInputStream();
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuffer stringBuilder=new StringBuffer();

                while ((temp=bufferedReader.readLine())!=null) {
                    stringBuilder.append(temp + "\n");
                    Log.d("b",temp);
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return stringBuilder.toString().trim();

            }catch(Exception e){

                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onProgressUpdate(Void... values){
            super.onProgressUpdate();
        }

        @Override
        public void onPostExecute(String result){
            try{
                //writeThingsList.clear();
                JSONObject jsonObject=new JSONObject(result);
                JSONArray rows=jsonObject.getJSONArray("response");
                int count=0;
                count=rows.length();
                for (int i = 0; i < rows.length(); i++) {
                    JSONObject c = rows.getJSONObject(i);

                    String sid = c.getString("sid");
                    String userID = c.getString("userID");
                    String sTitle=c.getString("sTitle");
                    String author = c.getString("author");
                    String publisher = c.getString("publisher");
                    String sContent = c.getString("sContent");
                    String likeCount2=c.getString("likeCount2");

                    SharedBookGet sthings=new SharedBookGet(Integer.valueOf(sid), userID, sTitle, author, publisher, sContent, likeCount2);
                    // adding contact to contact list
                    writeThingsList.add(sthings);
                    wListView.setAdapter(adapter);
                    count++;
                    adapter.notifyDataSetChanged();
                }
                if(count==0){
                    Toast.makeText(SharedBookComunity.this,"작성된 글이 없습니다.",Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
                //lv.setAdapter(adapter);


            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private void showBottom() {

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        View view = bottomNavigationView.findViewById(R.id.action_book);
        view.performClick();

        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.action_search:
                        Intent searchIntent = new Intent(SharedBookComunity.this, MapsActivity2.class);
                        startActivity(searchIntent);
                        //finish();
                        return true;

                    case R.id.action_home:
                        Intent homeIntent = new Intent(SharedBookComunity.this, MainActivity.class);
                        startActivity(homeIntent);
                        //finish();
                        return true;
                    case R.id.action_book:
                        Intent myIntent = new Intent(SharedBookComunity.this, MyBookshelfActivity.class);
                        startActivity(myIntent);
                        //finish();
                        return true;
                }
                return false;
            }
        });
    }
    class SecondPageTask extends AsyncTask<Void, Void, String> {

        String target;

        @Override
        protected void onPreExecute() {
            try {
                target = "http://kye1898.cafe24.com/nsl/share_get_name.php?sTitle="+sT1;
                Log.d("target", target);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuffer stringBuilder = new StringBuffer();

                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");
                    Log.d("b", temp);
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return stringBuilder.toString().trim();

            } catch (Exception e) {

                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onProgressUpdate(Void... values) {
            super.onProgressUpdate();
        }

        @Override
        public void onPostExecute(String result) {
            try {

                writeThingsList.clear();
                JSONObject jsonObject = new JSONObject(result);
                JSONArray rows = jsonObject.getJSONArray("response");
                int count = 0;
                count = rows.length();
                for (int i = 0; i < rows.length(); i++) {
                    JSONObject c = rows.getJSONObject(i);

                    String sid = c.getString("sid");
                    String userID = c.getString("userID");
                    String sTitle = c.getString("sTitle");
                    String author = c.getString("author");
                    String publisher = c.getString("publisher");
                    String sContent = c.getString("sContent");
                    String likeCount2 = c.getString("likeCount2");

                    SharedBookGet sthings = new SharedBookGet(Integer.valueOf(sid), userID, sTitle, author, publisher, sContent, likeCount2);
                    // adding contact to contact list
                    writeThingsList.add(sthings);
                    wListView.setAdapter(adapter);
                    count++;
                    adapter.notifyDataSetChanged();
                }
                if (count == 0) {
                    Toast.makeText(SharedBookComunity.this, "검색된 글이 없습니다.", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
                //lv.setAdapter(adapter);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
