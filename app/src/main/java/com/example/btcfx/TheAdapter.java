package com.example.btcfx;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by lenovo on 11/2/2017.
 */

public class TheAdapter extends RecyclerView.Adapter<TheAdapter.ViewHolder> {

    //global variables for the adapter class
    private List<ListItem> listItems;
    private Context context;

    public TheAdapter(List<ListItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    //onCreate view holder class( which takes in a view group and an integer parameter of the view type
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new ViewHolder(v);

    }


    //binding the view holder with the values saved in the list items
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final ListItem listItem = listItems.get(position);

        holder.textViewCurrency.setText(listItem.getCurrency());
        holder.textViewValue.setText(listItem.getValue());

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You clicked " + listItem.getCurrency(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, Convert.class);
                Bundle extras = new Bundle();
                extras.putString("EXTRA_CURRENCY", listItem.getCurrency().toString());
                extras.putString("EXTRA_VALUE", listItem.getValue().toString());
                intent.putExtras(extras);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }


    // creating the view holder class for the cards
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewCurrency;
        public TextView textViewValue;
        public RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewCurrency = (TextView) itemView.findViewById(R.id.itemCurrency);
            textViewValue = (TextView) itemView.findViewById(R.id.itemValue);

            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);

        }
    }

}