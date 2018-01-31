package com.cafe24.kye1898.library.MyBookshelf.SharedBook;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.cafe24.kye1898.library.BottomNavigationViewHelper;
import com.cafe24.kye1898.library.MainActivity;
import com.cafe24.kye1898.library.MyBookshelf.MyBook.MyBookshelfActivity;
import com.cafe24.kye1898.library.R;
import com.cafe24.kye1898.library.Search.NearLibSearch.MapsActivity2;

import org.json.JSONObject;

public class ShareEditActivity extends AppCompatActivity {

    private ArrayAdapter adapter;
    private String userID;
    private AlertDialog dialog;
    //private boolean validate=false;
    String title, wContent;
    String t1, c1;
    String a1, p1;
    EditText econtent;
    String wid;
    String www;
    TextView jemok, jakka, julpan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_to_write);
        showBottom();
        SharedPreferences pref=getSharedPreferences("Game", Activity.MODE_PRIVATE);
        userID=pref.getString("userID",null);


        jemok=(TextView) findViewById(R.id.jemok); //제목
        jakka=(TextView)findViewById(R.id.jakka);  //작가
        julpan=(TextView)findViewById(R.id.julpan);//출판사

        econtent=(EditText)findViewById(R.id.sssContent);//내용

        Intent intent=getIntent();
        www=intent.getStringExtra("sid");
        t1=intent.getStringExtra("title");
        a1=intent.getStringExtra("author");
        c1=intent.getStringExtra("content");
        p1=intent.getStringExtra("publisher");


        jemok.setText(t1);
        jakka.setText(a1);
        julpan.setText(p1);
        econtent.setText(c1);

        Button wButton=(Button)findViewById(R.id.wButton);
        wButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                //Toast.makeText(WriteSomethingActivity.this,"clicked",Toast.LENGTH_SHORT).show();


                title=jemok.getText().toString();
                wContent=econtent.getText().toString();

                if(title.equals("")||wContent.equals("")){
                    AlertDialog.Builder builder=new AlertDialog.Builder(ShareEditActivity.this);
                    dialog=builder.setMessage("빈칸 없이 입력해 주세요.")
                            .setNegativeButton("확인",null)
                            .create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListener=new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        try
                        {
                            JSONObject jsonResponse=new JSONObject(response);
                            boolean success=jsonResponse.getBoolean("success");
                            if(success)
                            {
                                AlertDialog.Builder builder=new AlertDialog.Builder(ShareEditActivity.this);
                                dialog=builder.setMessage("성공했습니다..")
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener(){

                                            public void onClick(DialogInterface dialog, int id){

                                                Intent gotomain=new Intent(ShareEditActivity.this,SharedBookComunity.class);
                                                startActivity(gotomain);
                                                finish();
                                            }
                                        })
                                        .create();
                                dialog.show();

                                //

                            }
                            else{
                                AlertDialog.Builder builder=new AlertDialog.Builder(ShareEditActivity.this);
                                dialog=builder.setMessage("실패했습니다.")
                                        .setNegativeButton("확인",null)
                                        .create();
                                dialog.show();
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();

                        }
                    }
                };
                ShareEditRequest seRequest=new ShareEditRequest(www, wContent, responseListener);
                RequestQueue queue = Volley.newRequestQueue(ShareEditActivity.this);
                queue.add(seRequest);
            }
        });
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

    @Override
    protected void onStop(){
        super.onStop();
        if(dialog!=null)
        {
            dialog.dismiss();
            dialog=null;
        }
    }
}

