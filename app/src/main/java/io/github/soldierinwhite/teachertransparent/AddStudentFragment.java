package io.github.soldierinwhite.teachertransparent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.github.soldierinwhite.teachertransparent.data.ClassesContract;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnAddStudentFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddStudentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddStudentFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String CLASS_ID_ARGS_KEY = "class_id_args";
    private static final String STUDENTS_IDS_ARGS_KEY = "students_ids_args";

    public static final String TAG = "add_student_fragment";

    private final int AVAILABLE_STUDENTS_LOADER = 1111;

    private OnAddStudentFragmentInteractionListener mListener;

    private Cursor availableStudentsCursor;

    private AutoCompleteTextView nameAutoComplete;
    private ArrayAdapter nameAdapter;
    private String names[];
    private Bundle idIndexPairs;

    public AddStudentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment
     *
     * @return A new instance of fragment AddStudentFragment.
     */
    public static AddStudentFragment newInstance(long classId, String[] studentsIds) {
        AddStudentFragment fragment = new AddStudentFragment();
        Bundle args = new Bundle();
        args.putLong(CLASS_ID_ARGS_KEY, classId);
        if(studentsIds.length > 0)
            args.putStringArray(STUDENTS_IDS_ARGS_KEY, studentsIds);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_student, container, false);

        nameAutoComplete = (AutoCompleteTextView) rootView.findViewById(R.id.student_name_autocomplete);
        nameAutoComplete.setThreshold(1);
        nameAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                nameAutoComplete.setText(names[position]);
                availableStudentsCursor.moveToPosition(idIndexPairs.getInt(String.valueOf(id)));
                ContentValues cv = new ContentValues();
                cv.put(ClassesContract.StudentsClassesEntry.STUDENTS_ID,
                        availableStudentsCursor.getLong(availableStudentsCursor.getColumnIndexOrThrow(ClassesContract.StudentEntry._ID)));
                cv.put(ClassesContract.StudentsClassesEntry.CLASSES_ID, getArguments().getLong(CLASS_ID_ARGS_KEY));
                Uri result = getActivity().getContentResolver().insert(ClassesContract.StudentsClassesEntry.CONTENT_URI, cv);
                if (result != null) {
                    Toast.makeText(getContext(), "Student added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Insertion failed. Try again.", Toast.LENGTH_SHORT).show();
                }
                onButtonPressed(result);
            }
        });

        getActivity().getSupportLoaderManager().restartLoader(AVAILABLE_STUDENTS_LOADER, getArguments(), this);
        return rootView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onAddStudentFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAddStudentFragmentInteractionListener) {
            mListener = (OnAddStudentFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAddStudentFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnAddStudentFragmentInteractionListener {

        void onAddStudentFragmentInteraction(Uri uri);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case AVAILABLE_STUDENTS_LOADER:
                String[] projection = {ClassesContract.StudentEntry._ID,
                        ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME,
                        ClassesContract.StudentEntry.COLUMN_STUDENT_LAST_NAME,
                        ClassesContract.StudentEntry.COLUMN_MENTOR_CLASS_NAME};
                if(args.getStringArray(STUDENTS_IDS_ARGS_KEY) != null) {
                    String parameters = "";
                    String studentIdColumnName = ClassesContract.StudentEntry._ID;
                    String[] studentIds = args.getStringArray(STUDENTS_IDS_ARGS_KEY);
                    for(int i = 0; i < studentIds.length; i++){
                        if(i == studentIds.length - 1) {
                            parameters += studentIdColumnName + "!=?";
                        }
                        else
                            parameters += studentIdColumnName + "!=? AND ";
                    }
                    String selection = "(" + parameters + ")";

                    String[] selectionArgs = studentIds;
                    return new CursorLoader(getContext(),
                            ClassesContract.StudentEntry.CONTENT_URI,
                            projection,
                            selection,
                            selectionArgs,
                            null);
                }
                else
                    return new CursorLoader(getContext(),
                            ClassesContract.StudentEntry.CONTENT_URI,
                            projection,
                            null,
                            null,
                            null);

            default:
                throw new IllegalArgumentException("Invalid loader id");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        availableStudentsCursor = data;
        idIndexPairs = new Bundle();
        List<String> namesList = new ArrayList<String>();
        if (data.moveToFirst()) {
            while (!data.isAfterLast()) {
                String newName = data.getString(data.getColumnIndexOrThrow(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME));
                newName += " " + data.getString(data.getColumnIndexOrThrow(ClassesContract.StudentEntry.COLUMN_STUDENT_LAST_NAME));
                newName = data.getString(data.getColumnIndexOrThrow(ClassesContract.StudentEntry.COLUMN_MENTOR_CLASS_NAME))
                        + " - " + newName;
                namesList.add(newName);
                idIndexPairs.putInt(data.getString(data.getColumnIndexOrThrow(ClassesContract.StudentEntry._ID)), data.getPosition());
                data.moveToNext();
            }
            names = new String[namesList.size()];
            namesList.toArray(names);
            nameAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, names);
            nameAutoComplete.setAdapter(nameAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        names = null;
        availableStudentsCursor = null;
    }
}
