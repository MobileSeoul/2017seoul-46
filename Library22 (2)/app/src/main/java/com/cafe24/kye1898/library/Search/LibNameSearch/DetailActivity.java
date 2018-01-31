package com.cafe24.kye1898.library.Search.LibNameSearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cafe24.kye1898.library.BottomNavigationViewHelper;
import com.cafe24.kye1898.library.MainActivity;
import com.cafe24.kye1898.library.MyBookshelf.MyBook.MyBookshelfActivity;
import com.cafe24.kye1898.library.R;
import com.cafe24.kye1898.library.Search.NearLibSearch.MapsActivity2;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback{
    private String TAG = DetailActivity.class.getSimpleName();

    String name,gu, address, tel, fax, homepage, type,close, xlocation,ylocation,found,borrow,member,howto,floor;
    TextView lname, lgu, laddress, ltel, lfax, lhomepage, ltype, lclose, lfound, lborrow, lmember, lhowto, lfloor, lx, ly;
    private GoogleMap gmap;
    MapView vmap;
    double dx,dy;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_item);
        Bundle bundle = this.getIntent().getExtras();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        showBottom();
        final ScrollView mainScroll = (ScrollView) findViewById(R.id.scroll);
        ImageView transparentImageView = (ImageView) findViewById(R.id.transparent_image);

        transparentImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        //Disallow ScrollView to intercept touch events.
                        mainScroll.requestDisallowInterceptTouchEvent(true);
                        return false;

                    case MotionEvent.ACTION_UP:
                        mainScroll.requestDisallowInterceptTouchEvent(false);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        mainScroll.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;

                }
            }
        });


        name = bundle.getString("l_name");
        gu = bundle.getString("l_gu");
        address = bundle.getString("l_address");
        tel = bundle.getString("l_tel");
        fax = bundle.getString("l_fax");
        homepage = bundle.getString("l_homepage");
        type = bundle.getString("l_type");
        close = bundle.getString("l_close");
        member = bundle.getString("l_member");
        found = bundle.getString("l_found");
        borrow = bundle.getString("l_borrow");
        howto = bundle.getString("l_howto");
        floor = bundle.getString("l_floor");
        xlocation = bundle.getString("l_x");
        ylocation = bundle.getString("l_y");

        dx = Double.parseDouble(xlocation);
        dy = Double.parseDouble(ylocation);

        lname = (TextView) findViewById(R.id.lname);
        lgu = (TextView) findViewById(R.id.lgu);
        laddress = (TextView) findViewById(R.id.laddress);
        ltel = (TextView) findViewById(R.id.ltel);
        lfax = (TextView) findViewById(R.id.lfax);
        lhomepage = (TextView) findViewById(R.id.lhomepage);
        ltype = (TextView) findViewById(R.id.ltype);
        lclose = (TextView) findViewById(R.id.lclose);
        lmember = (TextView) findViewById(R.id.lmember);
        lfound = (TextView) findViewById(R.id.lfound);
        lborrow = (TextView) findViewById(R.id.lborrow);
        lhowto = (TextView) findViewById(R.id.lhowto);
        lfloor = (TextView) findViewById(R.id.lfloor);


        if (bundle != null) {


            lname.setText(name);
            lgu.setText(gu);
            laddress.setText(address);
            ltel.setText(tel);
            lfax.setText(fax);
            lhomepage.setText(homepage);
            ltype.setText( type);
            lclose.setText(close);
            lfound.setText(found);
            lmember.setText(member);
            lborrow.setText(borrow);
            lhowto.setText(howto);
            lfloor.setText(floor);

            //lx.setText("x:\n"+xlocation);
            //ly.setText("y:\n"+ylocation);


        }
    }
  @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.

        LatLng library = new LatLng(dx, dy);
        googleMap.addMarker(new MarkerOptions().position(library)
                .title(name));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(library,16));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d("LatLng", latLng.latitude+"-"+latLng.longitude);
            }
        });
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
