package io.github.soldierinwhite.teachertransparent;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import io.github.soldierinwhite.teachertransparent.data.ClassesContract;

/**
 * Created by schoo on 2017/03/14.
 */

public class ClassCursorAdapter extends CursorAdapter {
    ClassCursorAdapter(Context context, Cursor cursor){
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.class_list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView className = (TextView) view.findViewById(R.id.class_name);
        TextView subject = (TextView) view.findViewById(R.id.subject);

        className.setText(cursor.getString(cursor.getColumnIndexOrThrow(ClassesContract.ClassEntry.COLUMN_CLASS_NAME)));
        subject.setText(cursor.getString(cursor.getColumnIndexOrThrow(ClassesContract.ClassEntry.COLUMN_SUBJECT)));
    }
}
