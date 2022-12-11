package com.example.user.project101;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by USER on 09/07/2021.
 */
public class RecordListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Model> recordlist;

    public RecordListAdapter(Context context, int layout, ArrayList<Model> recordlist) {
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
        TextView txtName, txtTime;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder holder = new ViewHolder();

        if (row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.txtName = (TextView) row.findViewById(R.id.txtNameRow);
            holder.txtTime = (TextView) row.findViewById(R.id.txtTimeRow);
            row.setTag(holder);

        }else{
            holder = (ViewHolder)row.getTag();
        }
        Model model = recordlist.get(position);
        holder.txtName.setText(model.getName());
        holder.txtTime.setText(model.getTime());

        return row;
    }
}
