package io.github.soldierinwhite.teachertransparent;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewMentorClassFragment.OnNewMentorClassFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewMentorClassFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewMentorClassFragment extends Fragment{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String MENTOR_CLASS_NAME_KEY = "mentor_class_name";

    public static final String EXISTING_MENTOR_CLASS_NAMES_KEY = "existing_mentor_names";

    private EditText mentorClassName;
    private Button addMentorClassName;

    private ArrayList<String> existingMentorNames;

    private OnNewMentorClassFragmentInteractionListener mListener;

    public NewMentorClassFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment NewMentorClassFragment.
     */
    public static NewMentorClassFragment newInstance(Bundle args) {
        NewMentorClassFragment fragment = new NewMentorClassFragment();
        fragment.setArguments(args);
        fragment.existingMentorNames = (ArrayList<String>) fragment.getArguments().getSerializable(EXISTING_MENTOR_CLASS_NAMES_KEY);
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
        final View rootView = inflater.inflate(R.layout.fragment_new_mentor_class, container, false);
        rootView.setBackgroundColor(Color.parseColor("#EEEEEE"));

        addMentorClassName = (Button) rootView.findViewById(R.id.proceed_new_mentor_class_button);
        addMentorClassName.setEnabled(false);

        mentorClassName = (EditText) rootView.findViewById(R.id.mentor_class_name_edit_text);
        mentorClassName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String mentorClassInEditText = mentorClassName.getText().toString().trim();
                boolean isCopy = false;
                int looper = 0;
                while(looper < existingMentorNames.size() && !isCopy){
                    if(existingMentorNames.get(looper).equalsIgnoreCase(mentorClassInEditText)){
                        isCopy = true;
                        Toast.makeText(getContext(), "Mentor class " + mentorClassInEditText + " already exists.", Toast.LENGTH_SHORT).show();
                    }
                    looper++;
                }
                if(!isCopy) {
                    addMentorClassName.setEnabled(true);
                }
                else
                    addMentorClassName.setEnabled(false);
            }
        });

        addMentorClassName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonPressed(mentorClassName.getText().toString().trim());
            }
        });

        return rootView;
    }


    public void onButtonPressed(String newMentorClassName) {
        if (mListener != null) {
            mListener.onNewMentorClassFragmentInteraction(newMentorClassName);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNewMentorClassFragmentInteractionListener) {
            mListener = (OnNewMentorClassFragmentInteractionListener) context;
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
    public interface OnNewMentorClassFragmentInteractionListener {
        void onNewMentorClassFragmentInteraction(String newMentorClassName);
    }
}
