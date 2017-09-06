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
 * Created by schoo on 2017/03/02.
 */

class StudentCursorAdapter extends CursorAdapter {

    StudentCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.student_list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor){

        TextView name = (TextView) view.findViewById(R.id.student_name_list_item);

        String nameString = cursor.getString(cursor.getColumnIndexOrThrow(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME)) +
                " " + cursor.getString(cursor.getColumnIndexOrThrow(ClassesContract.StudentEntry.COLUMN_STUDENT_LAST_NAME));

        name.setText(nameString);
    }
}
