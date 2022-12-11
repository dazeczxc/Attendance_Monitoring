package com.example.user.project101;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by dasec on 24/11/2021.
 */
public class Student_RecordListAdapter extends BaseAdapter{

    private Context context;
    private int layout;
    private ArrayList<Student_Model> recordlist;

    public Student_RecordListAdapter(Context context, int layout, ArrayList<Student_Model> recordlist) {
        this.context = context;
        this.layout = layout;
        this.recordlist = recordlist;
    }


    @Override
    public int getCount() {
        return recordlist.size();

    }

    @Override
    public Object getItem(int position) {
        return recordlist.get(position);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView txtName;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = new ViewHolder();

        if (row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.txtName = (TextView) row.findViewById(R.id.txtNameRow);
             row.setTag(holder);

        }else{
            holder = (ViewHolder)row.getTag();
        }
        Student_Model model = recordlist.get(position);
        holder.txtName.setText(model.getName());

        return row;
    }
}
