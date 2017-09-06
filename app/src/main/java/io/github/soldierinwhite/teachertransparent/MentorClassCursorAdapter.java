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
 * Created by schoo on 2017/04/05.
 */

public class MentorClassCursorAdapter extends CursorAdapter {

    MentorClassCursorAdapter(Context context, Cursor cursor){ super(context, cursor, 0); }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.mentor_class_list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView mentorClassName = (TextView) view.findViewById(R.id.mentor_class_list_item_text_view);
        String mentorClassString = cursor.getString(cursor.getColumnIndexOrThrow(ClassesContract.StudentEntry.COLUMN_MENTOR_CLASS_NAME));
        mentorClassName.setText(mentorClassString);
    }
}
