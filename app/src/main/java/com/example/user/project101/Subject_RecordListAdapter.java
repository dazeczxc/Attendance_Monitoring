package com.example.user.project101;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by USER on 10/07/2021.
 */
public class Subject_RecordListAdapter extends BaseAdapter {


    private Context context;
    private int layout;
    private ArrayList<Subject_Model> recordlist;

    SubjectDBHelper dbHelper = new SubjectDBHelper(context);


    public Subject_RecordListAdapter(Context context, int layout, ArrayList<Subject_Model> recordlist) {
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
        TextView txtSubject, txtSection, txtDay, txtTime, txtTimeto;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder holder = new ViewHolder();

        if (row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.txtSubject = (TextView) row.findViewById(R.id.textViewSubjectRow);
            holder.txtSection = (TextView) row.findViewById(R.id.textViewSectionRow);
            holder.txtDay = (TextView) row.findViewById(R.id.textViewDayRow);
            holder.txtTime = (TextView) row.findViewById(R.id.textViewTime);
            holder.txtTimeto = (TextView) row.findViewById(R.id.textViewTimeTo);



            row.setTag(holder);

        }else{
            holder = (ViewHolder)row.getTag();
        }

        Subject_Model model = recordlist.get(position);
        holder.txtSubject.setText(model.getSubject());
        holder.txtSection.setText(model.getSection());
        holder.txtDay.setText(model.getDay());
        holder.txtTime.setText(model.getTime());
        holder.txtTimeto.setText(model.getTimeto());



        return row;

    }

}
