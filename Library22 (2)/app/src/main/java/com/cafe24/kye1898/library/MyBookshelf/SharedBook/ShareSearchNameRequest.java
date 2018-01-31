package com.cafe24.kye1898.library.MyBookshelf.SharedBook;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nahej on 2017-08-13.
 */

public class ShareSearchNameRequest extends StringRequest {
    final static private String URL="http://kye1898.cafe24.com/nsl/share_get_name.php";
    private Map<String, String> parameters;

    public ShareSearchNameRequest(String sTitle, Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        parameters=new HashMap<>();
        parameters.put("sTitle",sTitle);
    }

    @Override
    public Map<String, String>getParams(){
        return parameters;
    }

}
