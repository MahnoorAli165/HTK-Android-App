package com.htk.consumerapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterAppointments extends ArrayAdapter {

    private ArrayList<String> categories;
    private ArrayList<String>  types;
    private ArrayList<String>  dates;
    private ArrayList<String>  times;
    private Activity context;

    public AdapterAppointments(Activity context, ArrayList<String> categories, ArrayList<String> types, ArrayList<String> dates, ArrayList<String> times)
    {
        super(context, R.layout.row_appointments, categories);
        this.context= context;
        this.categories = categories;
        this.types = types;
        this.dates = dates;
        this.times = times;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater= context.getLayoutInflater();
        View listViewItem= inflater.inflate(R.layout.row_appointments, null, true);

        TextView categoryT = (TextView) listViewItem.findViewById(R.id.categoryT);
        categoryT.setText(categories.get(position));
        TextView typeT = (TextView) listViewItem.findViewById(R.id.typeT);
        typeT.setText(types.get(position));
        TextView dateT = (TextView) listViewItem.findViewById(R.id.dateT);
        dateT.setText(dates.get(position));
        TextView timeT = (TextView) listViewItem.findViewById(R.id.timeT);
        timeT.setText(times.get(position));

        ImageView image = (ImageView) listViewItem.findViewById(R.id.appointmnetImage);
        switch(categories.get(position)){
            case "Doctor visit":{
                image.setImageResource(R.drawable.mdoctor_icon);
                break;
            }
            case "Nursing Care":{
                image.setImageResource(R.drawable.nurse_icon);
                break;
            }
            case "Laboratory on call":{
                image.setImageResource(R.drawable.lab_icon);
                break;
            }
            case "Wellness Treatments":{
                image.setImageResource(R.drawable.wellness_icon);
                break;
            }
            case "Physiotherapist Visit":{
                image.setImageResource(R.drawable.physio_icon);
                break;
            }
            case "null":{
                categoryT.setVisibility(View.INVISIBLE);
                break;
            }
            default:{
                image.setImageResource(R.drawable.image_loading_icon);
                break;
            }
        }

        return listViewItem;
    }
}
