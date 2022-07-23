package com.htk.consumerapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterReports extends ArrayAdapter {

    private ArrayList<String> titles;
    private ArrayList<String> dates;
    private ArrayList<String> appointments;
    private Activity context;

    public AdapterReports(Activity context, ArrayList<String> ts, ArrayList<String> ds, ArrayList<String> as) {
        super(context, R.layout.row_reports, ts);
        this.context = context;
        this.titles = ts;
        this.dates = ds;
        this.appointments = as;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.row_reports, null, true);

        TextView tT = (TextView) listViewItem.findViewById(R.id.reportTitleL);
        tT.setText(titles.get(position));
        TextView dT = (TextView) listViewItem.findViewById(R.id.reportDateL);
        dT.setText("Date: " + dates.get(position));
        TextView aT = (TextView) listViewItem.findViewById(R.id.appointmentIDL);
        aT.setText("Appointment: " + appointments.get(position));

        ImageView image = listViewItem.findViewById(R.id.reportImage);
        if (titles.get(position).equalsIgnoreCase("")) {
            image.setImageBitmap(null);
            dT.setText("");
            aT.setText("");
        }

        return listViewItem;
    }
}