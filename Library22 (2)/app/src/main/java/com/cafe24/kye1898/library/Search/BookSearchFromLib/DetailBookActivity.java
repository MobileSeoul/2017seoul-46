package com.cafe24.kye1898.library.Search.BookSearchFromLib;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.cafe24.kye1898.library.BottomNavigationViewHelper;
import com.cafe24.kye1898.library.MainActivity;
import com.cafe24.kye1898.library.MyBookshelf.MyBook.MyBookshelfActivity;
import com.cafe24.kye1898.library.R;
import com.cafe24.kye1898.library.Search.LibNameSearch.DetailActivity;
import com.cafe24.kye1898.library.Search.LibNameSearch.SearchName1;
import com.cafe24.kye1898.library.Search.NearLibSearch.MapsActivity2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;


public class DetailBookActivity extends AppCompatActivity {
    private String TAG = DetailBookActivity.class.getSimpleName();

    String manage_code,title_info,author_info,pub_info,manage_name,detaillink,getid;
    TextView lmanage_code,ltitle_info,lauthor_info,lpub_info,lmanage_name,ldetaillink,lgetid;
    Button button_lib;
    private AlertDialog dialog;
    String lib_code1;
    //String name, GU, address,tel,fax,homepage,type,close,member,found,borrow,howto,floor,xlocation,ylocation,lib_code;
    private ProgressDialog pDialog;
    private static String url = "http://kye1898.cafe24.com/nsl/lib_.php?lib_code=";
    String name, GU, address,tel,fax,homepage,type,close,member,found,borrow,howto,floor,xlocation,ylocation,lib_code;
    Boolean check_tf=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.s_books_detail_item);
        showBottom();
        Bundle bundle = this.getIntent().getExtras();

        manage_code = bundle.getString("l_manage_code");
        title_info = bundle.getString("l_title_info");
        author_info = bundle.getString("l_author_info");
        pub_info = bundle.getString("l_pub_info");
        manage_name = bundle.getString("l_manage_name");
        detaillink = bundle.getString("l_detaillink");
        getid= bundle.getString("l_getid");
        Log.e("code: ",manage_code);

        ltitle_info = (TextView) findViewById(R.id.title_info);
        lauthor_info = (TextView) findViewById(R.id.author_info);
        lpub_info = (TextView) findViewById(R.id.pub_info);
        lmanage_name = (TextView) findViewById(R.id.manage_name);
        //ㅗㅛㅅlmanage_code = (TextView) findViewById(R.id.manage_code);
        ldetaillink = (TextView) findViewById(R.id.detaillink);
        //lgetid=(TextView)findViewById(R.id.getid);

        lib_code1=manage_code;
        if (bundle != null) {


            ltitle_info.setText(title_info);
            lauthor_info.setText(author_info);
            lpub_info.setText(pub_info);
            lmanage_name.setText(manage_name);
            ldetaillink.setText("http://www.dibrary.net"+detaillink);

        }

        button_lib=(Button)findViewById(R.id.button_lib);
        button_lib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new GetLib().execute();

            }
        });
    }

    private class GetLib extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
//            super.onPreExecute();
//            // Showing progress dialog
//            pDialog = new ProgressDialog(DetailBookActivity.this);
//            pDialog.setMessage("Please wait...");
//            pDialog.setCancelable(false);
//            //pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            DetailBookActivity.HttpHandler sh = new DetailBookActivity.HttpHandler();
            String jsonStr = sh.makeServiceCall(url+lib_code1);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    JSONArray rows=jsonObj.getJSONArray("response");
                    // looping through All Contacts

                    if (rows.length()!=0){
                        check_tf=true;
                    }
                        //String name, GU, address,tel,fax,homepage,type,close,member,found,borrow,howto,floor,xlocation,ylocation,lib_code;
                    if(check_tf==true){
                        for (int i = 0; i < rows.length(); i++) {
                            JSONObject c = rows.getJSONObject(i);
                            name = c.getString("LBRRY_NAME");
                            GU = c.getString("CODE_VALUE");
                            address = c.getString("ADRES");
                            tel = c.getString("TEL_NO");
                            fax = c.getString("FXNUM");
                            homepage = c.getString("HMPG_URL");
                            type = c.getString("LBRRY_SE_NAME");
                            close = c.getString("FDRM_CLOSE_DATE");
                            member = c.getString("MBER_SBSCRB_RQISIT");
                            found = c.getString("FOND_YEAR");
                            borrow = c.getString("LON_GDCC");
                            howto = c.getString("TFCMN");
                            floor = c.getString("FLOOR_DC");
                            xlocation = c.getString("XCNTS");
                            ylocation = c.getString("YCNTS");
                            lib_code = c.getString("lib_code");
                        }
                    }
                    else
                    {
                        return null;
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
//            if (pDialog.isShowing())
//                pDialog.dismiss();

                    //String name,gu,address,tel,fax,homepage,type,close,member,found,borrow,howto, floor;
                    //String xlocation, ylocation;
                    if(check_tf==true) {
                        Intent intent = new Intent(DetailBookActivity.this, DetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("l_name", name);
                        bundle.putString("l_gu", GU);
                        bundle.putString("l_address", address);
                        bundle.putString("l_tel", tel);
                        bundle.putString("l_fax", fax);
                        bundle.putString("l_homepage", homepage);
                        bundle.putString("l_type", type);
                        bundle.putString("l_close", close);
                        bundle.putString("l_member", member);
                        bundle.putString("l_found", found);
                        bundle.putString("l_borrow", borrow);
                        bundle.putString("l_howto", howto);
                        bundle.putString("l_floor", floor);
                        bundle.putString("l_x", xlocation);
                        bundle.putString("l_y", ylocation);

                        intent.putExtras(bundle);
                        startActivity(intent);
                    }else
                    {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                DetailBookActivity.this);

                        // 제목셋팅
                        //alertDialogBuilder.setTitle("프로그램 종료");

                        // AlertDialog 셋팅
                        alertDialogBuilder
                                .setMessage("도서관에 대한 정보가 없습니다.")
                                .setCancelable(false)

                                .setNegativeButton("확인",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog, int id) {
                                                // 다이얼로그를 취소한다
                                                dialog.cancel();
                                            }
                                        });

                        // 다이얼로그 생성
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // 다이얼로그 보여주기
                        alertDialog.show();
                        //Toast.makeText(DetailBookActivity.this,"찾는 도서관 정보가 없습니다.",Toast.LENGTH_LONG).show();
                    }
                }

    }
    public class HttpHandler {

        private final String TAG = SearchName1.HttpHandler.class.getSimpleName();

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
