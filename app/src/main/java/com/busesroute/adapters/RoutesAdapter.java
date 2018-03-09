package com.busesroute.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.busesroute.R;
import com.busesroute.response.routes.RoutesSuccess;

import java.util.List;

/**
 * Created by dharamveer on 8/3/18.
 */

public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.MyHolder> {

    private List<RoutesSuccess> routesSuccessList;
    Context mContext;
    LayoutInflater mInflater;
   // public RowClickListener rowClickListener;


    public RoutesAdapter(List<RoutesSuccess> routesSuccessList, Context mContext) {
        this.routesSuccessList = routesSuccessList;
        this.mContext = mContext;
    }


 /*   public interface RowClickListener{

        void onClick(int position,int routeId);

    }



    public interface OnLngClickListener{
        void onLongClick(int position);

    }
    public void setOnItemClickListener(RowClickListener rowClickListener) {
        this.rowClickListener = rowClickListener;

    }*/




    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_route, parent, false);
        return new MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, final int position) {


        final RoutesSuccess routesSuccess = routesSuccessList.get(position);

        holder.txtId.setText(String.valueOf(routesSuccess.getId()));
        holder.txtRouteName.setText(String.valueOf(routesSuccess.getRouteName()));


    }

    @Override
    public int getItemCount() {
        return routesSuccessList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        TextView txtId,txtRouteName;
        RelativeLayout rowClick;

        public MyHolder(View itemView) {
            super(itemView);

            txtId = itemView.findViewById(R.id.txtId);
            txtRouteName = itemView.findViewById(R.id.txtRouteName);
            rowClick = itemView.findViewById(R.id.rowClick);
        }
    }
}
