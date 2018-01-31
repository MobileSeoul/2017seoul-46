package com.cafe24.kye1898.library.Search.LibNameSearch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.cafe24.kye1898.library.BottomNavigationViewHelper;
import com.cafe24.kye1898.library.MainActivity;
import com.cafe24.kye1898.library.MyBookshelf.MyBook.MyBookshelfActivity;
import com.cafe24.kye1898.library.R;
import com.cafe24.kye1898.library.Search.BookSearchFromLib.SearchBookMainActivity;
import com.cafe24.kye1898.library.Search.NearLibSearch.MapsActivity2;

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


public class SearchName1 extends AppCompatActivity {
    private String TAG = SearchName1.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;
    Button search_button;
    String encodedName="";
    EditText park_name;


    private static String url = "http://kye1898.cafe24.com/nsl/lib_seoul_public_lib.php?LBRRY_NAME=";

    ArrayList<HashMap<String, String>> LargeList;
    //ArrayList<HashMap<String, String>> mBackupData=new ArrayList<>();
    ArrayList<HashMap<String, String>> ResultData=new ArrayList<>();
    String fname,faddress;
    int flag;
    Button to_lib,to_book;
    String library_name;
    String lib_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_name);
        //Bundle bundle = this.getIntent().getExtras();
        //String lib_name = bundle.getString("l_name");
        lv = (ListView) findViewById(R.id.list);
        showBottom();
        LargeList = new ArrayList<>();


        search_button=(Button)findViewById(R.id.button_search);
        to_lib=(Button) findViewById(R.id.search_lib);
        to_book=(Button)findViewById(R.id.search_book_from_lib);

        park_name=(EditText)findViewById(R.id.park_name22);
        library_name=park_name.getText().toString();




        to_lib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent to_lib= new Intent(SearchName1.this,SearchName1.class);
                startActivity(to_lib);
                finish();
            }
        });
        to_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent to_book= new Intent(SearchName1.this,SearchBookMainActivity.class);
                startActivity(to_book);
                finish();
            }
        });

        search_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                String library_name=park_name.getText().toString();
                try {
                    library_name = URLEncoder.encode(library_name,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                encodedName=library_name;

                LargeList.clear();
                new GetContacts().execute();

                if(library_name.isEmpty()){
                    Toast.makeText(SearchName1.this,"검색어를 입력해 주세요.",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(SearchName1.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(url+encodedName);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    JSONArray rows=jsonObj.getJSONArray("response");

                    // looping through All Contacts
                    for (int i = 0; i < rows.length(); i++) {
                        JSONObject c = rows.getJSONObject(i);
                        String name = c.getString("LBRRY_NAME");
                        String GU = c.getString("CODE_VALUE");
                        String address=c.getString("ADRES");
                        String tel = c.getString("TEL_NO");
                        String fax = c.getString("FXNUM");
                        String homepage = c.getString("HMPG_URL");
                        String type=c.getString("LBRRY_SE_NAME");
                        String close=c.getString("FDRM_CLOSE_DATE");
                        String member=c.getString("MBER_SBSCRB_RQISIT");
                        String found=c.getString("FOND_YEAR");
                        String borrow=c.getString("LON_GDCC");
                        String howto=c.getString("TFCMN");
                        String floor=c.getString("FLOOR_DC");
                        String xlocation=c.getString("XCNTS");
                        String ylocation=c.getString("YCNTS");
                        String lib_code=c.getString("lib_code");
                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("name",name);
                        contact.put("gu", GU);
                        contact.put("address", address);
                        contact.put("tel",tel);
                        contact.put("fax",fax);
                        contact.put("homepage",homepage);
                        contact.put("type",type);
                        contact.put("close",close);
                        contact.put("member",member);
                        contact.put("found",found);
                        contact.put("borrow",borrow);
                        contact.put("howto",howto);
                        contact.put("floor",floor);
                        contact.put("xlocation",xlocation);
                        contact.put("ylocation",ylocation);

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
                                    Toast.LENGTH_LONG)
                                    .show();
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
             * */
            ListAdapter adapter = new SimpleAdapter(
                    SearchName1.this, LargeList,
                    R.layout.list_item, new String[]{"name", "address"}, new int[]{R.id.name, R.id.address});


            lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                    String name,gu,address,tel,fax,homepage,type,close,member,found,borrow,howto, floor;
                    String xlocation, ylocation;

                    Intent intent = new Intent(SearchName1.this, DetailActivity.class);
                    Bundle bundle = new Bundle();
                    HashMap<String, String> park = LargeList.get(position);
                    name=park.get("name");
                    gu=park.get("gu");
                    address=park.get("address");
                    tel=park.get("tel");
                    fax=park.get("fax");
                    homepage=park.get("homepage");
                    type=park.get("type");
                    close=park.get("close");
                    member=park.get("member");
                    found=park.get("found");
                    borrow=park.get("borrow");
                    howto=park.get("howto");
                    floor=park.get("floor");
                    xlocation=park.get("xlocation");
                    ylocation=park.get("ylocation");

                    bundle.putString("l_name", name);
                    bundle.putString("l_gu", gu);
                    bundle.putString("l_address", address);
                    bundle.putString("l_tel",tel);
                    bundle.putString("l_fax", fax);
                    bundle.putString("l_homepage", homepage);
                    bundle.putString("l_type", type);
                    bundle.putString("l_close", close);
                    bundle.putString("l_member",member);
                    bundle.putString("l_found",found);
                    bundle.putString("l_borrow",borrow);
                    bundle.putString("l_howto",howto);
                    bundle.putString("l_floor",floor);
                    bundle.putString("l_x",xlocation);
                    bundle.putString("l_y",ylocation);

                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            lv.setAdapter(adapter);
        }
    }


    public class HttpHandler {

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

    private void showBottom(){

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        View view = bottomNavigationView.findViewById(R.id.action_search);
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

