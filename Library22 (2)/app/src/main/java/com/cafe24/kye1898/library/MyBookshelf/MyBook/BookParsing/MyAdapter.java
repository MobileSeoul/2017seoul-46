package com.cafe24.kye1898.library.MyBookshelf.MyBook.BookParsing;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.cafe24.kye1898.library.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

/**
 * Created by nahej on 2017-08-15.
 */

public class MyAdapter extends SimpleAdapter {

    public MyAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to){
        super(context, data, resource, from, to);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        // here you let SimpleAdapter built the view normally.
        View v = super.getView(position, convertView, parent);

        // Then we get reference for Picasso
        ImageView img = (ImageView) v.getTag();
        if(img == null){
            img = (ImageView) v.findViewById(R.id.book_image);
            v.setTag(img); // <<< THIS LINE !!!!
        }
        // get the url from the data you passed to the `Map`
        String url = (String) ((Map)getItem(position)).get("cover");
        // do Picasso
        Picasso.with(v.getContext()).load(url).into(img);

        // return the view
        return v;
    }
}
