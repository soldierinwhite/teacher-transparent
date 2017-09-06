package io.github.soldierinwhite.teachertransparent;

import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import io.github.soldierinwhite.teachertransparent.data.ClassesContract;

/**
 * Created by schoo on 2017/06/01.
 */

public class NewStudentEntryFragment extends Fragment{
    private OnNewStudentFragmentInteractionListener mListener;

    private EditText firstName, lastName, personalNo, parentName, parentEmail, parentPhone, mentorName, mentorEmail, mentorClass;
    private LinearLayout mentorClassLayout;
    private Button addButton, deleteButton;
    private boolean [] editTextValid = {false, false, false, false, false, false, false, false, true};

    private Cursor studentData;

    public static final String FIRST_NAME_KEY = "first_name";
    public static final String LAST_NAME_KEY = "last_name";
    public static final String PERSONAL_NO_KEY = "personal_no";
    public static final String PARENT_NAME_KEY = "parent_name";
    public static final String PARENT_EMAIL_KEY = "parent_email";
    public static final String PARENT_PHONE_KEY = "parent_phone";
    public static final String MENTOR_NAME_KEY = "mentor_name";
    public static final String MENTOR_EMAIL_KEY = "mentor_email";
    public static final String MENTOR_CLASS_KEY = "mentor_class";
    public static final String DELETE_ISSUE_KEY = "delete_issued";


    private final int PERSONAL_NUMBER_LENGTH = 10;


    public NewStudentEntryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters
     * @return A new instance of fragment BehaviorTypeFragment.
     */
    public static NewStudentEntryFragment newInstance() {
        NewStudentEntryFragment fragment = new NewStudentEntryFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_new_student_entry, container, false);
        rootView.setBackgroundColor(Color.parseColor("#EEEEEE"));


        addButton = (Button) rootView.findViewById(R.id.add_student_button);
        addButton.setEnabled(false);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString(FIRST_NAME_KEY, firstName.getText().toString().trim());
                args.putString(LAST_NAME_KEY, lastName.getText().toString().trim());
                args.putString(PERSONAL_NO_KEY, personalNo.getText().toString());
                args.putString(PARENT_NAME_KEY, parentName.getText().toString().trim());
                args.putString(PARENT_PHONE_KEY, parentPhone.getText().toString().trim());
                args.putString(PARENT_EMAIL_KEY, parentEmail.getText().toString().trim());
                args.putString(MENTOR_NAME_KEY, mentorName.getText().toString().trim());
                args.putString(MENTOR_EMAIL_KEY, mentorEmail.getText().toString().trim());
                if(mentorClassLayout.getVisibility() == View.VISIBLE){
                    args.putString(MENTOR_CLASS_KEY, mentorClass.getText().toString().trim());
                    args.putLong(ClassesContract.StudentEntry._ID, getArguments().getLong(ClassesContract.StudentEntry._ID));
                }
                onButtonPressed(args);
            }
        });

        deleteButton = (Button) rootView.findViewById(R.id.delete_student_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });

        mentorClassLayout = (LinearLayout) rootView.findViewById(R.id.change_mentor_class_layout);
        mentorClass = (EditText) rootView.findViewById(R.id.change_mentor_class_name_edit_text);
        mentorClass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(mentorClass.getText().toString().trim().length() == 0){
                    mentorClass.setError("Mentor class missing!");
                    editTextValid[8] = false;
                }
                else
                    editTextValid[8] = true;
                checkEditTextValidity();
            }
        });
        mentorClassLayout.setVisibility(View.GONE);

        firstName = (EditText) rootView.findViewById(R.id.first_name_edit_text);
        firstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(firstName.getText().toString().trim().length() == 0) {
                    firstName.setError("First name missing!");
                    editTextValid[0] = false;
                }
                else
                    editTextValid[0] = true;
                checkEditTextValidity();
            }
        });


        lastName = (EditText) rootView.findViewById(R.id.last_name_edit_text);
        lastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(lastName.getText().toString().trim().length() == 0){
                    lastName.setError("Last name missing!");
                    editTextValid[1] = false;
                }
                else
                    editTextValid[1] = true;
                checkEditTextValidity();
            }
        });

        personalNo = (EditText) rootView.findViewById(R.id.personal_no_edit_text);
        personalNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(personalNo.getText().toString().trim().length() != PERSONAL_NUMBER_LENGTH ||  !TextUtils.isDigitsOnly(personalNo.getText().toString())){
                    personalNo.setError(PERSONAL_NUMBER_LENGTH + " digits needed");
                    editTextValid[2] = false;
                }
                else
                    editTextValid[2] = true;
                checkEditTextValidity();
            }
        });

        parentName = (EditText) rootView.findViewById(R.id.parent_name_edit_text);
        parentName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(parentName.getText().toString().trim().length() == 0) {
                    parentName.setError("Parent name missing!");
                    editTextValid[3] = false;
                }
                else
                    editTextValid[3] = true;
                checkEditTextValidity();
            }
        });

        parentEmail = (EditText) rootView.findViewById(R.id.parent_email_edit_text);
        parentEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!Patterns.EMAIL_ADDRESS.matcher(parentEmail.getText().toString().trim()).matches()){
                    parentEmail.setError("Invalid email!");
                    editTextValid[4] = false;
                }
                else
                    editTextValid[4] = true;
                checkEditTextValidity();
            }
        });

        parentPhone = (EditText) rootView.findViewById(R.id.parent_phone_edit_text);
        parentPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!Patterns.PHONE.matcher(parentPhone.getText().toString().trim()).matches()){
                    parentPhone.setError("Invalid phone number!");
                    editTextValid[5] = false;
                }
                else
                    editTextValid[5] = true;
                checkEditTextValidity();
            }
        });

        mentorName = (EditText) rootView.findViewById(R.id.mentor_name_edit_text);
        mentorName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(mentorName.getText().toString().trim().length() == 0) {
                    mentorName.setError("Mentor name missing!");
                    editTextValid[6] = false;
                }
                else
                    editTextValid[6] = true;
                checkEditTextValidity();
            }
        });

        mentorEmail = (EditText) rootView.findViewById(R.id.mentor_email_edit_text);
        mentorEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!Patterns.EMAIL_ADDRESS.matcher(mentorEmail.getText().toString().trim()).matches()){
                    mentorEmail.setError("Invalid email!");
                    editTextValid[7] = false;
                }
                else
                    editTextValid[7] = true;
                checkEditTextValidity();
            }
        });

        if(getArguments() != null){
            Bundle args = getArguments();
            firstName.setText(args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME));
            lastName.setText(args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_LAST_NAME));
            personalNo.setText(args.getString(ClassesContract.StudentEntry.COLUMN_STUDENT_PERSONAL_IDENTIDICATION_NUMBER));
            parentName.setText(args.getString(ClassesContract.StudentEntry.COLUMN_PARENT_FIRST_NAME));
            parentEmail.setText(args.getString(ClassesContract.StudentEntry.COLUMN_PARENT_EMAIL));
            parentPhone.setText(args.getString(ClassesContract.StudentEntry.COLUMN_PARENT_PHONE_NUMBER));
            mentorName.setText(args.getString(ClassesContract.StudentEntry.COLUMN_MENTOR_NAME));
            mentorEmail.setText(args.getString(ClassesContract.StudentEntry.COLUMN_MENTOR_EMAIL));

            addButton.setText("Save");
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) addButton.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            addButton.setLayoutParams(params);

            deleteButton.setVisibility(View.VISIBLE);

            mentorClassLayout.setVisibility(View.VISIBLE);
            mentorClass.setText(args.getString(ClassesContract.StudentEntry.COLUMN_MENTOR_CLASS_NAME));

            personalNo.setEnabled(false);
        }

        return rootView;
    }

    public void onButtonPressed(Bundle args) {
        if (mListener != null) {
            mListener.onNewStudentEntryFragmentInteraction(args);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNewStudentFragmentInteractionListener) {
            mListener = (OnNewStudentFragmentInteractionListener) context;
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

    private void checkEditTextValidity(){
        boolean allValid = true;
        int editTextCounter = 0;
        while(allValid && editTextCounter < editTextValid.length){
            if(!editTextValid[editTextCounter])
                allValid = false;
            editTextCounter++;
        }

        if(allValid){
            addButton.setEnabled(true);
        }
        else
            addButton.setEnabled(false);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Delete all of this student's info and behavior logs?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteStudent();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteStudent(){

        long id = getArguments().getLong(ClassesContract.StudentEntry._ID);
        Uri deleteUri = ContentUris.withAppendedId(ClassesContract.StudentEntry.CONTENT_URI, id);
        String selection = ClassesContract.StudentEntry._ID + "=?";
        String [] selectionArgs = {String.valueOf(id)};
        int rowsDeleted = getContext().getContentResolver().delete(deleteUri, selection, selectionArgs);

        if(rowsDeleted != 0){
            getContext().deleteFile(String.valueOf(getArguments().getString(ClassesContract.StudentEntry.COLUMN_STUDENT_PERSONAL_IDENTIDICATION_NUMBER)));
            Toast.makeText(getContext(), "Student deleted", Toast.LENGTH_SHORT).show();
            Bundle args = new Bundle();
            args.putString(DELETE_ISSUE_KEY, "Deleted");
            onButtonPressed(args);
        } else {
            Toast.makeText(getContext(), "Error deleting student", Toast.LENGTH_SHORT).show();
        }
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
    public interface OnNewStudentFragmentInteractionListener {
        void onNewStudentEntryFragmentInteraction(Bundle data);
    }
}
