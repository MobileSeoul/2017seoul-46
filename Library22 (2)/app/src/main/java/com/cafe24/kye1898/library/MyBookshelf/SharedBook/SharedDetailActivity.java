package com.cafe24.kye1898.library.MyBookshelf.SharedBook;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cafe24.kye1898.library.BottomNavigationViewHelper;
import com.cafe24.kye1898.library.MainActivity;
import com.cafe24.kye1898.library.MyBookshelf.MyBook.MyBookshelfActivity;
import com.cafe24.kye1898.library.R;
import com.cafe24.kye1898.library.Search.NearLibSearch.MapsActivity2;

public class SharedDetailActivity extends AppCompatActivity {

    String name,gu, address, tel, fax;
    TextView lname, lgu, laddress, ltel, lfax;

    String ttt,uuu,aaa,ppp,ccc;
    TextView tt,uu,aa,pp,cc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shared_detail_item);

        showBottom();
        Bundle bundle = this.getIntent().getExtras();

        ttt=bundle.getString("ttt");
        uuu=bundle.getString("uuu");
        aaa=bundle.getString("aaa");
        ppp=bundle.getString("ppp");
        ccc=bundle.getString("ccc");

        tt=(TextView)findViewById(R.id.shared_sTitle);
        uu=(TextView)findViewById(R.id.shared_userid);
        aa=(TextView)findViewById(R.id.shared_author);
        pp=(TextView)findViewById(R.id.shared_publisher);
        cc=(TextView)findViewById(R.id.sContent);

//        name = bundle.getString("l_name");
//        gu = bundle.getString("l_gu");
//        address = bundle.getString("l_address");
//        tel = bundle.getString("l_tel");
//        fax = bundle.getString("l_fax");
//
//        lname = (TextView) findViewById(R.id.lname);
//        lgu = (TextView) findViewById(R.id.lgu);
//        laddress = (TextView) findViewById(R.id.laddress);
//        ltel = (TextView) findViewById(R.id.ltel);
//        lfax = (TextView) findViewById(R.id.lfax);
//
//
//
//
        if (bundle != null) {

            tt.setText(ttt);
            uu.setText(uuu);
            aa.setText(aaa);
            pp.setText(ppp);
            cc.setText(ccc);

//
//
//            lname.setText(name);
//            lgu.setText("자치구명: "+gu);
//            laddress.setText("주소: "+address);
//            ltel.setText("전화 번호: "+tel);
//            lfax.setText("팩스 번호 :"+fax);
//
//
//
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
