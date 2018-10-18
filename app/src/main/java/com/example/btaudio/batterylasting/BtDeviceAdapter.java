package com.example.btaudio.batterylasting;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class BtDeviceAdapter extends BaseAdapter {
    private Context context; //context
    private ArrayList<BtDevice> items; //data source of the list adapter

    //public constructor
    public BtDeviceAdapter(Context context, ArrayList<BtDevice> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size(); //returns total of items in the list
    }

    @Override
    public Object getItem(int position) {
        return items.get(position); //returns list item at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.listview_bt, parent, false);
        }

        // get current item to be displayed
        BtDevice currentItem = (BtDevice) getItem(position);

        // get the TextView for item name and item description
        LinearLayout renglon = (LinearLayout) convertView.findViewById(R.id.renglon);
        TextView textViewItemName = (TextView) convertView.findViewById(R.id.text_view_item_name);
        TextView textViewItemDescription = (TextView) convertView.findViewById(R.id.text_view_item_description);
        TextView textViewItemStatus = (TextView) convertView.findViewById(R.id.text_view_item_status);
        TextView textViewItemTime = (TextView) convertView.findViewById(R.id.text_view_item_time);

        //sets the text for item name and item description from the current item object
        textViewItemName.setText(currentItem.getName());
        textViewItemDescription.setText(currentItem.getAddress());
        textViewItemStatus.setText(currentItem.getConnectedString());
        renglon.setBackgroundColor(currentItem.getConnectedColor());
        textViewItemTime.setText(currentItem.getTime());

        // returns the view for the current row
        return convertView;
    }
}