package com.busesroute.Dialogs;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.busesroute.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import pl.charmas.android.reactivelocation2.ReactiveLocationProvider;


public class NewDialog extends Dialog {



    public NewDialog(@NonNull Context context) {
        super(context);
    }

    private EditText edStopTitleMap;
    private Button btnOkMap;
    OnMyDialogResult mDialogResult; // the callback



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_new_route);



        edStopTitleMap = findViewById(R.id.edTitle);
        btnOkMap = findViewById(R.id.btnOkMap);

        btnOkMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(getContext());

                if (ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }


                locationProvider.getLastKnownLocation()
                        .subscribe(new io.reactivex.functions.Consumer<Location>() {
                            @Override
                            public void accept(Location location) throws Exception {

                                double curLat = location.getLatitude();
                                double curLong = location.getLongitude();


                                if( mDialogResult != null ){
                                    mDialogResult.finish(curLat,curLong);

                                }

                                NewDialog.this.dismiss();

                            }
                        });



                dismiss();


            }
        });


    }






    public void setDialogResult(OnMyDialogResult dialogResult){
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult{
        void finish(double curLat,double curLong);
    }




}
