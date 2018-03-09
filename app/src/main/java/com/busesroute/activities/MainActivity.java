package com.busesroute.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.bridge.Bridge;
import com.afollestad.bridge.BridgeException;
import com.afollestad.bridge.Request;
import com.afollestad.bridge.Response;
import com.busesroute.R;
import com.busesroute.adapters.RoutesAdapter;
import com.busesroute.response.routes.RoutesSuccess;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import am.appwise.components.ni.NoInternetDialog;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;


@RuntimePermissions
public class MainActivity extends AppCompatActivity  implements ColorPickerDialogListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    private FloatingActionButton fabPlus;
    private RecyclerView recyclerView;

    private RoutesSuccess routesSuccess;

    public  ArrayList<RoutesSuccess> routesSuccessArrayList;
    private RoutesAdapter routesAdapter;
    TextView toolbarTitle,txtwebViewActivity;
    NoInternetDialog noInternetDialog;

    private static final int DIALOG_ID = 0;

    private String colorCode;
    private Button  btnColoraddroutes;
    private boolean isEdtabDialog = false;


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);



    }


    @Override
    public void onColorSelected(int dialogId, int color) {

        switch (dialogId) {
            case DIALOG_ID:
                // We got result from the dialog that is shown when clicking on the icon in the action bar.
                // Toast.makeText(MainActivity.this, "Selected Color: #" + Integer.toHexString(color), Toast.LENGTH_SHORT).show();


                colorCode  = Integer.toHexString(color);

                if(isEdtabDialog){

                    btnEditColorPicker.setBackgroundColor(color);

                }else {

                    btnColoraddroutes.setBackgroundColor(color);

                }

                break;
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        noInternetDialog.onDestroy();
    }

    Button btnEditColorPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bridge.config()
                .defaultHeader("Content-Type", "application/json");



        //checkLocationPermission();
       noInternetDialog = new NoInternetDialog.Builder(this).build();


        routesSuccessArrayList = new ArrayList<>();


        //Android toolbar
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        txtwebViewActivity = findViewById(R.id.txtwebViewActivity);


        toolbarTitle.setText("Routes");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        txtwebViewActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                intent.putExtra("url", "https://www.mtrtransit.com/admin/");
                startActivity(intent);
            }
        });



        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);


        MainActivityPermissionsDispatcher.onCheckPermissionWithPermissionCheck(this);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new com.busesroute.utils.DividerItemDecoration(this));

        new Thread(new Runnable() {
            @Override
            public void run() {


                try {
                    final Request request = Bridge
                            .post("http://18.217.234.39:8080/api/allRoutes")
                            .retries(5, 6000)
                            .request();
                    request.response().asString();


                    Response response = request.response();
                    if (response.isSuccess()) {
                        // Request returned HTTP status 200-300

                        JSONArray jsonArray1 = null;

                        String res = response.asString();



                        try {
                            jsonArray1 = new JSONArray(res);

                            for(int i=0;i<jsonArray1.length();i++){

                                JSONObject jsonObject = jsonArray1.getJSONObject(i);

                                routesSuccess = new RoutesSuccess();

                                routesSuccess.setId(jsonObject.getInt("id"));
                                routesSuccess.setColorCode(jsonObject.getString("colorcode"));
                                routesSuccess.setRouteName(String.valueOf(jsonObject.get("title")));

                                routesSuccessArrayList.add(routesSuccess);

                            }


                            routesAdapter = new RoutesAdapter(routesSuccessArrayList,MainActivity.this);



                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView.setAdapter(routesAdapter);



                                    recyclerView.addOnItemTouchListener(new RecyclerTouchListener(MainActivity.this, recyclerView, new ClickListener() {
                                        @Override
                                        public void onClick(View view, final int position) {


                                            Intent intent = new Intent(MainActivity.this,StopsActivity.class);
                                            intent.putExtra("routeId",routesSuccessArrayList.get(position).getId());
                                            startActivity(intent);

                                        }

                                        @Override
                                        public void onLongClick(View view, final int position) {
                                            isEdtabDialog=true;
                                            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                                            final View yourCustomView = inflater.inflate(R.layout.dialog_deleteoredit_route, null);


                                            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                                                    .setView(yourCustomView)
                                                    .setPositiveButton("EDIT", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int whichButton) {


                                                            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                                                            final View yourCustomView = inflater.inflate(R.layout.edit_route, null);

                                                            final EditText edNewRouteName =  yourCustomView.findViewById(R.id.edNewRouteName);
                                                            btnEditColorPicker = yourCustomView.findViewById(R.id.btnEditColorPicker);

                                                            if(!TextUtils.isEmpty(routesSuccessArrayList.get(position).getColorCode()))
                                                                btnEditColorPicker.setBackgroundColor(Color.parseColor("#"+routesSuccessArrayList.get(position).getColorCode()));

                                                            btnEditColorPicker.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {

                                                                    ColorPickerDialog.newBuilder()
                                                                            .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                                                                            .setAllowPresets(false)
                                                                            .setDialogId(DIALOG_ID)
                                                                            .setColor(Color.BLACK)
                                                                            .setShowAlphaSlider(true)
                                                                            .show(MainActivity.this);


                                                                }
                                                            });


                                                            edNewRouteName.setText(routesSuccessArrayList.get(position).getRouteName());
                                                            int textLength = edNewRouteName.getText().length();
                                                            edNewRouteName.setSelection(textLength, textLength);

                                                            AlertDialog dialog_edit = new AlertDialog.Builder(MainActivity.this)
                                                                    .setView(yourCustomView)
                                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                        public void onClick(DialogInterface dialog, int whichButton) {

                                                                            new Thread(new Runnable() {
                                                                                @Override
                                                                                public void run() {

                                                                                    JSONObject jsonObject = new JSONObject();
                                                                                    try {
                                                                                        jsonObject.put("routeid",routesSuccessArrayList.get(position).getId());
                                                                                        jsonObject.put("title",edNewRouteName.getText().toString());
                                                                                        jsonObject.put("colorcode",colorCode);

                                                                                    } catch (JSONException e) {
                                                                                        e.printStackTrace();
                                                                                    }
                                                                                    try {
                                                                                        final Request request = Bridge
                                                                                                .post("http://18.217.234.39:8080/api/editRoute")
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
                                                                                                    final String message = json.getString("message");


                                                                                                    runOnUiThread(new Runnable() {
                                                                                                        @Override
                                                                                                        public void run() {


                                                                                                            RoutesSuccess routesSuccess = routesSuccessArrayList.get(position);
                                                                                                            routesSuccess.setRouteName(edNewRouteName.getText().toString());
                                                                                                            routesSuccess.setColorCode(colorCode);
                                                                                                            routesAdapter.notifyItemChanged(position);

                                                                                                           // Toast.makeText(MainActivity.this,message,Toast.LENGTH_LONG).show();

                                                                                                        }
                                                                                                    });

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
                                                                    })
                                                                    .setNegativeButton("Cancel", null).create();
                                                            dialog_edit.show();






                                                        }
                                                    })
                                                    .setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {


                                                            final int routeid = routesSuccessArrayList.get(position).getId();

                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {

                                                                    JSONObject jsonObject = new JSONObject();
                                                                    try {
                                                                        jsonObject.put("routeid",routeid);

                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                    try {
                                                                        final Request request = Bridge
                                                                                .post("http://18.217.234.39:8080/api/deleteRoute")
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
                                                                                    final String message = json.getString("message");


                                                                                    runOnUiThread(new Runnable() {
                                                                                        @Override
                                                                                        public void run() {


                                                                                            routesSuccessArrayList.remove(position);
                                                                                            routesAdapter.notifyDataSetChanged();


                                                                                          //  Toast.makeText(MainActivity.this,message,Toast.LENGTH_LONG).show();



                                                                                        }
                                                                                    });

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
                                                    }).create();
                                            dialog.show();






                                        }
                                    }));


                                }
                            });





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




        fabPlus =  findViewById(R.id.fab);
        fabPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEdtabDialog=false;

                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                final View yourCustomView = inflater.inflate(R.layout.routes_custom_dialog, null);

                final TextView etName = (EditText) yourCustomView.findViewById(R.id.edStopTitleMap);

                btnColoraddroutes = yourCustomView.findViewById(R.id.btnColoraddroutes);


                btnColoraddroutes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ColorPickerDialog.newBuilder()
                                .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                                .setAllowPresets(false)
                                .setDialogId(DIALOG_ID)
                                .setColor(Color.BLACK)
                                .setShowAlphaSlider(true)
                                .show(MainActivity.this);
                    }
                });
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setView(yourCustomView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {


                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        JSONObject jsonObject = new JSONObject();
                                        try {
                                            jsonObject.put("title",etName.getText().toString());
                                            jsonObject.put("colorcode",colorCode);
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
                                                        final int routeId = json.getInt("routeid");

                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {

                                                                RoutesSuccess routesSuccess=   new RoutesSuccess();
                                                                routesSuccess.setId(routeId);
                                                                routesSuccess.setRouteName(etName.getText().toString());
                                                                routesSuccessArrayList.add(routesSuccess);
                                                                routesAdapter.notifyDataSetChanged();


                                                            }
                                                        });


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
                        })
                        .setNegativeButton("Cancel", null).create();
                dialog.show();



            }
        });




    }

    public static interface ClickListener{
        public void onClick(View view,int position);
        public void onLongClick(View view,int position);
    }


    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener){

            this.clicklistener=clicklistener;
            gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child=recycleView.findChildViewUnder(e.getX(),e.getY());
                    if(child!=null && clicklistener!=null){
                        clicklistener.onLongClick(child,recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child=rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clicklistener!=null && gestureDetector.onTouchEvent(e)){
                clicklistener.onClick(child,rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }





    @NeedsPermission({android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION})
    void onCheckPermission() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale({android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION})
    void onShowRationale(final PermissionRequest request) {
    }

    @OnPermissionDenied({android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION})
    void onPermissionDenied() {
    }

    @OnNeverAskAgain({android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION})
    void onNverAskAgain() {
    }





}
