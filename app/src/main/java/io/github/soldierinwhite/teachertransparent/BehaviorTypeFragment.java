package io.github.soldierinwhite.teachertransparent;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BehaviorTypeFragment.OnBehaviorTypeFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BehaviorTypeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BehaviorTypeFragment extends Fragment {

    public static final int BAD = 0;

    public static final int ENGAGING_LESSON = 101;
    public static final int KINDNESS = 102;
    public static final int CONTRIBUTION_TO_LEARNING_ENVIRONMENT = 103;
    public static final int WORKING_GOAL_ORIENTED = 104;
    public static final int HELPFUL = 105;
    public static final int POLITENESS = 106;
    public static final int OTHER = 107;

    private static final String [] GOODBEHAVIOROPTIONS = {"Engaging lesson", "Kind", "Contributive to learning space", "Working goal oriented", "Helpful", "Polite", "Other"};

    public static final String TAG = "behavior_type_fragment";

    private OnBehaviorTypeFragmentInteractionListener mListener;

    public BehaviorTypeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BehaviorTypeFragment.
     */
    public static BehaviorTypeFragment newInstance(Bundle args) {
        BehaviorTypeFragment fragment = new BehaviorTypeFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_behavior_type, container, false);

        final ImageView goodButtonView = (ImageView) rootView.findViewById(R.id.good_button_img);
        goodButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, GOODBEHAVIOROPTIONS);
                new AlertDialog.Builder(getContext())
                        .setTitle("Choose relevant option")
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch(which){
                                    case 0:
                                        onButtonPressed(ENGAGING_LESSON);
                                        break;
                                    case 1:
                                        onButtonPressed(KINDNESS);
                                        break;
                                    case 2:
                                        onButtonPressed(CONTRIBUTION_TO_LEARNING_ENVIRONMENT);
                                        break;
                                    case 3:
                                        onButtonPressed(WORKING_GOAL_ORIENTED);
                                        break;
                                    case 4:
                                        onButtonPressed(HELPFUL);
                                        break;
                                    case 5:
                                        onButtonPressed(POLITENESS);
                                        break;
                                    case 6:
                                        onButtonPressed(OTHER);
                                        break;
                                }
                            }
                        }).create().show();
            }
        });

        ImageView badButtonView = (ImageView) rootView.findViewById(R.id.bad_button_img);
        badButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonPressed(BAD);
            }
        });

        return rootView;
    }

    public void onButtonPressed(int buttonId) {
        if (mListener != null) {
            mListener.onBehaviorTypeFragmentInteraction(buttonId);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBehaviorTypeFragmentInteractionListener) {
            mListener = (OnBehaviorTypeFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBehaviorFragmentInteractionListener");
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
    public interface OnBehaviorTypeFragmentInteractionListener {
        void onBehaviorTypeFragmentInteraction(int actionId);
    }
}
