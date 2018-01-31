package com.cafe24.kye1898.library.MyBookshelf.MyBook.BookParsing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cafe24.kye1898.library.MyBookshelf.MyBook.AddActivity;
import com.cafe24.kye1898.library.MyBookshelf.MyBook.Book;
import com.cafe24.kye1898.library.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchName_bookParsing extends AppCompatActivity {
    private String TAG = SearchName_bookParsing.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;
    Button search_button;
    String encodedName="";
    private ArrayAdapter TAdapter;
    private Spinner spinnerT, spinnerS;
    private ArrayAdapter SAdapter;
    private Spinner SSpinner;
    String type="";
    String sort = "";
    static int page=1;

    private static String url_type;
    private static String url_sort;
    private static String url_search;

    //private static String url2="http://openapi.seoul.go.kr:8088/414646414668656536314768564362/json/SeoulSmallLibrary/1/955/";

    ArrayList<HashMap<String, String>> LargeList;
    ArrayList<HashMap<String, String>> mBackupData=new ArrayList<>();
    ArrayList<HashMap<String, String>> ResultData=new ArrayList<>();

    int flag=0;
    int find=0;
    Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_name_aladin2);

        LargeList = new ArrayList<>();

        search_button=(Button)findViewById(R.id.button_search);
        lv = (ListView) findViewById(R.id.list);

        spinnerT = (Spinner)findViewById(R.id.QueryType);
        ArrayAdapter<String> spinnerTArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.QueryType));
        spinnerT.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

                type = spinnerT.getItemAtPosition(position).toString();
                Log.i("TEST","strItem:::"+type);
                ((TextView)arg0.getChildAt(0)).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        spinnerT.setAdapter(spinnerTArrayAdapter);


        spinnerS = (Spinner)findViewById(R.id.Sort);
        ArrayAdapter<String> spinnerSArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.Sort));

        spinnerS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

                sort = spinnerS.getItemAtPosition(position).toString();
                Log.i("TEST","strItem:::"+sort);
                ((TextView)arg0.getChildAt(0)).setTextColor(Color.WHITE);

            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        spinnerS.setAdapter(spinnerSArrayAdapter);

        Button before=(Button)findViewById(R.id.beforebtn);
        Button after=(Button)findViewById(R.id.afterbtn);


        before.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if(page>1) {
                    Log.d("page:",String.valueOf(page));
                    page = page - 1;
                    EditText park_name = (EditText) findViewById(R.id.park_name22);
                    String library_name = park_name.getText().toString();
                    encodedName = library_name;
                    LargeList.clear();
                    new GetContacts().execute();
                }
                else{
                    Toast.makeText(SearchName_bookParsing.this,"첫번째 페이지 입니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });
        after.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {

                if(LargeList.size()<15){
                    Toast.makeText(SearchName_bookParsing.this,"마지막 페이지 입니다.",Toast.LENGTH_LONG).show();
                }
                else{
                    page=page+1;
                    EditText park_name=(EditText)findViewById(R.id.park_name22);
                    String library_name=park_name.getText().toString();
                    encodedName=library_name;
                    LargeList.clear();
                    new GetContacts().execute();
                }


            }
        });



        search_button.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){


                page=1;
                EditText park_name=(EditText)findViewById(R.id.park_name22);
                String library_name=park_name.getText().toString();
                encodedName=library_name;
                LargeList.clear();
                new GetContacts().execute();
                //ResultData.clear();

            }
        });

        Intent aintent = getIntent();
        find=aintent.getIntExtra("find",0);
        flag=aintent.getIntExtra("flag",0);
        book=(Book) aintent.getSerializableExtra("bookdata");
    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(SearchName_bookParsing.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            //HttpHandler sh2 = new HttpHandler();
            // Making a request to url and getting response
            String number=String.valueOf(page);
            Log.d("num::::::",number);

            if(type.equals("지은이")){
                type="Author";
            }
            else if(type.equals("제목")){
                type="Title";
            }
            else if(type.equals("출판사")){
                type="Publisher";
            }

            if(sort.equals("판매량순")){
                sort="SalesPoint";
            }
            else if(sort.equals("출판일순")){
                sort="PublishTime";
            }
            else if(sort.equals("이름순")){
                sort="Title";
            }
            else if(sort.equals("별점순")){
                sort="CustomerRating";
            }
            else if(sort.equals("리뷰순")){
                sort="MyReviewCount";
            }


            Log.d("type",type);
            String url = "http://www.aladin.co.kr/ttb/api/search.aspx?" +
                    "TTBKey=ttbnahejae5332339001&" +
                    "QueryType="+type+
                    "&SearchTarget=Book" +
                    "&Sort=" +sort+
                    "&MaxResults=15" +
                    "&Output=js" +
                    "&Start="+page+
                    "&Query=";

            try {
                encodedName = URLEncoder.encode(encodedName,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String jsonStr = sh.makeServiceCall(url+encodedName);

            jsonStr=jsonStr.replace("\"publisher\":","\"publisher\": \"");
            jsonStr=jsonStr.replace(", \"itemPage\"","\", \"itemPage\"");
            Log.e(TAG, "Response from url: " + jsonStr);
            //Toast.makeText(SearchName_bookParsing.this,"hello",Toast.LENGTH_LONG).show();
            if (jsonStr != null) {
                try {

                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray rows=jsonObj.getJSONArray("item");

                    //JSONArray rows=largelibrary.getJSONArray("row");
                    // looping through All Contacts



                    for (int i = 0; i < rows.length(); i++) {


                        JSONObject c = rows.getJSONObject(i);

                        String title = c.getString("title");
                        String author = c.getString("author");
                        String pubDate=c.getString("pubDate");
                        String description = c.getString("description");
                        String creator = c.getString("creator");
                        String isbn = c.getString("isbn");
                        String cover=c.getString("cover");
                        String categoryName=c.getString("categoryName");
                        String publisher=c.getString("publisher");
                        String itemPage=c.getString("itemPage");
                        String customerReviewRank=c.getString("customerReviewRank");


                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("title",title);
                        contact.put("author", author);
                        contact.put("pubDate", pubDate);
                        contact.put("description",description);
                        contact.put("creator",creator);
                        contact.put("isbn",isbn);
                        contact.put("cover",cover);
                        contact.put("categoryName",categoryName);
                        contact.put("publisher",publisher);
                        contact.put("itemPage",itemPage);
                        contact.put("customerReviewRank",customerReviewRank);

                        //new DownloadTask();
                        
                        // adding contact to contact list
                        LargeList.add(contact);

                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG);

                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            if(LargeList.isEmpty()){

            }
            //mBackupData.addAll(LargeList);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             *
             * String image_url = (String) data.get("image_url");
             Picasso.with(mContext).load(image_url).into(image);
             * */
            ListAdapter adapter = new MyAdapter(
                    SearchName_bookParsing.this,
                    LargeList,
                    R.layout.list_item2_aladin,
                    new String[]{"title", "author","publisher","pubDate","description","cover"},
                    new int[]{R.id.book_title, R.id.book_author, R.id.book_pub, R.id.book_pubdate, R.id.book_info, R.id.book_image});


            lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                    String title, author, pubDate, description, creator, isbn, cover,categoryName, publisher, itemPage, customerReviewRank;
                    Bundle bundle = new Bundle();
                    HashMap<String, String> park = LargeList.get(position);
                    title=park.get("title");
                    author=park.get("author");
                    pubDate=park.get("pubDate");
                    description=park.get("description");
                    creator=park.get("creator");
                    isbn=park.get("isbn");
                    cover=park.get("cover");
                    categoryName=park.get("categoryName");
                    publisher=park.get("publisher");
                    itemPage=park.get("itemPage");
                    customerReviewRank=park.get("customerReviewRank");

                    if(find==1){
                        Intent addIntent = new Intent(getApplicationContext(), AddActivity.class);
                        book.setName(title);
                        book.setAuthor(author);
                        book.setPublisher(publisher);
                        addIntent.putExtra("bookdata",book);
                        addIntent.putExtra("flag",flag);
                        addIntent.putExtra("fromSearch",1);
                        addIntent.putExtra("url",cover);
                        Log.d("urlaaaaa",cover);
                        startActivity(addIntent);
                        finish();

                    }
                }
            });
            lv.setAdapter(adapter);

        }


    }


    class HttpHandler {

        private final String TAG = HttpHandler.class.getSimpleName();

        public HttpHandler() {
        }

        public String makeServiceCall(String reqUrl) {
            String response = null;
            try {
                URL url = new URL(reqUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                // read the response
                InputStream in = new BufferedInputStream(conn.getInputStream());
                response = convertStreamToString(in);
                //response=response.replaceAll("\\\'","'");
            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException: " + e.getMessage());
            } catch (ProtocolException e) {
                Log.e(TAG, "ProtocolException: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
            return response;
        }

        private String convertStreamToString(InputStream is) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }
    }
}

