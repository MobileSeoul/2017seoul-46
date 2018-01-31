package com.cafe24.kye1898.library.MyBookshelf.SharedBook;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nahej on 2017-08-03.
 */

public class RegisterRequest extends StringRequest {
    final static private String URL="http://kye1898.cafe24.com/nsl/pages/UserRegister.php";
    private Map<String, String> parameters;

    public RegisterRequest(String userID, String userPassword, String userEmail, Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        parameters=new HashMap<>();
        parameters.put("userID",userID);
        parameters.put("userPassword",userPassword);
        parameters.put("userEmail",userEmail);
    }

    @Override
    public Map<String, String>getParams(){
        return parameters;
    }
}
