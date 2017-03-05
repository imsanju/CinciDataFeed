package com.example.chilipepper.finalgooglemapsproject;

/**
 * Created by sanju on 12/10/15.
 */
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchAdapter  extends BaseAdapter  {

    Context context;
    LayoutInflater inflater;
    public ArrayList<HashMap<String, String>> data;

    HashMap<String, String> resultp = new HashMap<String, String>();

    public SearchAdapter(Context context,
                           ArrayList<HashMap<String, String>> arraylist) {
        this.context = context;
        data = arraylist;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        // Declare Variables
        TextView inputSearch;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.list_activity, parent, false);
        // Get the position
        resultp = data.get(position);

        inputSearch = (TextView) itemView.findViewById(R.id.businessName);
        inputSearch.setText(resultp.get(MainActivity.businessName));
        itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get the position
                resultp = data.get(position);
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra(MainActivity.businessName, resultp.get(MainActivity.businessName));
                context.startActivity(intent);

            }
        });
        return itemView;

    }
}
