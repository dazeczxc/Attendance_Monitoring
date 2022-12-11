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
public class SpinnerRecordListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Spinner_Subject_Model> recordlist;

    public SpinnerRecordListAdapter(Context context, int layout, ArrayList<Spinner_Subject_Model> recordlist) {
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
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView txtSubjectID, txtSubject, txtSection;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = new ViewHolder();

        if (row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.txtSubject = (TextView) row.findViewById(R.id.txtvSubjectRow);
            holder.txtSection = (TextView) row.findViewById(R.id.txtvSectionRow);
            row.setTag(holder);

        }else{
            holder = (ViewHolder)row.getTag();
        }
        Spinner_Subject_Model model = recordlist.get(position);
        holder.txtSubject.setText(model.getSubject());
        holder.txtSection.setText(model.getSection());

        return row;
    }
}
