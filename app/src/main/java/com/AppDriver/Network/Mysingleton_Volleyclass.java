package com.AppDriver.Network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class Mysingleton_Volleyclass {


    private RequestQueue requestQueue;
    private static Mysingleton_Volleyclass mysingleton_volleyclass;
    private static Context mcontext;





    private Mysingleton_Volleyclass(Context context) {

        mcontext = context;
        requestQueue =getRequestQueue();


    }

    public static Mysingleton_Volleyclass getInstance(Context context) {
        if (mysingleton_volleyclass == null)

        {
            mysingleton_volleyclass = new Mysingleton_Volleyclass(context);
        }

        return mysingleton_volleyclass;
    }

    public  synchronized RequestQueue getRequestQueue() {

        if(requestQueue == null){

            requestQueue= Volley.newRequestQueue(mcontext.getApplicationContext());

        }

        return requestQueue;
    }

    public <T> Request addrequest(Request<T> req){

        return requestQueue.add(req);
    }

}
