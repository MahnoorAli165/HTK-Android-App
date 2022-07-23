package com.htk.consumerapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class ReportsAdapter extends ArrayAdapter<String>
{
    private String[] titles;
    private String[] dates;
    private Activity context;

    public ReportsAdapter(Activity context, String[] titles, String[] dates)
    {
        super(context, R.layout.row_appointments, titles);//sending R.layoutfile and one array is necessary here
        this.context= context;
        this.titles = titles;
        this.dates= dates;
    }
// here is the place for one more function pasted on the next slide

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater= context.getLayoutInflater();
        View listViewItem= inflater.inflate(R.layout.row_appointments, null, true);
//        TextView titleT= (TextView) listViewItem.findViewById(R.id.reportTitle);
//        titleT.setText(titles[position]);
//        TextView dateT = (TextView) listViewItem.findViewById(R.id.reportDate);
//        dateT.setText(dates[position]);
        return listViewItem;
    }
}
