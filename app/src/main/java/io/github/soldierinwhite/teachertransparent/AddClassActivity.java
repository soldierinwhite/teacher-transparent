package io.github.soldierinwhite.teachertransparent;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.github.soldierinwhite.teachertransparent.data.ClassesContract;
import io.github.soldierinwhite.teachertransparent.util.DataUtils;

/**
 * Created by schoo on 2017/03/14.
 */

public class AddClassActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final int CLASS_LOADER = 0;

    private ArrayList<Mentor> mentors;
    private Uri currentClassUri;
    private Cursor mCursor;

    private EditText classNameEditText, subjectEditText, mentorNameEditText, mentorEmailEditText;
    private LinearLayout mentorLinearLayout;
    private ArrayList<View> mentorListViewCustom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        classNameEditText = (EditText) findViewById(R.id.class_name_edit_text);
        subjectEditText = (EditText) findViewById(R.id.subject_edit_text);
        mentorNameEditText = (EditText) findViewById(R.id.mentors_name_edit_text);
        mentorEmailEditText = (EditText) findViewById(R.id.mentors_email_edit_text);
        mentorLinearLayout = (LinearLayout) findViewById(R.id.mentor_linear_layout);
        mentors = new ArrayList<>();
        mentorListViewCustom = new ArrayList<>();

        currentClassUri = getIntent().getData();

        if(currentClassUri == null){
            setTitle("Add new class");
            invalidateOptionsMenu();
        }
        else{
            setTitle("Edit class");
            getSupportLoaderManager().initLoader(CLASS_LOADER, null, this);
        }
    }

    public void addMentor(View view){
        String mentorName = mentorNameEditText.getText().toString().trim();
        if(mentorName.isEmpty() || mentorName == null){
            Toast.makeText(this, "Needs a valid mentor name", Toast.LENGTH_SHORT).show();
            return;
        }
        String mentorEmail = mentorEmailEditText.getText().toString().trim();
        if(mentorEmail.isEmpty() || mentorEmail == null){
            Toast.makeText(this, "Needs a valid mentor email", Toast.LENGTH_SHORT).show();
            return;
        }

        Mentor mentor = new Mentor(mentorName, mentorEmail);
        mentors.add(mentor);

        addMentorListViewTab(mentorName, mentorEmail);

        mentorNameEditText.setText(null);
        mentorEmailEditText.setText(null);
    }

    public void addClass(View view){
        String className = classNameEditText.getText().toString().trim();
        if(className.isEmpty() || className == null){
            Toast.makeText(this, "Needs a valid class name", Toast.LENGTH_SHORT).show();
            return;
        }
        String subject = subjectEditText.getText().toString().trim();
        if(subject.isEmpty() || subject == null){
            Toast.makeText(this, "Needs a valid subject name", Toast.LENGTH_SHORT).show();
            return;
        }

        if(mentors.isEmpty()){
            Toast.makeText(this, "Needs an assigned mentor teacher with email", Toast.LENGTH_SHORT).show();
            return;
        }

        if(currentClassUri == null) {
            String resultMsg = DataUtils.insertClass(this, className, subject, mentors);
            Toast.makeText(this, resultMsg, Toast.LENGTH_SHORT).show();
        } else{
            String resultMsg = DataUtils.updateClass(this, currentClassUri, className, subject, mentors);
            Toast.makeText(this, resultMsg, Toast.LENGTH_SHORT).show();
        }

        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void addMentorListViewTab(String mentorName, String mentorEmail){
        mentorListViewCustom.add(getLayoutInflater().inflate(R.layout.mentor_list_item, null));
        ((TextView) mentorListViewCustom.get(mentorListViewCustom.size()-1).findViewById(R.id.mentor_name_list_item)).setText(mentorName);
        ((TextView) mentorListViewCustom.get(mentorListViewCustom.size()-1).findViewById(R.id.mentor_email_list_item)).setText(mentorEmail);
        mentorListViewCustom.get(mentorListViewCustom.size()-1).findViewById(R.id.mentor_delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ViewGroup)view.getParent().getParent()).removeView((ViewGroup)view.getParent());
                mentors.remove(mentorListViewCustom.indexOf(view.getParent()));
                mentorListViewCustom.remove(mentorListViewCustom.indexOf(view.getParent()));
            }
        });


        mentorLinearLayout.addView(mentorListViewCustom.get(mentorListViewCustom.size()-1));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ClassesContract.ClassEntry._ID,
                ClassesContract.ClassEntry.COLUMN_CLASS_NAME,
                ClassesContract.ClassEntry.COLUMN_SUBJECT,
                ClassesContract.ClassEntry.COLUMN_CLASS_MENTORS,
                ClassesContract.ClassEntry.COLUMN_MENTORS_EMAIL};

        switch(id){
            case CLASS_LOADER:
                String selection = ClassesContract.ClassEntry._ID + "=?";
                String[] selectionArgs = new String[]{String.valueOf(ContentUris.parseId(currentClassUri))};
                return new CursorLoader(
                        this,
                        ClassesContract.ClassEntry.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        null);
            default:
                //invalid loader id
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        if(mCursor.getCount() > 0){
            mCursor.moveToFirst();
            classNameEditText.setText(mCursor.getString(mCursor.getColumnIndexOrThrow(ClassesContract.ClassEntry.COLUMN_CLASS_NAME)));
            subjectEditText.setText(mCursor.getString(mCursor.getColumnIndexOrThrow(ClassesContract.ClassEntry.COLUMN_SUBJECT)));
            mentors = DataUtils.mentorArrayListFromCursorString(
                    mCursor.getString(mCursor.getColumnIndexOrThrow(ClassesContract.ClassEntry.COLUMN_CLASS_MENTORS)),
                    mCursor.getString(mCursor.getColumnIndexOrThrow(ClassesContract.ClassEntry.COLUMN_MENTORS_EMAIL)));
            for(Mentor mentor : mentors){
                addMentorListViewTab(mentor.getMentorName(), mentor.getMentorEmail());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
    }
}
