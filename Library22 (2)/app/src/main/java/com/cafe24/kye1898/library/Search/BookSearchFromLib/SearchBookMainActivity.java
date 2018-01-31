package com.cafe24.kye1898.library.Search.BookSearchFromLib;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cafe24.kye1898.library.BottomNavigationViewHelper;
import com.cafe24.kye1898.library.MainActivity;
import com.cafe24.kye1898.library.MyBookshelf.MyBook.MyBookshelfActivity;
import com.cafe24.kye1898.library.R;
import com.cafe24.kye1898.library.Search.LibNameSearch.SearchName1;
import com.cafe24.kye1898.library.Search.NearLibSearch.MapsActivity2;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SearchBookMainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    Button btn_search_book;
    EditText book_edit;
    String book;
    private ListView mRssListView;
    private BooksParser mNewsFeeder;
    private List<BooksParser> mNew;
    private List<BookFeed> mRssFeedList;
    private List<BookFeed> mRssFeedList2=new ArrayList<>();
    private RssAdapter mRssAdap;
    private Spinner spinnerf1;
    String typef1="";
    Button to_lib, to_book;
    private static final String TOPSTORIES =
            "http://www.dibrary.net/search/xml/searchKolisNet.jsp?key=1fd652268b11225e4be884ed82f701ec&f1=";
    private static String TOPSTORIES2 ="&v1=";
    private static String TOPSTORIES3 =
            "&pageSize=100&Category=kolisNet&pageNum=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.books_feed_view2);

        showBottom();
        spinnerf1 = (Spinner)findViewById(R.id.f1);
        ArrayAdapter<String> spinnerTArrayAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.F1));
        spinnerf1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

                typef1 = spinnerf1.getItemAtPosition(position).toString();
                Log.i("TEST","strItem:::"+typef1);
                ((TextView)arg0.getChildAt(0)).setTextColor(Color.WHITE);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        mRssListView = (ListView) findViewById(R.id.rss_list_view);
        mRssFeedList = new ArrayList<BookFeed>();

        to_lib=(Button) findViewById(R.id.search_lib);
        to_book=(Button)findViewById(R.id.search_book_from_lib);

        to_lib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent to_lib= new Intent(SearchBookMainActivity.this,SearchName1.class);
                startActivity(to_lib);
                finish();
            }
        });
        to_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent to_book= new Intent(SearchBookMainActivity.this,SearchBookMainActivity.class);

                startActivity(to_book);
                finish();
            }
        });

        btn_search_book=(Button)findViewById(R.id.btn_search_book);

        btn_search_book.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                book_edit=(EditText) findViewById(R.id.book_name);

                book=book_edit.getText().toString();

                if(typef1.equals("전체")){
                    typef1="total";
                }
                else if(typef1.equals("제목")){
                    typef1="title";
                }
                else if(typef1.equals("키워드")){
                    typef1="keyword";
                }
                else if(typef1.equals("작가")){
                    typef1="author";
                }
                else if(typef1.equals("출판사")){
                    typef1="publisher";
                }
                else if(typef1.equals("초록 키워드")){
                    typef1="abs_keyword";
                }
                else if(typef1.equals("목차 키워드")){
                    typef1="toc_keyword";
                }

                try{
                    typef1=URLEncoder.encode(typef1,"UTF-8");
                }catch (UnsupportedEncodingException e){

                }
                try {
                    book = URLEncoder.encode(book,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                mRssFeedList2.clear();
                Log.d("error11111111111:",typef1+book);
                new DoRssFeedTask().execute(TOPSTORIES+typef1+TOPSTORIES2+book+TOPSTORIES3);

            }


        });
        spinnerf1.setAdapter(spinnerTArrayAdapter2);
        //mRssListView.setOnItemClickListener(this);
    }

    private class RssAdapter extends ArrayAdapter<BookFeed> {
        private List<BookFeed> rssFeedLst;

        public RssAdapter(Context context, int textViewResourceId, List<BookFeed> rssFeedLst) {
            super(context, textViewResourceId, rssFeedLst);
            this.rssFeedLst = rssFeedLst;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = convertView;
            RssHolder rssHolder = null;
            if (convertView == null) {
                view = View.inflate(SearchBookMainActivity.this, R.layout.books_list_item, null);
                rssHolder = new RssHolder();
                rssHolder.rssTitleView = (TextView) view.findViewById(R.id.rss_title_view);
                view.setTag(rssHolder);
            } else {
                rssHolder = (RssHolder) view.getTag();
            }
            BookFeed rssFeed = rssFeedLst.get(position);
            rssHolder.rssTitleView.setText(rssFeed.getTitle_info());
            return view;
        }
    }

    static class RssHolder {
        public TextView rssTitleView;
    }

    public class DoRssFeedTask extends AsyncTask<String, Void, List<BookFeed>> {
        ProgressDialog prog;

        @Override
        protected void onPreExecute() {
            prog = new ProgressDialog(SearchBookMainActivity.this);
            prog.setMessage("Loading....");
            prog.show();
        }

        @Override
        protected List<BookFeed> doInBackground(String... params) {

            /*
            for (String urlVal : params) {
                    //String a=Integer.toString(i);
                    mNewsFeeder = new BooksParser(urlVal);
            }
            mRssFeedList = mNewsFeeder.parse();*/

            //최대 500개 출력가능하므로
            //page5까지 한꺼번에 합치도록 만듬 ㅎㅎ
            for(int i=1;i<6;i++){
                String ii=Integer.toString(i);
                for(String url:params){
                    mNewsFeeder = new BooksParser(url + ii);
                }
                mRssFeedList = mNewsFeeder.parse();

                for(BookFeed a:mRssFeedList){
                    mRssFeedList2.add(a);
                }
            }

            return mRssFeedList2;
        }

        @Override
        protected void onPostExecute(final List<BookFeed> result) {
            prog.dismiss();
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mRssAdap = new RssAdapter(SearchBookMainActivity.this, R.layout.books_list_item,
                            mRssFeedList2);
                    int count = mRssAdap.getCount();

                    if (count ==0){
                        //mRssListView.clear();
                        mRssListView.setAdapter(mRssAdap);
                        Toast.makeText(SearchBookMainActivity.this,"검색 결과가 없습니다.",Toast.LENGTH_LONG).show();

//                        if(mRssFeedList2.size()<1){
//                            Toast.makeText(SearchBookMainActivity.this,"검색 결과가 없습니다.",Toast.LENGTH_LONG).show();
//                        }
                    }
                    if (count != 0 && mRssAdap != null) {
                        mRssListView.setAdapter(mRssAdap);

                        if(!mRssAdap.isEmpty()){
                            mRssListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    String manage_code,title_info,author_info,pub_info,manage_name,detaillink,getid;
                                    Intent intent = new Intent(SearchBookMainActivity.this, DetailBookActivity.class);
                                    Bundle bundle = new Bundle();

                                    BookFeed bookfeed=(BookFeed) mRssListView.getItemAtPosition(position);

                                    manage_code = bookfeed.getManage_code();
                                    title_info =bookfeed.getTitle_info();
                                    author_info = bookfeed.getAuthor_info();
                                    pub_info = bookfeed.getPub_info();
                                    manage_name = bookfeed.getManage_name();
                                    detaillink =  bookfeed.getDetailLink();
                                    getid =  bookfeed.getId();

                                    bundle.putString("l_manage_code", manage_code);
                                    bundle.putString("l_title_info", title_info);
                                    bundle.putString("l_author_info", author_info);
                                    bundle.putString("l_pub_info", pub_info);
                                    bundle.putString("l_manage_name", manage_name);
                                    bundle.putString("l_detaillink", detaillink);
                                    bundle.putString("l_getid", getid);

                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                }
            });
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
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