package io.github.soldierinwhite.teachertransparent;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.github.soldierinwhite.teachertransparent.data.ClassesContract;
import io.github.soldierinwhite.teachertransparent.data.ClassesProvider;

/**
 * Created by schoo on 2017/05/31.
 */

public class StudentEditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        NewMentorClassFragment.OnNewMentorClassFragmentInteractionListener,
        NewStudentEntryFragment.OnNewStudentFragmentInteractionListener {
    private final String LOG_TAG = "StudentEditorActivity";

    private MentorClassCursorAdapter mentorClassCursorAdapter;
    private StudentCursorAdapter studentCursorAdapter;

    private boolean isAdapterMentorClasses;

    private TextView titleLabelTextView;
    private ListView mentorClassListView;
    private View parentView;

    private ContentValues newStudent;

    private final int MENTOR_CLASS_LOADER_ID = 111;
    private final int STUDENT_LOADER_ID = 222;

    private final String STUDENT_LOADER_ARGS_KEY = "student_args";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_editor);

        parentView = findViewById(R.id.student_editor_parent);

        titleLabelTextView = (TextView) findViewById(R.id.mentor_classes_label);
        titleLabelTextView.setText("Mentor classes");
        mentorClassListView = (ListView) findViewById(R.id.mentor_classes_list_view);
        mentorClassCursorAdapter = new MentorClassCursorAdapter(this, null);
        studentCursorAdapter = new StudentCursorAdapter(this, null);
        mentorClassListView.setAdapter(mentorClassCursorAdapter);
        isAdapterMentorClasses = true;
        mentorClassListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (isAdapterMentorClasses) {
                    titleLabelTextView.setText("Students in " + ((TextView)view.findViewById(R.id.mentor_class_list_item_text_view)).getText().toString());
                    mentorClassListView.setAdapter(studentCursorAdapter);
                    isAdapterMentorClasses = false;
                    Bundle args = new Bundle();
                    String mentorClassName = ((TextView) view.findViewById(R.id.mentor_class_list_item_text_view)).getText().toString();
                    args.putString(STUDENT_LOADER_ARGS_KEY, mentorClassName);
                    startLoadingStudents(args);
                } else {
                    NewStudentEntryFragment editStudentFragment = NewStudentEntryFragment.newInstance();
                    Cursor cursor = ((StudentCursorAdapter) adapterView.getAdapter()).getCursor();
                    if (cursor.moveToPosition(i)) {
                        Bundle args = new Bundle();
                        args.putLong(ClassesContract.StudentEntry._ID, cursor.getLong(cursor.getColumnIndexOrThrow(ClassesContract.StudentEntry._ID)));
                        args.putString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME,
                                cursor.getString(cursor.getColumnIndexOrThrow(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME)));
                        args.putString(ClassesContract.StudentEntry.COLUMN_STUDENT_LAST_NAME,
                                cursor.getString(cursor.getColumnIndexOrThrow(ClassesContract.StudentEntry.COLUMN_STUDENT_LAST_NAME)));
                        args.putString(ClassesContract.StudentEntry.COLUMN_STUDENT_PERSONAL_IDENTIDICATION_NUMBER,
                                cursor.getString(cursor.getColumnIndexOrThrow(ClassesContract.StudentEntry.COLUMN_STUDENT_PERSONAL_IDENTIDICATION_NUMBER)));
                        args.putString(ClassesContract.StudentEntry.COLUMN_PARENT_FIRST_NAME,
                                cursor.getString(cursor.getColumnIndexOrThrow(ClassesContract.StudentEntry.COLUMN_PARENT_FIRST_NAME)));
                        args.putString(ClassesContract.StudentEntry.COLUMN_PARENT_EMAIL,
                                cursor.getString(cursor.getColumnIndexOrThrow(ClassesContract.StudentEntry.COLUMN_PARENT_EMAIL)));
                        args.putString(ClassesContract.StudentEntry.COLUMN_PARENT_PHONE_NUMBER,
                                cursor.getString(cursor.getColumnIndexOrThrow(ClassesContract.StudentEntry.COLUMN_PARENT_PHONE_NUMBER)));
                        args.putString(ClassesContract.StudentEntry.COLUMN_MENTOR_NAME,
                                cursor.getString(cursor.getColumnIndexOrThrow(ClassesContract.StudentEntry.COLUMN_MENTOR_NAME)));
                        args.putString(ClassesContract.StudentEntry.COLUMN_MENTOR_EMAIL,
                                cursor.getString(cursor.getColumnIndexOrThrow(ClassesContract.StudentEntry.COLUMN_MENTOR_EMAIL)));
                        args.putString(ClassesContract.StudentEntry.COLUMN_MENTOR_CLASS_NAME,
                                cursor.getString(cursor.getColumnIndexOrThrow(ClassesContract.StudentEntry.COLUMN_MENTOR_CLASS_NAME)));

                        editStudentFragment.setArguments(args);
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, editStudentFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }

                }
            }
        });

        FloatingActionButton addNewMentorClass = (FloatingActionButton) findViewById(R.id.add_mentor_class_button);
        addNewMentorClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isAdapterMentorClasses) {
                    Bundle allMentorClasses = new Bundle();
                    ArrayList<String> mentorClasses = new ArrayList<>();
                    Cursor cursor = mentorClassCursorAdapter.getCursor();
                    if (cursor != null) {
                        cursor.moveToFirst();
                        while (!cursor.isAfterLast()) {
                            mentorClasses.add(cursor.getString(cursor.getColumnIndexOrThrow(ClassesContract.StudentEntry.COLUMN_MENTOR_CLASS_NAME)));
                            cursor.moveToNext();
                        }
                    }
                    allMentorClasses.putSerializable(NewMentorClassFragment.EXISTING_MENTOR_CLASS_NAMES_KEY, mentorClasses);
                    Fragment newMentorClassFragment = NewMentorClassFragment.newInstance(allMentorClasses);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, newMentorClassFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                else{
                    Cursor cursor = ((StudentCursorAdapter)mentorClassListView.getAdapter()).getCursor();
                    cursor.moveToFirst();
                    String mentorClassName = cursor.getString(cursor.getColumnIndexOrThrow(ClassesContract.StudentEntry.COLUMN_MENTOR_CLASS_NAME));
                    newStudent = new ContentValues();
                    newStudent.put(ClassesContract.StudentEntry.COLUMN_MENTOR_CLASS_NAME, mentorClassName);
                    Fragment newStudentEntryFragment = NewStudentEntryFragment.newInstance();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, newStudentEntryFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    transaction.commit();
                }
            }
        });

        getSupportLoaderManager().initLoader(MENTOR_CLASS_LOADER_ID, null, this);

    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();
        else {
            super.onBackPressed();
        }
    }



    private void startLoadingStudents(Bundle args) {
        getSupportLoaderManager().initLoader(STUDENT_LOADER_ID, args, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case MENTOR_CLASS_LOADER_ID:
                return new CursorLoader(this,
                        ClassesProvider.CONTENT_URI_DISTINCT_MENTOR_CLASSES,
                        null,
                        null,
                        null,
                        null);
            case STUDENT_LOADER_ID:
                String selection = ClassesContract.StudentEntry.COLUMN_MENTOR_CLASS_NAME + "=?";
                String[] selectionArgs = {args.getString(STUDENT_LOADER_ARGS_KEY)};
                return new CursorLoader(this,
                        ClassesContract.StudentEntry.CONTENT_URI,
                        null,
                        selection,
                        selectionArgs,
                        ClassesContract.StudentEntry.COLUMN_STUDENT_LAST_NAME);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int loaderId = loader.getId();
        switch (loaderId) {
            case MENTOR_CLASS_LOADER_ID:
                mentorClassCursorAdapter.swapCursor(data);
                break;
            case STUDENT_LOADER_ID:
                studentCursorAdapter.swapCursor(data);
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        int loaderId = loader.getId();
        switch (loaderId) {
            case MENTOR_CLASS_LOADER_ID:
                mentorClassCursorAdapter.swapCursor(null);
                break;
            case STUDENT_LOADER_ID:
                studentCursorAdapter.swapCursor(null);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNewMentorClassFragmentInteraction(String newMentorClassName) {
        newStudent = new ContentValues();
        newStudent.put(ClassesContract.StudentEntry.COLUMN_MENTOR_CLASS_NAME, newMentorClassName);
        Fragment newStudentEntryFragment = NewStudentEntryFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newStudentEntryFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onNewStudentEntryFragmentInteraction(Bundle data) {
        if(!data.containsKey(NewStudentEntryFragment.DELETE_ISSUE_KEY)) {
            if (data.containsKey(NewStudentEntryFragment.MENTOR_CLASS_KEY)) {
                newStudent = new ContentValues();
                newStudent.put(ClassesContract.StudentEntry.COLUMN_MENTOR_CLASS_NAME, data.getString(NewStudentEntryFragment.MENTOR_CLASS_KEY));
            }
            newStudent.put(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME, data.getString(NewStudentEntryFragment.FIRST_NAME_KEY));
            newStudent.put(ClassesContract.StudentEntry.COLUMN_STUDENT_LAST_NAME, data.getString(NewStudentEntryFragment.LAST_NAME_KEY));
            newStudent.put(ClassesContract.StudentEntry.COLUMN_PARENT_FIRST_NAME, data.getString(NewStudentEntryFragment.PARENT_NAME_KEY));
            newStudent.put(ClassesContract.StudentEntry.COLUMN_PARENT_EMAIL, data.getString(NewStudentEntryFragment.PARENT_EMAIL_KEY));
            newStudent.put(ClassesContract.StudentEntry.COLUMN_PARENT_PHONE_NUMBER, data.getString(NewStudentEntryFragment.PARENT_PHONE_KEY));
            if (!data.containsKey(NewStudentEntryFragment.MENTOR_CLASS_KEY)) {
                newStudent.put(ClassesContract.StudentEntry.COLUMN_STUDENT_PERSONAL_IDENTIDICATION_NUMBER, data.getString(NewStudentEntryFragment.PERSONAL_NO_KEY));
                String newFileName = data.getString(NewStudentEntryFragment.PERSONAL_NO_KEY);
                newStudent.put(ClassesContract.StudentEntry.COLUMN_LOG_FILE_NAME, newFileName);
            }
            newStudent.put(ClassesContract.StudentEntry.COLUMN_MENTOR_NAME, data.getString(NewStudentEntryFragment.MENTOR_NAME_KEY));
            newStudent.put(ClassesContract.StudentEntry.COLUMN_MENTOR_EMAIL, data.getString(NewStudentEntryFragment.MENTOR_EMAIL_KEY));

            if (!data.containsKey(NewStudentEntryFragment.MENTOR_CLASS_KEY)) {
                Uri result = getContentResolver().insert(ClassesContract.StudentEntry.CONTENT_URI, newStudent);
                if(result.getLastPathSegment().equalsIgnoreCase(ClassesProvider.PERSONAL_NUMBER_CLASH)){
                    Toast.makeText(this, "Personal number not unique. Student not added", Toast.LENGTH_SHORT).show();
                } else if (result != null){
                    Toast.makeText(this, "Student added", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "Student insert failed", Toast.LENGTH_SHORT).show();
                }
            } else {
                String selection = ClassesContract.StudentEntry._ID + "=?";
                String[] selectionArgs = {String.valueOf(data.getLong(ClassesContract.StudentEntry._ID))};
                int updatedRows = getContentResolver().update(ContentUris.withAppendedId(ClassesContract.StudentEntry.CONTENT_URI, data.getLong(ClassesContract.StudentEntry._ID)),
                        newStudent,
                        selection,
                        selectionArgs);
                if (updatedRows == 0) {
                    Toast.makeText(this, "Error saving student", Toast.LENGTH_SHORT).show();
                }
            }
        }

        getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.fragment_container)).commit();

        getSupportLoaderManager().restartLoader(MENTOR_CLASS_LOADER_ID, null, this);
    }
}
