package com.busesroute.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.busesroute.R;
import com.busesroute.response.StopsSuccess.StopsSuccess;

import java.util.ArrayList;

/**
 * Created by dharamveer on 8/3/18.
 */

public class StopsAdapter extends RecyclerView.Adapter<StopsAdapter.MyHolder>  {



    private StopsSuccess stopsSuccess;

    public ArrayList<StopsSuccess> stopsSuccessArrayList;
    Context mContext;


    public StopsAdapter(ArrayList<StopsSuccess> stopsSuccessArrayList, Context mContext) {
        this.stopsSuccessArrayList = stopsSuccessArrayList;
        this.mContext = mContext;
    }



    @Override
    public StopsAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_stops, parent, false);
        return new StopsAdapter.MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StopsAdapter.MyHolder holder, int position) {


        final StopsSuccess stopsSuccess = stopsSuccessArrayList.get(position);

        holder.txtIdStops.setText(String.valueOf(stopsSuccess.getIdStopsToShow()));
        holder.txtTitle.setText(stopsSuccess.getStopsTitle());
        holder.txtLatStops.setText(stopsSuccess.getStopsLat());
        holder.txtLngStops.setText(stopsSuccess.getStopsLng());



    }

    @Override
    public int getItemCount() {
        return stopsSuccessArrayList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {


        private TextView txtIdStops,txtTitle,txtLatStops,txtLngStops;
        public MyHolder(View itemView) {
            super(itemView);

            txtIdStops = itemView.findViewById(R.id.txtIdStops);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtLatStops = itemView.findViewById(R.id.txtLatStops);
            txtLngStops = itemView.findViewById(R.id.txtLngStops);



        }
    }
}
