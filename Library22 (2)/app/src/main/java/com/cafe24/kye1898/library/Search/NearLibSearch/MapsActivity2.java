package com.cafe24.kye1898.library.Search.NearLibSearch;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.cafe24.kye1898.library.BottomNavigationViewHelper;
import com.cafe24.kye1898.library.MainActivity;
import com.cafe24.kye1898.library.MyBookshelf.MyBook.MyBookshelfActivity;
import com.cafe24.kye1898.library.R;
import com.cafe24.kye1898.library.Search.BookSearchFromLib.SearchBookMainActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
import java.util.ArrayList;
import java.util.HashMap;
import com.cafe24.kye1898.library.Search.LibNameSearch.*;

public class MapsActivity2 extends FragmentActivity  implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private String TAG = MapsActivity2.class.getSimpleName();
    //private GoogleMap mMap;
    private GoogleMap gMap;
    //private GoogleApiClient mGoogleApiClient;
    //private LocationListener listener;
    //private LocationManager locationManager;
    private static String url = "http://openapi.seoul.go.kr:8088/6d4f4f456968656537384164487449/json/SeoulPublicLibrary/2/155/";
    private static String url2 = "http://openapi.seoul.go.kr:8088/414646414668656536314768564362/json/SeoulSmallLibrary/1/955/";
    private ProgressDialog pDialog;
    String encodedName = "";
    ArrayList<HashMap<String, String>> LargeList;
    ArrayList<HashMap<String, String>> mBackupData = new ArrayList<>();
    ArrayList<HashMap<String, String>> ResultData = new ArrayList<>();

    static double current_x=0, current_y=0;
    EditText park_name;
    Button search_button;
    private ListView lv;
    Button to_lib,to_book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapmap);
        mapFragment.getMapAsync(this);

        showBottom();

//        if(!checkPermissions()){
//            this.finish();
//            Intent searchIntent = new Intent(getApplicationContext(),MapsActivity2.class);
//            searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(searchIntent);
//
//        }


        LargeList = new ArrayList<>();
        LargeList.clear();

        new GetContacts().execute();
        park_name=(EditText)findViewById(R.id.park_name22);
        search_button=(Button)findViewById(R.id.button_search);

        lv = (ListView) findViewById(R.id.list);

        to_lib=(Button) findViewById(R.id.search_lib);
        to_book=(Button)findViewById(R.id.search_book_from_lib);

        to_lib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent to_lib= new Intent(MapsActivity2.this,SearchName1.class);
                startActivity(to_lib);
                finish();
            }
        });
        to_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent to_book= new Intent(MapsActivity2.this,SearchBookMainActivity.class);
                startActivity(to_book);
                finish();
            }
        });

        search_button.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){


                String library_name=park_name.getText().toString();

                if(library_name.isEmpty()){
                    Toast.makeText(MapsActivity2.this,"검색어를 입력해 주세요.",Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(MapsActivity2.this, SearchName2.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("l_name", library_name);


                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }

            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    private boolean checkPermissions() {
        int result;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.INTERNET}, 1);

            //Toast.makeText(this, "Search 버튼을 한번 더 눌러주세요.", Toast.LENGTH_LONG).show();

            return false;
        }
        else{
            return true;
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        //boolean dd=checkPermissions();

        if (!gMap.isMyLocationEnabled()) {

//
           if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.INTERNET}, 1);


               //Toast.makeText(this, "Grant", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "Search 버튼을 한번 더 눌러주세요.", Toast.LENGTH_LONG).show();

                //return;


            }else {
                Criteria criteria = new Criteria();
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                String provider = locationManager.getBestProvider(criteria, false);
                Location location = locationManager.getLastKnownLocation(provider);
                double la, lo;

                if (location != null) {
                    la = location.getLatitude();
                    lo = location.getLongitude();
                    //Toast.makeText(this, la+"______________"+lo, Toast.LENGTH_LONG).show();
                    LatLng TutorialsPoint = new LatLng(la, lo);

                    float hue = 210 ;//0080ff
                    gMap.addMarker(new
                            MarkerOptions().position(TutorialsPoint).title("현재 위치")
                            .icon(BitmapDescriptorFactory.defaultMarker(hue))

                    );

                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(TutorialsPoint, 14));

                    current_x = la;
                    current_y = lo;
                    Circle circle = gMap.addCircle(new CircleOptions().center(TutorialsPoint).radius(1150).strokeColor(Color.DKGRAY).fillColor(0x4DA6A6A0));


                } else {

                    float hue = 210 ;//0080ff
                    //LatLng isu = new LatLng(37.4853810, 126.9821390);
                    // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(isu,14));}}
                    LatLng TutorialsPoint = new LatLng(37.4853810, 126.9821390);
                    gMap.addMarker(new
                            MarkerOptions().position(TutorialsPoint).title("기본 위치")
                            .icon(BitmapDescriptorFactory.defaultMarker(hue))

                    );
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(TutorialsPoint, 14));

                    current_x = 37.4853810;
                    current_y = 126.9821390;
                    Circle circle = gMap.addCircle(new CircleOptions().center(TutorialsPoint).radius(1150).strokeColor(Color.DKGRAY).fillColor(0x4DA6A6A0));

                    //Circle circle=gMap.addCircle(new CircleOptions().center(TutorialsPoint).radius(1000).strokeColor(Color.BLUE).fillColor(0x100000ff));
                    Toast.makeText(this, "GPS를 잡을 수 없습니다. 기본으로 설정된 지도가 보입니다.", Toast.LENGTH_LONG).show();

                }

            }
        }

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Info window clicked",
                Toast.LENGTH_SHORT).show();
    }



    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MapsActivity2.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            HttpHandler sh2 = new HttpHandler();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url + encodedName);
            String jsonStr2 = sh2.makeServiceCall(url2 + encodedName);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr2 != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr2);
                    // Getting JSON Array node
                    JSONObject largelibrary = jsonObj.getJSONObject("SeoulSmallLibrary");
                    JSONArray rows = largelibrary.getJSONArray("row");

                    // looping through All Contacts
                    for (int i = 0; i < rows.length(); i++) {
                        JSONObject c = rows.getJSONObject(i);

                        String name = c.getString("LBRRY_NAME");
                        String GU = c.getString("CODE_VALUE");
                        String address = c.getString("ADRES");
                        String tel = c.getString("TEL_NO");
                        String fax = c.getString("FXNUM");
                        String homepage = c.getString("HMPG_URL");
                        String type = c.getString("LBRRY_SE_NAME");
                        String close = c.getString("FDRM_CLOSE_DATE");
                        String member = c.getString("MBER_SBSCRB_RQISIT");
                        String found = c.getString("FOND_YEAR");
                        String borrow = c.getString("LON_GDCC");
                        String howto = c.getString("TFCMN");
                        String floor = c.getString("FLOOR_DC");
                        String xlocation = c.getString("XCNTS");
                        String ylocation = c.getString("YDNTS");

                        //String plo=c.getString("LONGITUDE");
                        // String pla=c.getString("LATITUDE");

                        //Log.e(TAG, "id: "+id+"name:"+name+"detail"+detail+"address:"+address+"img:"+pimg+"lotitude:"+plo+"latitude:"+pla+"\n");

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("name", name);
                        contact.put("gu", GU);
                        contact.put("address", address);
                        contact.put("tel", tel);
                        contact.put("fax", fax);
                        contact.put("homepage", homepage);
                        contact.put("type", type);
                        contact.put("close", close);
                        contact.put("member", member);
                        contact.put("found", found);
                        contact.put("borrow", borrow);
                        contact.put("howto", howto);
                        contact.put("floor", floor);
                        contact.put("xlocation", xlocation);
                        contact.put("ylocation", ylocation);

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
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    JSONObject largelibrary = jsonObj.getJSONObject("SeoulPublicLibrary");
                    JSONArray rows = largelibrary.getJSONArray("row");

                    // looping through All Contacts
                    for (int i = 0; i < rows.length(); i++) {
                        JSONObject c = rows.getJSONObject(i);

                        String name = c.getString("LBRRY_NAME");
                        String GU = c.getString("CODE_VALUE");
                        String address = c.getString("ADRES");
                        String tel = c.getString("TEL_NO");
                        String fax = c.getString("FXNUM");
                        String homepage = c.getString("HMPG_URL");
                        String type = c.getString("LBRRY_SE_NAME");
                        String close = c.getString("FDRM_CLOSE_DATE");
                        String member = c.getString("MBER_SBSCRB_RQISIT");
                        String found = c.getString("FOND_YEAR");
                        String borrow = c.getString("LON_GDCC");
                        String howto = c.getString("TFCMN");
                        String floor = c.getString("FLOOR_DC");
                        String xlocation = c.getString("XCNTS");
                        String ylocation = c.getString("YDNTS");

                        //String plo=c.getString("LONGITUDE");
                        // String pla=c.getString("LATITUDE");

                        //Log.e(TAG, "id: "+id+"name:"+name+"detail"+detail+"address:"+address+"img:"+pimg+"lotitude:"+plo+"latitude:"+pla+"\n");

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("name", name);
                        contact.put("gu", GU);
                        contact.put("address", address);
                        contact.put("tel", tel);
                        contact.put("fax", fax);
                        contact.put("homepage", homepage);
                        contact.put("type", type);
                        contact.put("close", close);
                        contact.put("member", member);
                        contact.put("found", found);
                        contact.put("borrow", borrow);
                        contact.put("howto", howto);
                        contact.put("floor", floor);
                        contact.put("xlocation", xlocation);
                        contact.put("ylocation", ylocation);

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

            mBackupData.addAll(LargeList);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();



            for (HashMap<String, String> map : LargeList) {

                String name, address, gu, tel, fax, homepage, type, close, member, found, borrow, howto, floor;
                Double xl, yl;
                name = map.get("name");

                xl = Double.parseDouble(map.get("xlocation"));
                yl = Double.parseDouble(map.get("ylocation"));
                LatLng libs = new LatLng(xl, yl);
                //float current_la;
                //float current_lo;
                address = map.get("address");
                if(current_x-0.008<xl&&xl<current_x+0.008&&current_y-0.008<yl&&yl<current_y+0.008) {
                    gMap.addMarker(new MarkerOptions().position(libs).title(name).snippet(address));
                }
            }


            gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {

                    for (HashMap<String, String> map : LargeList) {
                        String name, address, gu, tel, fax, homepage, type, close, member, found, borrow, howto, floor;
                        Double xl, yl;
                        name = map.get("name");
                        xl = Double.parseDouble(map.get("xlocation"));
                        yl = Double.parseDouble(map.get("ylocation"));
                        LatLng libs = new LatLng(xl, yl);
                        address = map.get("address");
                        gu = map.get("gu");
                        tel = map.get("tel");
                        fax = map.get("fax");
                        homepage = map.get("homepage");
                        type = map.get("type");
                        close = map.get("close");
                        member = map.get("member");
                        found = map.get("found");
                        borrow = map.get("borrow");
                        howto = map.get("howto");
                        floor = map.get("floor");


                        if(marker.getPosition().equals(libs)) {
                            Intent intent = new Intent(MapsActivity2.this, DetailActivity.class);
                            Bundle bundle = new Bundle();


                            bundle.putString("l_name", name);
                            bundle.putString("l_gu", gu);
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
                            bundle.putString("l_x", xl.toString());
                            bundle.putString("l_y", yl.toString());

                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }

                }
            });

        }
}

                /*
                gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener()
                {
                    @Override
                    public void onInfoWindowClick(Marker arg0) {
                        // call an activity(xml file)
                        Intent in = new Intent(MapsActivity2.this, SearchMainActivity.class);
                        startActivity(in);
                    }

                });*/


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



