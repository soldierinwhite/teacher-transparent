package io.github.soldierinwhite.teachertransparent.util;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import java.util.ArrayList;

import io.github.soldierinwhite.teachertransparent.Mentor;
import io.github.soldierinwhite.teachertransparent.data.ClassesContract;

/**
 * Created by schoo on 2017/03/14.
 */

public class DataUtils {
    private DataUtils() {
    }

    public static String insertClass(Context context, String className, String subject, ArrayList<Mentor> mentors) {
        Uri classesUri = ClassesContract.ClassEntry.CONTENT_URI;
        ContentValues cv = new ContentValues();
        cv.put(ClassesContract.ClassEntry.COLUMN_CLASS_NAME, className);
        cv.put(ClassesContract.ClassEntry.COLUMN_SUBJECT, subject);
        String mentorNames = mentorNamesToString(mentors);
        String mentorEmails = mentorEmailsToString(mentors);
        cv.put(ClassesContract.ClassEntry.COLUMN_CLASS_MENTORS, mentorNames);
        cv.put(ClassesContract.ClassEntry.COLUMN_MENTORS_EMAIL, mentorEmails);


        Uri result = context.getContentResolver().insert(classesUri, cv);
        if (result != null) {
            return "Class added";
        } else {
            return "Insertion failed: please try again";
        }
    }

    public static String updateClass(Context context, Uri updateUri, String className, String subject, ArrayList<Mentor> mentors){
        ContentValues cv = new ContentValues();
        cv.put(ClassesContract.ClassEntry.COLUMN_CLASS_NAME, className);
        cv.put(ClassesContract.ClassEntry.COLUMN_SUBJECT, subject);
        String mentorNames = mentorNamesToString(mentors);
        String mentorEmails = mentorEmailsToString(mentors);
        cv.put(ClassesContract.ClassEntry.COLUMN_CLASS_MENTORS, mentorNames);
        cv.put(ClassesContract.ClassEntry.COLUMN_MENTORS_EMAIL, mentorEmails);
        String selection = ClassesContract.ClassEntry._ID + "=?";
        String [] selectionArgs = new String[]{String.valueOf(ContentUris.parseId(updateUri))};

        int rowsUpdated = context.getContentResolver().update(updateUri, cv, selection, selectionArgs);
        if(rowsUpdated != 0){
            return "Update successful";
        }
        else
            return "Update failed";
    }

    public static ArrayList<Mentor> mentorArrayListFromCursorString (String mentorNames, String mentorEmails){
        ArrayList<Mentor> mentors = new ArrayList<>();

        String currentMentorName, currentMentorEmail;
        Mentor currentMentor;
        while(mentorNames.indexOf(';') != -1 && mentorEmails.indexOf(';') != -1){
            currentMentorName = mentorNames.substring(0, mentorNames.indexOf(';'));
            mentorNames = mentorNames.substring(mentorNames.indexOf(';')+1);
            currentMentorEmail = mentorEmails.substring(0, mentorEmails.indexOf(';'));
            mentorEmails = mentorEmails.substring(mentorEmails.indexOf(';')+1);
            currentMentor = new Mentor(currentMentorName, currentMentorEmail);
            mentors.add(currentMentor);
        }

        return mentors;
    }

    public static String[] studentIdToArray(String studentIdString){
        ArrayList<String> studentIdList = new ArrayList<>();
        while(studentIdString.indexOf(';') != -1){
            studentIdList.add(studentIdString.substring(0, studentIdString.indexOf(';')));
            studentIdString = studentIdString.substring(studentIdString.indexOf(';') + 1);
        }
        String[] studentIdArray = new String[studentIdList.size()];
        for(int i = 0; i < studentIdArray.length; i++){
            studentIdArray[i] = studentIdList.get(i);
        }
        return studentIdArray;
    }

    private static String mentorNamesToString(ArrayList<Mentor> mentors){
        String mentorNames = "";
        for (int i = 0; i < mentors.size(); i++) {
            mentorNames = mentorNames + mentors.get(i).getMentorName() + ";";
        }
        return mentorNames;
    }

    private static String mentorEmailsToString(ArrayList<Mentor> mentors){
        String mentorEmails = "";
        for(int i = 0; i < mentors.size(); i++){
            mentorEmails = mentorEmails + mentors.get(i).getMentorEmail() + ";";
        }
        return mentorEmails;
    }
}
