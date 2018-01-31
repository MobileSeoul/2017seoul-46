package com.cafe24.kye1898.library.MyBookshelf.SharedBook;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nahej on 2017-08-13.
 */

public class ShareBookRequest extends StringRequest {
    final static private String URL="http://kye1898.cafe24.com/nsl/share_insert.php";
    private Map<String, String> parameters;

    public ShareBookRequest(String userID, String sTitle, String author, String publisher, String sContent, Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        parameters=new HashMap<>();
        parameters.put("userID",userID);
        parameters.put("sTitle",sTitle);
        parameters.put("author",author);
        parameters.put("publisher",publisher);
        parameters.put("sContent",sContent);
    }

    @Override
    public Map<String, String>getParams(){
        return parameters;
    }

}
