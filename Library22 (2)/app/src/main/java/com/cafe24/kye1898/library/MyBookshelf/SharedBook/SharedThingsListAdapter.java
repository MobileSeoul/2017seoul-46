package com.cafe24.kye1898.library.MyBookshelf.SharedBook;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.cafe24.kye1898.library.R;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by nahej on 2017-08-16.
 */

public class SharedThingsListAdapter extends BaseAdapter {
    private Context context;
    private List<SharedBookGet> writeThingsList;
    List<SharedBookGet> writeth;
    //ListView lv;
    String userID;
    TextView likeCount2;
    private AlertDialog dialog;


    public SharedThingsListAdapter(Context context, List<SharedBookGet>writeThingsList){
        this.context=context;
        this.writeThingsList=writeThingsList;
    }

    @Override
    public int getCount(){
        return writeThingsList.size();
    }
    @Override
    public Object getItem(int i){
        return writeThingsList.get(i);
    }
    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, final View view, ViewGroup viewGroup) {
        SharedPreferences pref=context.getSharedPreferences("Game", Activity.MODE_PRIVATE);
        userID=pref.getString("userID",null);

        View v=View.inflate(context, R.layout.list_item_write2, null);

        ListView lv=(ListView)v.findViewById(R.id.sharedlist);
        TextView title=(TextView)v.findViewById(R.id.sTitle);
        TextView userId=(TextView)v.findViewById(R.id.userID1);
        TextView author=(TextView)v.findViewById(R.id.author);
        likeCount2=(TextView)v.findViewById(R.id.likeCount2);


        title.setText(writeThingsList.get(i).getsTitle());
        userId.setText(writeThingsList.get(i).getUserID());
        author.setText(writeThingsList.get(i).getAuthor());
        Button like2=(Button)v.findViewById(R.id.like2);
        Button delete2=(Button)v.findViewById(R.id.delete2);
        Button edit2=(Button)v.findViewById(R.id.edit2);

        if(!userID.equals(userId.getText().toString())){

            edit2.setVisibility(view.GONE);
            delete2.setVisibility(view.GONE);

        }
        likeCount2.setText(writeThingsList.get(i).getLikeCount2());

        v.setTag(writeThingsList.get(i).getSid());
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // put your code here

                Intent intent=new Intent(context, SharedDetailActivity.class);
                Bundle bundle = new Bundle();

                String ttt=writeThingsList.get(i).getsTitle();
                String uuu=writeThingsList.get(i).getUserID();
                String aaa=writeThingsList.get(i).getAuthor();
                String ppp=writeThingsList.get(i).getPublisher();
                String ccc=writeThingsList.get(i).getsContent();

                bundle.putString("ttt",ttt);//title
                bundle.putString("uuu",uuu);//user
                bundle.putString("aaa",aaa);//author
                bundle.putString("ppp",ppp);//publisher
                bundle.putString("ccc",ccc);//content

                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });


        like2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View v) {
                String writeUser = writeThingsList.get(i).getUserID();
                if (!writeUser.equals(userID)) {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if (success) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    dialog = builder.setMessage("이 독후감을 좋아합니다.")
                                            .setPositiveButton("확인", null)
                                            .create();

                                    dialog.show();
                                    dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                                    dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);


                                    View v = View.inflate(context, R.layout.list_item_write2, null);
                                    SharedBookGet wt = writeThingsList.get(i);
                                    String abc = writeThingsList.get(i).likeCount2;
                                    int def = Integer.valueOf(abc) + 1;
                                    String ghi = String.valueOf(def);
                                    wt.likeCount2 = ghi;
                                    writeThingsList.set(i, wt);
                                    notifyDataSetChanged();
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    dialog = builder.setMessage("실패했습니다.")
                                            .setNegativeButton("확인", null)
                                            .create();
                                    dialog.show();

                                }
                            } catch (Exception e) {
                                e.printStackTrace();

                            }
                        }

                    };
                    String pos = String.valueOf(writeThingsList.get(i).getSid());
                    SharelikeRequest slRequest = new SharelikeRequest(pos, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(context);
                    queue.add(slRequest);

                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    dialog = builder.setMessage("자신의 글은 좋아요 할 수 없습니다.")
                            .setPositiveButton("확인", null)
                            .create();

                    dialog.show();

                    //Toast.makeText(context,"자신의 글은 좋아요 할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }


        });
        delete2.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(final View v) {
                String writeUser = writeThingsList.get(i).getUserID();
                if (writeUser.equals(userID)) {
                    AlertDialog.Builder reask=new AlertDialog.Builder(context);
                    reask.setTitle("삭제 확인");
                    reask.setMessage("정말로 삭제 하시겠습니까?");
                    reask.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Response.Listener<String> responseListener = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonResponse = new JSONObject(response);
                                        boolean success = jsonResponse.getBoolean("success");
                                        if (success) {
                                            Toast.makeText(context,"삭제 되었습니다.", Toast.LENGTH_SHORT).show();

                                            writeThingsList.remove(i);
                                            notifyDataSetChanged();
                                        } else {
                                            Toast.makeText(context,"삭제 실패 되었습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();

                                    }
                                }


                            };
                            String pos = String.valueOf(writeThingsList.get(i).getSid());
                            ShareDeleteRequest sdRequest = new ShareDeleteRequest(pos, responseListener);
                            RequestQueue queue = Volley.newRequestQueue(context);
                            queue.add(sdRequest);
                        }
                    });
                    reask.setNegativeButton("취소", null);
                    reask.show();



                }
                else
                {
                    Toast.makeText(context,"you are not the same user", Toast.LENGTH_SHORT).show();
                }
            }


        });

        edit2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View v){
                //Toast.makeText(v.getContext(),"you are1", Toast.LENGTH_SHORT).show();
                String writeUser=writeThingsList.get(i).getUserID();
                if(writeUser.equals(userID))
                {
                    String title=writeThingsList.get(i).getsTitle();
                    String author=writeThingsList.get(i).getAuthor();
                    String content=writeThingsList.get(i).getsContent();
                    String publisherr=writeThingsList.get(i).getPublisher();

                    String wid2=Integer.toString(writeThingsList.get(i).getSid());
                    Log.d("log:",title+" "+content+" "+wid2);
                    Intent gotoEdit=new Intent(context, ShareEditActivity.class);
                    gotoEdit.putExtra("sid",wid2);
                    gotoEdit.putExtra("title",title);
                    gotoEdit.putExtra("author",author);
                    gotoEdit.putExtra("publisher",publisherr);
                    gotoEdit.putExtra("content",content);
                    context.startActivity(gotoEdit);
                }
                else
                {
                    Toast.makeText(context,"you are not the same user", Toast.LENGTH_SHORT).show();
                }
                Response.Listener<String> responseListener=new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        //Toast.makeText(v.getContext(),"you are", Toast.LENGTH_SHORT).show();
                        try
                        {
                            Toast.makeText(v.getContext(),"you are", Toast.LENGTH_SHORT).show();
                            String writeUser=writeThingsList.get(i).getUserID();
                            if(writeUser.equals(userID))
                            {
                                Intent gotoEdit=new Intent(context, ShareEditActivity.class);
                                context.startActivity(gotoEdit);
                            }
                            else
                            {
                                Toast.makeText(context,"you are not the same user", Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();

                        }
                    }

                };

            }


        });

        return v;

    }


    public void updateResults(List<SharedBookGet> results) {
        // assign the new result list to your existing list it will work

        notifyDataSetChanged();
    }


}