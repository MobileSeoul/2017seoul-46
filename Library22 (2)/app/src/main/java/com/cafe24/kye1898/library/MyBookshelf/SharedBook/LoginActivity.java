package com.cafe24.kye1898.library.MyBookshelf.SharedBook;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private AlertDialog dialog;
    String loginId, loginPwd;
    String userID, userPassword;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        showBottom();
        TextView registerButton=(TextView)findViewById(R.id.registerButton);


        // 아이디 등록 버튼...
        registerButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Intent registerIntent=new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);

            }
        });

        final EditText idText=(EditText)findViewById(R.id.idText);
        final EditText passwordText=(EditText)findViewById(R.id.passwordText);
        final Button loginButton=(Button)findViewById(R.id.loginButton);

        SharedPreferences pref=getSharedPreferences("Game", Activity.MODE_PRIVATE);
        userID=pref.getString("userID",null);
        userPassword=pref.getString("userPassword",null);

        if(userID!=null && userPassword !=null){
            Response.Listener<String> responseList = new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    try {

                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");

                        if (success)
                        {

                            SharedPreferences pref = getSharedPreferences("Game", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("userID", userID);
                            editor.putString("userPassword", userPassword);
                            editor.apply();
                            editor.commit();

                            Log.d("userID after",userID);
                            Log.d("userPassword after",userPassword);



                            Intent intent = new Intent(LoginActivity.this, SharedBookComunity.class);
                            //intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                            //intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                            LoginActivity.this.startActivity(intent);
                            finish();

                        } else {

                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            dialog = builder.setMessage("계정을 다시 확인하세요.")
                                    .setNegativeButton("다시 시도", null)
                                    .create();
                            dialog.show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            LoginRequest loginRequest = new LoginRequest(userID, userPassword, responseList);
            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

            queue.add(loginRequest);

        }


        loginButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){

                    userID = idText.getText().toString();
                    userPassword = passwordText.getText().toString();

                    Response.Listener<String> responseList = new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {

                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");

                                if (success)
                                {
                                    SharedPreferences pref = getSharedPreferences("Game", Activity.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putString("userID", userID);
                                    editor.putString("userPassword", userPassword);
                                    editor.apply();
                                    editor.commit();

                                    Log.d("userID after",userID);
                                    Log.d("userPassword after",userPassword);

                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                    dialog = builder.setMessage("로그인에 성공했습니다.")
                                            .setPositiveButton("확인", null)
                                            .create();
                                    dialog.show();

                                    Intent intent = new Intent(LoginActivity.this, SharedBookComunity.class);
                                    LoginActivity.this.startActivity(intent);
                                    finish();
                                } else {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                    dialog = builder.setMessage("계정을 다시 확인하세요.")
                                            .setNegativeButton("다시 시도", null)
                                            .create();
                                    dialog.show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };


                    LoginRequest loginRequest = new LoginRequest(userID, userPassword, responseList);
                    RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

                    queue.add(loginRequest);
                }


        });

        //showBottom();
    }


    @Override
    protected void onStop(){
        super.onStop();;
        if(dialog!=null){
            dialog.dismiss();
            dialog=null;
        }
    }



    private void showBottom(){

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        View view = bottomNavigationView.findViewById(R.id.action_book);
        view.performClick();

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
    }

}
