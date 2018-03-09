package com.busesroute.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.bridge.Bridge;
import com.afollestad.bridge.BridgeException;
import com.afollestad.bridge.Request;
import com.afollestad.bridge.Response;
import com.busesroute.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dharamveer on 8/3/18.
 */

public class AddRouteDialog extends Dialog {


    public AddRouteDialog(@NonNull Context context) {
        super(context);
    }


    private EditText edTitle;
    private Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_add_route);




        edTitle = findViewById(R.id.edTitle);
        btnOk = findViewById(R.id.btnOk);


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("title",edTitle.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            final Request request = Bridge
                                    .post("http://18.217.234.39:8080/api/createRoute")
                                    .retries(5, 6000)
                                    .body(jsonObject)
                                    .request();
                            request.response().asString();



                            Response response = request.response();
                            if (response.isSuccess()) {
                                // Request returned HTTP status 200-300

                                String res = response.asString();

                                JSONObject json = null;
                                try {
                                    json = new JSONObject(res);

                                    try {
                                        String routeId = String.valueOf(json.get("routeid"));




                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }





                            } else {
                                // Request returned an HTTP error status
                            }




                        } catch (BridgeException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();


            }
        });

    }
}
