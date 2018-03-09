package com.busesroute;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.bridge.Bridge;
import com.afollestad.bridge.BridgeException;
import com.afollestad.bridge.Request;
import com.afollestad.bridge.Response;
import com.busesroute.response.routes.RoutesSuccess;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import am.appwise.components.ni.NoInternetDialog;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import java.lang.reflect.Type;


@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    private FloatingActionButton fabPlus;
    private RecyclerView recyclerView;

    private RoutesSuccess routesSuccess;

    public  ArrayList<RoutesSuccess> routesSuccessArrayList;
    private RoutesAdapter routesAdapter;
    TextView toolbarTitle;
    NoInternetDialog noInternetDialog;


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


        toolbarTitle.setText("Routes");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);



        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);


        MainActivityPermissionsDispatcher.onCheckPermissionWithPermissionCheck(this);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


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
                                routesSuccess.setRouteName(String.valueOf(jsonObject.get("title")));

                                routesSuccessArrayList.add(routesSuccess);

                            }


                            routesAdapter = new RoutesAdapter(routesSuccessArrayList,MainActivity.this);

                            routesAdapter.setOnItemClickListener(new RoutesAdapter.RowClickListener() {
                                @Override
                                public void onClick(int position, int routeId) {


                                    Intent intent = new Intent(MainActivity.this,StopsActivity.class);
                                    intent.putExtra("routeId",routeId);
                                    startActivity(intent);

                                }
                            });

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView.setAdapter(routesAdapter);

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


                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                final View yourCustomView = inflater.inflate(R.layout.routes_custom_dialog, null);

                final TextView etName = (EditText) yourCustomView.findViewById(R.id.edStopTitleMap);
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

                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                //routesAdapter.notifyDataSetChanged();
                                                                finish();
                                                                startActivity(getIntent());

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
