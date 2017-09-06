package io.github.soldierinwhite.teachertransparent;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.github.soldierinwhite.teachertransparent.data.ClassesContract;
import io.github.soldierinwhite.teachertransparent.data.ClassesProvider;

public class MainActivity extends AppCompatActivity implements BehaviorTypeFragment.OnBehaviorTypeFragmentInteractionListener,
        AddStudentFragment.OnAddStudentFragmentInteractionListener,
        LoaderManager.LoaderCallbacks<Cursor>,
        BadBehaviorFragment.OnBadBehaviorFragmentInteractionListener {

    private static final String LOG_TAG = "MainActivityTag";

    private final int CLASS_LOADER = 0;
    private final int STUDENT_LOADER = 1;
    private final int STUDENTS_FROM_MENTOR_CLASS_LOADER = 2;

    private final String CLASS_ID_BUNDLE_KEY = "class_id";
    private final String CLASS_NAME_BUNDLE_KEY = "class_name";

    private final String VIEW_CLASSES = "classes_adapter_active";
    private final String VIEW_STUDENTS = "students_adapter_active";

    private ClassCursorAdapter mClassAdapter;
    private StudentCursorAdapter mStudentAdapter;

    private ListView classListView, studentListView;

    private boolean studentsToggle = false; //switch indicating which listview is active to signal to menu and other elements
    private String currentClassName, currentClassSubject;
    private long currentClassId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        classListView = (ListView) findViewById(R.id.main_activity_class_list);
        studentListView = (ListView) findViewById(R.id.main_activity_student_list);
        studentListView.setVisibility(View.GONE);
        mStudentAdapter = new StudentCursorAdapter(this, null);
        studentListView.setAdapter(mStudentAdapter);

        mClassAdapter = new ClassCursorAdapter(this, null);
        classListView.setAdapter(mClassAdapter);

        classListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentClassName = ((TextView) view.findViewById(R.id.class_name)).getText().toString();
                Cursor classCursor = (Cursor) adapterView.getItemAtPosition(i);
                currentClassSubject = classCursor.getString(classCursor.getColumnIndexOrThrow(ClassesContract.ClassEntry.COLUMN_SUBJECT));
                currentClassId = l;
                loadStudents(l);
            }
        });

        classListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent editClass = new Intent(MainActivity.this, AddClassActivity.class);
                editClass.setData(ContentUris.withAppendedId(ClassesContract.ClassEntry.CONTENT_URI, l));
                startActivity(editClass);
                return true;
            }
        });

        studentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor item = (Cursor) adapterView.getItemAtPosition(i);
                Bundle args = new Bundle();
                args.putString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME, item.getString(item.getColumnIndexOrThrow(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME)));
                args.putString(ClassesContract.StudentEntry.COLUMN_STUDENT_LAST_NAME, item.getString(item.getColumnIndexOrThrow(ClassesContract.StudentEntry.COLUMN_STUDENT_LAST_NAME)));
                args.putString(ClassesContract.StudentEntry.COLUMN_PARENT_EMAIL, item.getString(item.getColumnIndexOrThrow(ClassesContract.StudentEntry.COLUMN_PARENT_EMAIL)));
                args.putString(ClassesContract.StudentEntry.COLUMN_MENTOR_EMAIL, item.getString(item.getColumnIndexOrThrow(ClassesContract.StudentEntry.COLUMN_MENTOR_EMAIL)));
                args.putString(ClassesContract.StudentEntry.COLUMN_PARENT_FIRST_NAME, item.getString(item.getColumnIndexOrThrow(ClassesContract.StudentEntry.COLUMN_PARENT_FIRST_NAME)));
                args.putLong(ClassesContract.StudentEntry._ID, item.getLong(item.getColumnIndexOrThrow(ClassesContract.StudentEntry._ID)));
                BehaviorTypeFragment behaviorTypeFragment = BehaviorTypeFragment.newInstance(args);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.fragment_frame_main_activity, behaviorTypeFragment, BehaviorTypeFragment.TAG);
                transaction.commit();
            }
        });

        studentListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                return false;
            }
        });

        getSupportLoaderManager().initLoader(CLASS_LOADER, null, this);

    }

    private void loadStudents(long classId) {
        Bundle args = new Bundle();
        args.putLong(CLASS_ID_BUNDLE_KEY, classId);
        getSupportLoaderManager().restartLoader(STUDENT_LOADER, args, this);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag(AddStudentFragment.TAG) != null) {
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag(AddStudentFragment.TAG)).commit();
        } else if (mStudentAdapter.getCursor() == null)
            finish();
        else {
            getSupportLoaderManager().restartLoader(CLASS_LOADER, null, this);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (studentsToggle) {
            MenuItem addClassItem = menu.findItem(R.id.action_add_class);
            addClassItem.setVisible(false);
            MenuItem studentEditor = menu.findItem(R.id.action_student_editor);
            studentEditor.setVisible(false);
        } else {
            MenuItem addStudentItem = menu.findItem(R.id.action_add_student);
            addStudentItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_class:
                Intent addClass = new Intent(this, AddClassActivity.class);
                startActivity(addClass);
                return true;
            case R.id.action_add_student:
                if (mStudentAdapter.getCursor().getCount() == 0) {
                    showMentorClassAddDialog();
                } else
                    startAddStudentFragment();

                return true;
            case R.id.action_student_editor:
                Intent studentEditor = new Intent(this, StudentEditorActivity.class);
                startActivity(studentEditor);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBehaviorTypeFragmentInteraction(int actionId) {
        if (actionId == BehaviorTypeFragment.BAD) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Bundle args = getSupportFragmentManager().findFragmentByTag(BehaviorTypeFragment.TAG).getArguments();
            BadBehaviorFragment bb = BadBehaviorFragment.newInstance(args);
            ft.remove(getSupportFragmentManager().findFragmentByTag(BehaviorTypeFragment.TAG));
            ft.add(R.id.fragment_frame_main_activity, bb, BadBehaviorFragment.TAG);
            ft.commit();
        } else {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            Bundle args = getSupportFragmentManager().findFragmentByTag(BehaviorTypeFragment.TAG).getArguments();
            emailIntent.setData(Uri.parse("mailto:")); // only email apps should handle this
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{args.getString(ClassesContract.StudentEntry.COLUMN_PARENT_EMAIL), args.getString(ClassesContract.StudentEntry.COLUMN_MENTOR_EMAIL)});
            String subject = "Praise for " + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME) + "'s behavior in class";
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            String message = "Dear " + args.getString(ClassesContract.StudentEntry.COLUMN_PARENT_FIRST_NAME)
                    + "\n\n"
                    + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME)
                    + "'s behavior today in "
                    + currentClassSubject
                    + " is appreciated and the school encourages "
                    + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME)
                    + " to continue being a positive influence in the classroom.\n\n";
            switch (actionId) {
                case BehaviorTypeFragment.ENGAGING_LESSON:
                    message = message + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME) + " has been engaging in class today, trying to gain the most out of the learning opportunity of the lesson.";
                    break;
                case BehaviorTypeFragment.KINDNESS:
                    message = message + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME) + " has shown noteworthy kindness in class today, an example to fellow classmates of how to make school a welcome, safe space for everyone.";
                    break;
                case BehaviorTypeFragment.CONTRIBUTION_TO_LEARNING_ENVIRONMENT:
                    message = message + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME) + " has been positively contributing towards a good learning environment today, making learning easier for the whole class.";
                    break;
                case BehaviorTypeFragment.WORKING_GOAL_ORIENTED:
                    message = message + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME) + " has been working goal-oriented during today's lesson, an example to other classmates of how to apply a growth mindset to learning.";
                    break;
                case BehaviorTypeFragment.HELPFUL:
                    message = message + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME) + " has been helping others who needed guidance in class today, an example to classmates of how to be selfless and kind.";
                    break;
                case BehaviorTypeFragment.POLITENESS:
                    message = message + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME) + " has been polite to others in class today, an example to classmates of how respect in the classroom can create a more pleasant learning environment for everyone.";
                    break;
                case BehaviorTypeFragment.OTHER:
                    message = message + "[Teacher, enter description here]";
                    break;
                default:
                    throw new IllegalArgumentException("Unknown action id: " + actionId);
            }
            message = message + "\n\nPlease help us encourage " + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME)
                    +" to continue with such a positive attitude in school. This remark will be documented with others in order to build a picture of "
                    + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME) + "'s"
                    + " behavior in school over time. Behavioral development can be discussed with " + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME) + "'s mentor, who will also be updated.\n\n";
            emailIntent.putExtra(Intent.EXTRA_TEXT, message);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.remove(getSupportFragmentManager().findFragmentByTag(BehaviorTypeFragment.TAG));
            ft.commit();
            if (emailIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(emailIntent);
            }
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case CLASS_LOADER:
                String[] classProjection = {
                        ClassesContract.ClassEntry._ID,
                        ClassesContract.ClassEntry.COLUMN_CLASS_NAME,
                        ClassesContract.ClassEntry.COLUMN_SUBJECT,
                        ClassesContract.ClassEntry.COLUMN_CLASS_MENTORS,
                        ClassesContract.ClassEntry.COLUMN_MENTORS_EMAIL};
                return new CursorLoader(
                        this,
                        ClassesContract.ClassEntry.CONTENT_URI,
                        classProjection,
                        null,
                        null,
                        null);
            case STUDENT_LOADER:
                String classId = String.valueOf(args.getLong(CLASS_ID_BUNDLE_KEY));
                Uri uri = Uri.withAppendedPath(ClassesProvider.CONTENT_URI_STUDENTS_JOIN_CLASS_GET_STUDENTS, classId);
                return new CursorLoader(
                        this,
                        uri,
                        null,
                        null,
                        null,
                        ClassesContract.StudentEntry.COLUMN_STUDENT_LAST_NAME);
            case STUDENTS_FROM_MENTOR_CLASS_LOADER:
                String[] projection = {ClassesContract.StudentEntry._ID};
                String selection = ClassesContract.StudentEntry.COLUMN_MENTOR_CLASS_NAME + "=?";
                String[] selectionArgs = {args.getString(CLASS_NAME_BUNDLE_KEY)};
                String orderBy = ClassesContract.StudentEntry.COLUMN_STUDENT_LAST_NAME;
                return new CursorLoader(this,
                        ClassesContract.StudentEntry.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        orderBy);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int loaderId = loader.getId();
        switch (loaderId) {
            case CLASS_LOADER:
                mClassAdapter.swapCursor(data);
                toggleAdapter(VIEW_CLASSES);
                break;
            case STUDENT_LOADER:
                mStudentAdapter.swapCursor(data);
                toggleAdapter(VIEW_STUDENTS);
                break;
            case STUDENTS_FROM_MENTOR_CLASS_LOADER:
                ContentValues[] cv;
                List<ContentValues> valueList = new ArrayList<ContentValues>();
                if (data.moveToFirst()) {
                    while (!data.isAfterLast()) {
                        ContentValues newValue = new ContentValues();
                        newValue.put(ClassesContract.StudentsClassesEntry.CLASSES_ID, currentClassId);
                        newValue.put(ClassesContract.StudentsClassesEntry.STUDENTS_ID, data.getLong(data.getColumnIndexOrThrow(ClassesContract.StudentEntry._ID)));
                        valueList.add(newValue);
                        data.moveToNext();
                    }
                }
                cv = new ContentValues[valueList.size()];
                valueList.toArray(cv);
                int rowsAdded = getContentResolver().bulkInsert(ClassesContract.StudentsClassesEntry.CONTENT_URI, cv);
                if (rowsAdded == 0) {
                    Toast.makeText(this, "Unable to insert students", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Number of students added: " + rowsAdded, Toast.LENGTH_SHORT).show();
                    mStudentAdapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        int loaderId = loader.getId();
        switch (loaderId) {
            case CLASS_LOADER:
                mClassAdapter.swapCursor(null);
                break;
            case STUDENT_LOADER:
                mStudentAdapter.swapCursor(null);
                break;
            default:
                break;
        }

    }

    private void toggleAdapter(String activeAdapter) {
        switch (activeAdapter) {
            case VIEW_CLASSES:
                mStudentAdapter.swapCursor(null);
                studentListView.setVisibility(View.GONE);
                classListView.setVisibility(View.VISIBLE);
                currentClassName = null;
                currentClassId = -1;
                studentsToggle = false;
                invalidateOptionsMenu();
                break;
            case VIEW_STUDENTS:
                mClassAdapter.swapCursor(null);
                classListView.setVisibility(View.GONE);
                studentListView.setVisibility(View.VISIBLE);
                studentsToggle = true;
                invalidateOptionsMenu();
                break;
        }
    }

    private void showMentorClassAddDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setMessage("Do you want to add all the students in the mentor class");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Bundle args = new Bundle();
                args.putLong(CLASS_ID_BUNDLE_KEY, currentClassId);
                args.putString(CLASS_NAME_BUNDLE_KEY, currentClassName);
                addMentorClassStudents(args);
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startAddStudentFragment();
            }
        });
        dialog.create();
        dialog.show();
    }

    private void addMentorClassStudents(Bundle args) {
        getSupportLoaderManager().initLoader(STUDENTS_FROM_MENTOR_CLASS_LOADER, args, this);
    }

    @Override
    public void onAddStudentFragmentInteraction(Uri uri) {
        if (uri != null) {
            mStudentAdapter.notifyDataSetChanged();
        }
        getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag(AddStudentFragment.TAG)).commit();
    }

    public void startAddStudentFragment() {
        Cursor cursor = mStudentAdapter.getCursor();
        String[] studentsIds;
        List<String> idsList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                idsList.add(String.valueOf(cursor.getLong(cursor.getColumnIndexOrThrow(ClassesContract.StudentEntry._ID))));
                cursor.moveToNext();
            }
        }
        studentsIds = new String[idsList.size()];
        idsList.toArray(studentsIds);
        AddStudentFragment addStudentFragment = AddStudentFragment.newInstance(currentClassId, studentsIds);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_frame_main_activity, addStudentFragment, AddStudentFragment.TAG);
        transaction.commit();
    }

    @Override
    public void onBadBehaviorFragmentInteraction(int offenseId) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        Bundle args = getSupportFragmentManager().findFragmentByTag(BadBehaviorFragment.TAG).getArguments();
        emailIntent.setData(Uri.parse("mailto:")); // only email apps should handle this
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{args.getString(ClassesContract.StudentEntry.COLUMN_PARENT_EMAIL), args.getString(ClassesContract.StudentEntry.COLUMN_MENTOR_EMAIL)});
        String subject = "Behavior notice for " + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);

        //TODO: Write event text description to log file
        String logFileName = ClassesContract.StudentEntry.COLUMN_LOG_FILE_NAME;
        String message = "Dear " + args.getString(ClassesContract.StudentEntry.COLUMN_PARENT_FIRST_NAME)
                + "\n\n"
                + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME)
                + " has behaved inappropriately today in "
                + currentClassSubject
                + ".\n\n";

        switch (offenseId) {
            case BadBehaviorFragment.TALKING_OVER_TEACHER:
                message = message + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME)
                        + " has been a disruptive influence in class by talking while the teacher was addressing the class.";
                break;
            case BadBehaviorFragment.SPEAKING_OUT_OF_TURN:
                message = message + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME)
                        + " has been a disruptive influence in class by speaking out of turn.";
                break;
            case BadBehaviorFragment.DISRUPTING_CLASSMATES:
                message = message + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME)
                        + " has been a disruptive influence in class by disrupting classmates in their work.";
                break;
            case BadBehaviorFragment.NOISE_MAKING:
                message = message + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME)
                        + " has been a disruptive influence in class by deliberately making noises to disturb others.";
                break;
            case BadBehaviorFragment.DISTRACTIVE_BEHAVIOR:
                message = message + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME)
                        + " has been a disruptive influence in class by trying to distract others from their work.";
                break;
            case BadBehaviorFragment.ABUSING_CLASSMATE_VERBAL:
                message = message + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME)
                        + " has verbally abused a classmate. Please note that this is a serious offence related to bullying.";
                break;
            case BadBehaviorFragment.ABUSING_TEACHER_VERBAL:
                message = message + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME)
                        + " has verbally abused the teacher. Please note that this is a serious offence related to bullying and undermining of the teacher's role as facilitator.";
                break;
            case BadBehaviorFragment.ABUSING_STUDENT_IN_SCHOOL_VERBAL:
                message = message + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME)
                        + " has verbally abused another student from another class. Please note that this is a serious offence related to bullying.";
                break;
            case BadBehaviorFragment.ABUSING_ANOTHER_TEACHER_VERBAL:
                message = message + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME)
                        + " has verbally abused another teacher. Please note that this is a serious offence related to bullying and undermining of the teacher's role as facilitator.";
                break;
            case BadBehaviorFragment.NO_COOPERATION:
                message = message + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME)
                        + " has been refusing to cooperate at all during lesson time. This behavior undermines "
                        + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME)
                        + "'s learning progress as well as the learning environment.";
                break;
            case BadBehaviorFragment.MINIMAL_COOPERATION:
                message = message + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME)
                        + " has only been giving minimal cooperation during lesson time. This behavior undermines "
                        + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME)
                        + "'s learning progress as well as the learning environment.";
                break;
            case BadBehaviorFragment.LITTLE_COOPERATION:
                message = message + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME)
                        + " has been giving little cooperation during lesson time. This behavior undermines "
                        + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME)
                        + "'s learning progress as well as the learning environment.";
                break;
            case BadBehaviorFragment.ABUSING_CLASSMATE_PHYSICAL:
                message = message + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME)
                        + " has physically abused a classmate. Please note that this is a serious offence related to school safety, bullying and violent behavior.";
                break;
            case BadBehaviorFragment.ABUSING_TEACHER_PHYSICAL:
                message = message + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME)
                        + " has physically abused the teacher. Please note that this is a serious offence related to school safety, bullying and violent behavior.";
                break;
            case BadBehaviorFragment.ABUSING_OTHER_PHYSICAL:
                message = message + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME)
                        + " has physically abused someone from outside the classroom during lesson time. Please note that this is a serious offence related to school safety, bullying and violent behavior.";
                break;
            case BadBehaviorFragment.SCHOOL_PROPERTY_DAMAGE:
                message = message + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME)
                        + " has damaged school property on purpose. Please note that this is a serious offence related to vandalism.";
                break;
            case BadBehaviorFragment.CLASSMATE_PROPERTY_DAMAGE:
                message = message + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME)
                        + " has damaged a classmate's property on purpose. Please note that this is a serious offence related to vandalism.";
                break;
            case BadBehaviorFragment.TEACHER_PROPERTY_DAMAGE:
                message = message + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME)
                        + " has damaged the teacher's property on purpose. Please note that this is a serious offence related to vandalism.";
                break;
            case BadBehaviorFragment.OWN_PROPERTY_DAMAGE:
                message = message + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME)
                        + " has damaged his/her own personal property on purpose.";
                break;
            case BadBehaviorFragment.OTHER_PROPERTY_DAMAGE:
                message = message + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME)
                        + " has damaged another's property on purpose.";
                break;
            case BadBehaviorFragment.CLASS_RULE_VIOLATION:
                message = message + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME)
                        + " has violated one of the class rules. [Teacher, enter description here]";
                break;
            case BadBehaviorFragment.OTHER:
                message = message + "[Teacher, enter description here]";
                break;
            default:
                throw new IllegalArgumentException("Offense ID not found.");
        }
        message = message + "\n\nPlease address this behavior in an appropriate manner at home. The incident will be documented with others in order to build a picture of "
                + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME) + "'s"
                + " behavior in school over time. Behavioral development can be discussed with " + args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME) + "'s mentor, who will also be updated.";
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(emailIntent);
        }
    }
}
