package com.cafe24.kye1898.library.MyBookshelf.SharedBook;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nahej on 2017-08-13.
 */

public class ShareEditRequest extends StringRequest {
    final static private String URL="http://kye1898.cafe24.com/nsl/share_edit.php";
    private Map<String, String> parameters;

    public ShareEditRequest(String sid, String sContent, Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        parameters=new HashMap<>();
        parameters.put("sid",sid);
        parameters.put("sContent",sContent);
    }

    @Override
    public Map<String, String>getParams(){
        return parameters;
    }

}
