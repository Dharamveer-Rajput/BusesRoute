package com.busesroute;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
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
import android.widget.Toast;

import com.afollestad.bridge.Bridge;
import com.afollestad.bridge.BridgeException;
import com.afollestad.bridge.Request;
import com.afollestad.bridge.Response;
import com.busesroute.response.StopsSuccess.StopsSuccess;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import am.appwise.components.ni.NoInternetDialog;
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider;

public class StopsActivity extends AppCompatActivity {


    int routeId;
    int stopId;

    private RecyclerView recyclerViewStops;

    private StopsSuccess stopsSuccess;

    public ArrayList<StopsSuccess> stopsSuccessArrayList;
    private StopsAdapter stopsAdapter;
    private FloatingActionButton fabStops;
    TextView toolbarTitle;
    NoInternetDialog noInternetDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stops);

        //Android toolbar
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);

        noInternetDialog = new NoInternetDialog.Builder(this).build();

        toolbarTitle.setText("Stops");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        recyclerViewStops = findViewById(R.id.recyclerViewStops);

        recyclerViewStops.setHasFixedSize(true);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(StopsActivity.this);

        recyclerViewStops.setLayoutManager(mLayoutManager);
        recyclerViewStops.setItemAnimator(new DefaultItemAnimator());

        Bundle bundle = getIntent().getExtras();
        routeId = bundle.getInt("routeId");


        stopsSuccessArrayList = new ArrayList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {


                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("routeid", routeId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    final Request request = Bridge
                            .post("http://18.217.234.39:8080/api/getStops")
                            .retries(5, 6000)
                            .body(jsonObject)
                            .request();
                    request.response().asString();


                    Response response = request.response();
                    if (response.isSuccess()) {
                        // Request returned HTTP status 200-300

                        JSONArray jsonArray1 = null;

                        String res = response.asString();


                        try {
                            jsonArray1 = new JSONArray(res);


                            for (int i = 0; i < jsonArray1.length(); i++) {

                                JSONObject jsonObject1 = jsonArray1.getJSONObject(i);

                                stopsSuccess = new StopsSuccess();

                                stopsSuccess.setIdStops(jsonObject1.getInt("id"));
                                stopsSuccess.setStopsTitle(jsonObject1.getString("title"));
                                stopsSuccess.setStopsLat(jsonObject1.getString("lat"));
                                stopsSuccess.setStopsLng(jsonObject1.getString("lng"));

                                stopsSuccessArrayList.add(stopsSuccess);

                            }


                            stopsAdapter = new StopsAdapter(stopsSuccessArrayList, StopsActivity.this);


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerViewStops.setAdapter(stopsAdapter);

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


        fabStops = findViewById(R.id.fabStops);
        fabStops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater inflater = LayoutInflater.from(StopsActivity.this);
                final View yourCustomView = inflater.inflate(R.layout.stops_add_roots, null);

                final TextView etName = (EditText) yourCustomView.findViewById(R.id.edStopTitleMap);
                AlertDialog dialog = new AlertDialog.Builder(StopsActivity.this)
                        .setView(yourCustomView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(getApplicationContext());

                                if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                        android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(),
                                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                    return;
                                }

                                locationProvider.getLastKnownLocation()
                                        .subscribe(new io.reactivex.functions.Consumer<Location>() {
                                            @Override
                                            public void accept(Location location) throws Exception {

                                                final double curLat = location.getLatitude();
                                                final double curLong = location.getLongitude();


                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        JSONObject jsonObject = new JSONObject();
                                                        try {
                                                            jsonObject.put("title",etName.getText().toString());
                                                            jsonObject.put("routeid",routeId);
                                                            jsonObject.put("lat",curLat);
                                                            jsonObject.put("lng",curLong);
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                        try {
                                                            final Request request = Bridge
                                                                    .post("http://18.217.234.39:8080/api/createStop")
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
                                                                        stopId = json.getInt("stopid");


                                                                        runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {

                                                                                finish();
                                                                                startActivity(getIntent());


                                                                               /* stopsAdapter.notifyDataSetChanged();

                                                                                recyclerViewStops.invalidate();
*/

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
                                        });



                                Toast.makeText(getApplicationContext(),etName.getText().toString(),Toast.LENGTH_SHORT).show();




                            }
                        })
                        .setNegativeButton("Cancel", null).create();
                dialog.show();


            }

        });
    }
}
