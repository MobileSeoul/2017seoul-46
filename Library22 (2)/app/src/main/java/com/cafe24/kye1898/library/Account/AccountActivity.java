package com.cafe24.kye1898.library.Account;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.cafe24.kye1898.library.BookDatabase;
import com.cafe24.kye1898.library.BottomNavigationViewHelper;
import com.cafe24.kye1898.library.MainActivity;
import com.cafe24.kye1898.library.MyBookshelf.MyBook.MyBookshelfActivity;
import com.cafe24.kye1898.library.R;
import com.cafe24.kye1898.library.Search.NearLibSearch.MapsActivity2;

import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity {


    private static final String TAG = "AccountActivity";

    BookDatabase database2;

    public static String ACCOUNT_TABLE = "ACCOUNT";
    public static String TABLE_TOREAD = "READ";


    ToReadAdapter toReadAdapter;

    TextView goal;
    TextView alarm;

    int sel,al;

    ListView toReadListView;

    int select;
    int choice;

    String bookName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        showBottom();

        goal = (TextView)findViewById(R.id.goal);
        alarm = (TextView)findViewById(R.id.alarm);

        useDB();
        setAccount();
        displayToReadView();
        clickList();

    }

    private void changeDividerColor(NumberPicker picker, int color) {

        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(picker, colorDrawable);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
    public void onAlarmClicked(View v){

        final Dialog d2 = new Dialog(AccountActivity.this);
        d2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d2.setContentView(R.layout.numberpicker2_layout);

        Button setButton = (Button)d2.findViewById(R.id.cancel);
        Button cancelButton = (Button)d2.findViewById(R.id.set);

        final NumberPicker np2 = (NumberPicker)d2.findViewById(R.id.numberPicker2);
        np2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        np2.setMaxValue(9); np2.setMinValue(1); np2.setWrapSelectorWheel(true);
        np2.setValue(al);
        changeDividerColor(np2, Color.parseColor("#00ffffff"));

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d2.dismiss();
            }
        });

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num;
                num=np2.getValue();
                try {
                    database2.rawQuery("update " + ACCOUNT_TABLE + " set ALARM = '" + num + "' where ALARM = '" + al + "'");
                }catch(Exception e){
                    Log.e(TAG, "Exception in executing update SQL.", e);
                }
                setAccount();
                d2.dismiss();
            }
        });

        d2.show();


    }


    public void onGoalClicked(View v){

        final Dialog d = new Dialog(AccountActivity.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.numberpicker_layout);

        Button setButton = (Button)d.findViewById(R.id.cancel);
        Button cancelButton = (Button)d.findViewById(R.id.set);

        final NumberPicker np1 = (NumberPicker)d.findViewById(R.id.numberPicker1);
        final NumberPicker np2 = (NumberPicker)d.findViewById(R.id.numberPicker2);
        final NumberPicker np3 = (NumberPicker)d.findViewById(R.id.numberPicker3);

        np1.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np3.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        np1.setMaxValue(9); np2.setMaxValue(9); np3.setMaxValue(9);
        np1.setMinValue(0); np2.setMinValue(0); np3.setMinValue(0);

        int temp,temp1,temp2,temp3;
        temp=sel;
        temp1=temp/100;
        temp-=(temp1*100);
        temp2=temp/10;
        temp-=(temp2*10);
        temp3=temp;
        np1.setValue(temp1); np2.setValue(temp2); np3.setValue(temp3);

        np1.setWrapSelectorWheel(true); np2.setWrapSelectorWheel(true); np3.setWrapSelectorWheel(true);

        changeDividerColor(np1, Color.parseColor("#00ffffff"));
        changeDividerColor(np2, Color.parseColor("#00ffffff"));
        changeDividerColor(np3, Color.parseColor("#00ffffff"));

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int num;
                num=np1.getValue()*100+np2.getValue()*10+np3.getValue();

                try {
                    database2.rawQuery("update " + ACCOUNT_TABLE + " set GOAL = '" + num + "' where GOAL = '" + sel + "'");
                }catch(Exception e){
                    Log.e(TAG, "Exception in executing update SQL.", e);
                }
                setAccount();
                d.dismiss();
            }
        });

        d.show();

    }

    public void setAccount(){

        try{
            Cursor cursor = database2.rawQuery("select GOAL, ALARM from " + ACCOUNT_TABLE);
                cursor.moveToNext();
                sel = cursor.getInt(0);
                al = cursor.getInt(1);
                goal.setText(sel+"권");
                alarm.setText(al+"일");

        }catch(Exception e){
            Log.e(TAG, "Exception in executing insert SQL.", e);
        }

    }


    protected Dialog onCreateDialog(int id) {
        android.app.AlertDialog.Builder builder = null;
        builder = new android.app.AlertDialog.Builder(this);

        switch(id){
            case 1:
                builder = new android.app.AlertDialog.Builder(this);
                select= R.array.array_read;
                builder.setTitle("선택하세요");
                builder.setSingleChoiceItems(select, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        choice=which;
                    }
                });
                builder.setPositiveButton("선택", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(choice == 0 ) {
                            readDone();
                        } else if(choice == 1) {
                            deleteList();
                        }
                    }
                });
                builder.setNegativeButton("취소", null);

                break;

            case 2 :
                builder = new android.app.AlertDialog.Builder(this);
                select= R.array.array_read2;
                builder.setTitle("선택하세요");
                builder.setSingleChoiceItems(select, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        choice=which;
                    }
                });
                builder.setPositiveButton("선택", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(choice == 0 ) {
                            readNotDone();
                        } else if(choice == 1) {
                            deleteList();
                        }
                    }
                });
                builder.setNegativeButton("취소", null);

                break;
            default:
                break;
        }
        return builder.create();
    }

    public void deleteList(){
        String query = "delete from "+TABLE_TOREAD+" where NAME = '"+bookName+"'";
        database2.rawQuery(query);
        displayToReadView();
    }

    public void readDone(){

        String query = "update "+TABLE_TOREAD+" set DONE = 'true' where NAME = '"+bookName+"'";
        database2.rawQuery(query);
        displayToReadView();
    }

    public void readNotDone(){
        String query = "update "+TABLE_TOREAD+" set DONE = 'false' where NAME = '"+bookName+"'";
        database2.rawQuery(query);
        displayToReadView();
    }

    public void clickList(){
        toReadListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                choice=0;
                Read read = toReadAdapter.getItem(position);
                bookName = read.getBookname();
                boolean done = read.getDone();
                if(done){
                    showDialog(2);
                }else{
                    showDialog(1);
                }

                return false;
            }
        });

    }

    public void onAddClicked(View v){
        final EditText taskEditText = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("읽을 책 추가하기")
                .setView(taskEditText)
                .setPositiveButton("추가", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(taskEditText.getText());
                        Log.d(TAG, "Task to add: " + task);
                        try {

                            if (database2.checkExistToRead(task)) {
                                Toast.makeText(getApplicationContext(), "이미 존재합니다.", Toast.LENGTH_SHORT).show();
                            }else if(task.equals("")){
                                Toast.makeText(getApplicationContext(),"데이터를 입력해주세요.",Toast.LENGTH_SHORT).show();
                            }else{
                                String SQL = "insert into " + TABLE_TOREAD + " (NAME) values (" + "'" + task + "')";
                                database2.rawQuery(SQL);
                                displayToReadView();
                            }

                        }catch(Exception e){
                            Log.e(TAG, "Exception in executing update SQL.", e);
                        }

                    }
                })
                .setNegativeButton("취소", null)
                .create();
        dialog.show();


    }

    private void displayToReadView(){
        ArrayList<Read> toReadList = new ArrayList<Read>();
        Read read;
        try{
            Cursor cursor = database2.rawQuery("select NAME, DONE from " + TABLE_TOREAD);
            for(int i=0;i<cursor.getCount();i++){
                cursor.moveToNext();
                String name = cursor.getString(0);
                String done = cursor.getString(1);
                Log.d("done",i+"  "+done);
                if(done.equals("true")){
                    read = new Read(name,true);
                }else{
                    read= new Read(name,false);
                }
                toReadList.add(read);
            }
        }catch (Exception e){
            Log.e(TAG, "Exception in executing insert SQL.", e);
        }


        toReadAdapter = new ToReadAdapter(this, R.layout.favorite_layout,toReadList);
        toReadListView = (ListView) findViewById(R.id.list_toRead);
        toReadListView.setAdapter(toReadAdapter);
    }



    private class ToReadAdapter extends ArrayAdapter<Read>{
        private  ArrayList<Read> readList;

        public ToReadAdapter(Context context, int id, ArrayList<Read> readList){
            super(context, id, readList);
            this.readList=new ArrayList<Read>();
            this.readList.addAll(readList);
        }

        private class ViewHolder{
            TextView name;
        }

        @Override
        public void add(Read object) {
            super.add(object);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if(convertView==null){
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.favorite_layout,null);

                holder = new ViewHolder();
                holder.name = (TextView)convertView.findViewById(R.id.name);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            Read read = readList.get(position);
            holder.name.setText((position+1)+". "+read.getBookname());
            Log.d("checkdone",read.getDone()+"");
            if(read.getDone()){
                holder.name.setPaintFlags(holder.name.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            }else{
                holder.name.setPaintFlags(0);
            }

            return convertView;
        }
    }




    private void showBottom(){

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        View view = bottomNavigationView.findViewById(R.id.action_home);
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


    private   void useDB(){


        if (database2 != null) {
            database2.close();
            database2 = null;
        }

        database2 = BookDatabase.getInstance(this);
        boolean isOpen = database2.open();
        if (isOpen) {
            Log.d(TAG, "Account db is open.");
        } else {
            Log.d(TAG, "Account db is not open.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void onDestroy() {
        super.onDestroy();
    }


}
