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
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BadBehaviorFragment.OnBadBehaviorFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BadBehaviorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BadBehaviorFragment extends Fragment {

    public static final int TALKING_OVER_TEACHER = 101;
    public static final int SPEAKING_OUT_OF_TURN = 102;
    public static final int DISRUPTING_CLASSMATES = 103;
    public static final int NOISE_MAKING = 104;
    public static final int DISTRACTIVE_BEHAVIOR = 105;
    public static final int ABUSING_CLASSMATE_VERBAL = 201;
    public static final int ABUSING_TEACHER_VERBAL = 202;
    public static final int ABUSING_STUDENT_IN_SCHOOL_VERBAL = 203;
    public static final int ABUSING_ANOTHER_TEACHER_VERBAL = 204;
    public static final int NO_COOPERATION = 301;
    public static final int MINIMAL_COOPERATION = 302;
    public static final int LITTLE_COOPERATION = 303;
    public static final int ABUSING_CLASSMATE_PHYSICAL = 401;
    public static final int ABUSING_TEACHER_PHYSICAL = 402;
    public static final int ABUSING_OTHER_PHYSICAL = 403;
    public static final int SCHOOL_PROPERTY_DAMAGE = 501;
    public static final int CLASSMATE_PROPERTY_DAMAGE = 502;
    public static final int TEACHER_PROPERTY_DAMAGE = 503;
    public static final int OWN_PROPERTY_DAMAGE = 504;
    public static final int OTHER_PROPERTY_DAMAGE = 505;
    public static final int CLASS_RULE_VIOLATION = 601;
    public static final int OTHER = 701;

    public static final String TAG = "bad_behavior_fragment";

    private static final String [] DISRUPTIVEBEHAVIOROPTIONS = {"Talking over teacher", "Speaking out of turn", "Disrupting classmates", "Noise-making", "Distractive behavior"};
    private static final String [] VERBALABUSEOPTIONS = {"Abusing classmate", "Abusing teacher", "Abusing student in school", "Abusing another teacher"};
    private static final String [] REFUSALTOWORKOPTIONS = {"No cooperation", "Minimal cooperation", "Little cooperation"};
    private static final String [] PHYSICALABUSEOPTIONS = {"Abusing classmate", "Abusing teacher", "Abusing other"};
    private static final String [] PROPERTYDAMAGEOPTIONS = {"School", "Classmate", "Teacher", "Self", "Other"};


    private OnBadBehaviorFragmentInteractionListener mListener;

    public BadBehaviorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BehaviorFragment.
     */
    public static BadBehaviorFragment newInstance(Bundle args) {
        BadBehaviorFragment fragment = new BadBehaviorFragment();
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
        View view = inflater.inflate(R.layout.fragment_bad_behavior, container, false);

        Button disruption = (Button)view.findViewById(R.id.disruptive_button);
        Button verbalAbuse = (Button)view.findViewById(R.id.verbal_abuse_button);
        Button refusingWork = (Button)view.findViewById(R.id.refusing_work_button);
        Button physicalAbuse = (Button)view.findViewById(R.id.physical_abuse_button);
        Button propertyDamage = (Button)view.findViewById(R.id.damaging_property_button);
        Button violatingRule = (Button)view.findViewById(R.id.class_rules_button);
        Button other = (Button) view.findViewById(R.id.other_infringement_button);


        disruption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, DISRUPTIVEBEHAVIOROPTIONS);
                new AlertDialog.Builder(getContext())
                        .setTitle("Choose relevant option")
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch(which){
                                    case 0:
                                        onButtonPressed(TALKING_OVER_TEACHER);
                                        break;
                                    case 1:
                                        onButtonPressed(SPEAKING_OUT_OF_TURN);
                                        break;
                                    case 2:
                                        onButtonPressed(DISRUPTING_CLASSMATES);
                                        break;
                                    case 3:
                                        onButtonPressed(NOISE_MAKING);
                                        break;
                                    case 4:
                                        onButtonPressed(DISTRACTIVE_BEHAVIOR);
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Behavior ID not found.");
                                }
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });

        verbalAbuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, VERBALABUSEOPTIONS);
                new AlertDialog.Builder(getContext())
                        .setTitle("Choose relevant option")
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch(which){
                                    case 0:
                                        onButtonPressed(ABUSING_CLASSMATE_VERBAL);
                                        break;
                                    case 1:
                                        onButtonPressed(ABUSING_TEACHER_VERBAL);
                                        break;
                                    case 2:
                                        onButtonPressed(ABUSING_STUDENT_IN_SCHOOL_VERBAL);
                                        break;
                                    case 3:
                                        onButtonPressed(ABUSING_ANOTHER_TEACHER_VERBAL);
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Behavior ID not found.");
                                }
                            }
                        }).create().show();
            }
        });

        refusingWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, REFUSALTOWORKOPTIONS);
                new AlertDialog.Builder(getContext())
                        .setTitle("Choose relevant option")
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch(which){
                                    case 0:
                                        onButtonPressed(NO_COOPERATION);
                                        break;
                                    case 1:
                                        onButtonPressed(MINIMAL_COOPERATION);
                                        break;
                                    case 2:
                                        onButtonPressed(LITTLE_COOPERATION);
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Behavior ID not found.");
                                }
                            }
                        }).create().show();
            }
        });

        physicalAbuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, PHYSICALABUSEOPTIONS);
                new AlertDialog.Builder(getContext())
                        .setTitle("Choose relevant option")
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch(which){
                                    case 0:
                                        onButtonPressed(ABUSING_CLASSMATE_PHYSICAL);
                                        break;
                                    case 1:
                                        onButtonPressed(ABUSING_TEACHER_PHYSICAL);
                                        break;
                                    case 2:
                                        onButtonPressed(ABUSING_OTHER_PHYSICAL);
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Behavior ID not found.");
                                }
                            }
                        }).create().show();
            }
        });

        propertyDamage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, PROPERTYDAMAGEOPTIONS);
                new AlertDialog.Builder(getContext())
                        .setTitle("Choose relevant option")
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch(which){
                                    case 0:
                                        onButtonPressed(SCHOOL_PROPERTY_DAMAGE);
                                        break;
                                    case 1:
                                        onButtonPressed(CLASSMATE_PROPERTY_DAMAGE);
                                        break;
                                    case 2:
                                        onButtonPressed(TEACHER_PROPERTY_DAMAGE);
                                        break;
                                    case 3:
                                        onButtonPressed(OWN_PROPERTY_DAMAGE);
                                        break;
                                    case 4:
                                        onButtonPressed(OTHER_PROPERTY_DAMAGE);
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Behavior ID not found.");
                                }
                            }
                        }).create().show();
            }
        });

        violatingRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(CLASS_RULE_VIOLATION);
            }
        });

        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(OTHER);
            }
        });

        return view;
    }

    public void onButtonPressed(int offenseId) {
        if (mListener != null) {
            mListener.onBadBehaviorFragmentInteraction(offenseId);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBadBehaviorFragmentInteractionListener) {
            mListener = (OnBadBehaviorFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
    public interface OnBadBehaviorFragmentInteractionListener {
        // TODO: Update argument type and name
        void onBadBehaviorFragmentInteraction(int offenseId);
    }
}
