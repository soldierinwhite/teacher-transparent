package io.github.soldierinwhite.teachertransparent.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

/**
 * Created by schoo on 2017/03/07.
 */

public class ClassesProvider extends ContentProvider {
    public static final String LOG_TAG = ClassesProvider.class.getSimpleName();

    public static final String PERSONAL_NUMBER_CLASH = "personal_number_not_unique";

    //JOIN paths
    private static final String PATH_STUDENTS_JOIN_CLASS_GET_STUDENTS = "students_join_class_get_students";
    private static final String PATH_DISTINCT_MENTOR_CLASSES = "distinct_mentor_classes";


    public static final Uri CONTENT_URI_STUDENTS_JOIN_CLASS_GET_STUDENTS = Uri.withAppendedPath(
            ClassesContract.BASE_CONTENT_URI, PATH_STUDENTS_JOIN_CLASS_GET_STUDENTS);
    public static final Uri CONTENT_URI_DISTINCT_MENTOR_CLASSES = Uri.withAppendedPath(ClassesContract.BASE_CONTENT_URI, PATH_DISTINCT_MENTOR_CLASSES);

    public static final int CODE_CLASSES = 100;
    public static final int CODE_CLASSES_WITH_ID = 101;

    public static final int CODE_STUDENTS = 200;
    public static final int CODE_STUDENTS_WITH_ID = 201;

    public static final int CODE_STUDENTS_CLASSES = 300;
    public static final int CODE_STUDENTS_CLASSES_WITH_ID = 301;

    //customized query codes
    public static final int CODE_STUDENTS_JOIN_CLASS_GET_STUDENTS_WITH_ID = 401;
    public static final int CODE_DISTINCT_MENTOR_CLASSES = 402;

    /*
* The URI Matcher used by this content provider.
*/
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private ClassesDbHelper mOpenHelper;

    public static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ClassesContract.CONTENT_AUTHORITY;

        /* This URI is content://io.github.soldierinwhite.teachertransparent/classes/ */
        matcher.addURI(authority, ClassesContract.PATH_CLASSES, CODE_CLASSES);

        /* This URI is content://io.github.soldierinwhite.teachertransparent/classes/# */
        matcher.addURI(authority, ClassesContract.PATH_CLASSES + "/#", CODE_CLASSES_WITH_ID);

        /* This URI is content://io.github.soldierinwhite.teachertransparent/students/ */
        matcher.addURI(authority, ClassesContract.PATH_STUDENTS, CODE_STUDENTS);

        /* This URI is content://io.github.soldierinwhite.teachertransparent/students/#/ */
        matcher.addURI(authority, ClassesContract.PATH_STUDENTS + "/#", CODE_STUDENTS_WITH_ID);

        /* This URI is content://io.github.soldierinwhite.teachertransparent/students_classes/ */
        matcher.addURI(authority, ClassesContract.PATH_STUDENTS_CLASSES, CODE_STUDENTS_CLASSES);

        /* This URI is content://io.github.soldierinwhite.teachertransparent/students_classes/#/ */
        matcher.addURI(authority, ClassesContract.PATH_STUDENTS_CLASSES + "/#", CODE_STUDENTS_CLASSES_WITH_ID);

        /* This URI is content://io.github.soldierinwhite.teachertransparent/students_join_class_get_students/#/ */
        matcher.addURI(authority, PATH_STUDENTS_JOIN_CLASS_GET_STUDENTS + "/#", CODE_STUDENTS_JOIN_CLASS_GET_STUDENTS_WITH_ID);

        /*  This URI is content://io.github.soldierinwhite.teachertransparent/distinct_mentor_classes/ */
        matcher.addURI(authority, PATH_DISTINCT_MENTOR_CLASSES, CODE_DISTINCT_MENTOR_CLASSES);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new ClassesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        Cursor cursor;

        final int match = sUriMatcher.match(uri);

        switch(match){
            case CODE_CLASSES:
                cursor = db.query(ClassesContract.ClassEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_CLASSES_WITH_ID:
                selection = ClassesContract.ClassEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ClassesContract.ClassEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_STUDENTS:
                cursor = db.query(ClassesContract.StudentEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_STUDENTS_WITH_ID:
                selection = ClassesContract.StudentEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ClassesContract.StudentEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_STUDENTS_JOIN_CLASS_GET_STUDENTS_WITH_ID:
                String [] args = {String.valueOf(ContentUris.parseId(uri))};
                String queryInnerJoinSql = "SELECT * FROM " + ClassesContract.StudentEntry.TABLE_NAME +
                        " INNER JOIN " + ClassesContract.StudentsClassesEntry.TABLE_NAME +
                        " ON " + ClassesContract.StudentsClassesEntry.TABLE_NAME + "." + ClassesContract.StudentsClassesEntry.STUDENTS_ID
                        + "=" + ClassesContract.StudentEntry.TABLE_NAME + "." + ClassesContract.StudentEntry._ID
                        + " WHERE " + ClassesContract.StudentsClassesEntry.TABLE_NAME + "." + ClassesContract.StudentsClassesEntry.CLASSES_ID + "=? ORDER BY " +
                        sortOrder;
                cursor = db.rawQuery(queryInnerJoinSql, args);
                break;
            case CODE_DISTINCT_MENTOR_CLASSES:
                String queryDistinctSql = "SELECT * FROM " + ClassesContract.StudentEntry.TABLE_NAME +
                        " WHERE " + ClassesContract.StudentEntry._ID + " IN (SELECT MIN(" + ClassesContract.StudentEntry._ID + ") FROM " + ClassesContract.StudentEntry.TABLE_NAME + " GROUP BY " +
                        ClassesContract.StudentEntry.COLUMN_MENTOR_CLASS_NAME + ") ORDER BY " + ClassesContract.StudentEntry.COLUMN_MENTOR_CLASS_NAME;
                cursor = db.rawQuery(queryDistinctSql, null);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        //Set notification URI on the Cursor,
        //so we know what content URI the Cursor was created for
        //If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case CODE_CLASSES:
                return ClassesContract.ClassEntry.CONTENT_LIST_TYPE;
            case CODE_CLASSES_WITH_ID:
                return ClassesContract.ClassEntry.CONTENT_ITEM_TYPE;
            case CODE_STUDENTS:
                return ClassesContract.StudentEntry.CONTENT_LIST_TYPE;
            case CODE_STUDENTS_WITH_ID:
                return ClassesContract.StudentEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch(match){
            case CODE_CLASSES:
                return insertClass(uri, contentValues);
            case CODE_STUDENTS:
                return insertStudent(uri, contentValues);
            case CODE_STUDENTS_CLASSES:
                SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                long id = db.insert(ClassesContract.StudentsClassesEntry.TABLE_NAME, null, contentValues);
                getContext().getContentResolver().notifyChange(ContentUris.withAppendedId(CONTENT_URI_STUDENTS_JOIN_CLASS_GET_STUDENTS, contentValues.getAsLong(ClassesContract.StudentsClassesEntry.CLASSES_ID)), null);
                return ContentUris.withAppendedId(uri, id);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch(match){
            case CODE_STUDENTS_CLASSES:
                db.beginTransaction();
                int rowsInserted = 0;
                try{
                    for (ContentValues value : values){
                        long id = db.insert(ClassesContract.StudentsClassesEntry.TABLE_NAME, null, value);
                        if(id != -1){
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if(rowsInserted > 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    private Uri insertClass(Uri uri, ContentValues contentValues){
        String name = contentValues.getAsString(ClassesContract.ClassEntry.COLUMN_CLASS_NAME);
        if(name == null || name.isEmpty()){
            throw new IllegalArgumentException("Class requires a name.");
        }

        String subject = contentValues.getAsString(ClassesContract.ClassEntry.COLUMN_SUBJECT);
        if(subject == null || subject.isEmpty()){
            throw new IllegalArgumentException("Subject is required for class.");
        }

        String classMentors = contentValues.getAsString(ClassesContract.ClassEntry.COLUMN_CLASS_MENTORS);
        if(classMentors == null || classMentors.isEmpty()){
            throw new IllegalArgumentException("Class mentors required.");
        }

        String mentorEmails = contentValues.getAsString(ClassesContract.ClassEntry.COLUMN_MENTORS_EMAIL);
        if(mentorEmails == null || mentorEmails.isEmpty()){
            throw new IllegalArgumentException("Mentor emails required.");
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        long id = db.insert(ClassesContract.ClassEntry.TABLE_NAME, null, contentValues);
        if(id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertStudent(Uri uri, ContentValues contentValues){
        String studentName = contentValues.getAsString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME);
        if(studentName == null || studentName.isEmpty()){
            throw new IllegalArgumentException("Student requires a first name.");
        }

        String studentSurname = contentValues.getAsString(ClassesContract.StudentEntry.COLUMN_STUDENT_LAST_NAME);
        if(studentSurname == null || studentSurname.isEmpty()){
            throw new IllegalArgumentException("Student requires a last name.");
        }

        String personalNumber = contentValues.getAsString(ClassesContract.StudentEntry.COLUMN_STUDENT_PERSONAL_IDENTIDICATION_NUMBER);
        if(personalNumber.length() != 10 || !TextUtils.isDigitsOnly(personalNumber)){
            throw new IllegalArgumentException("Invalid student personal number.");
        }

        String parentName = contentValues.getAsString(ClassesContract.StudentEntry.COLUMN_PARENT_FIRST_NAME);
        if(parentName == null || parentName.isEmpty()){
            throw new IllegalArgumentException("Parent requires a name.");
        }

        String parentEmail = contentValues.getAsString(ClassesContract.StudentEntry.COLUMN_PARENT_EMAIL);
        if(!Patterns.EMAIL_ADDRESS.matcher(parentEmail).matches()){
            throw new IllegalArgumentException("Invalid parent email.");
        }

        String parentPhone = contentValues.getAsString(ClassesContract.StudentEntry.COLUMN_PARENT_PHONE_NUMBER);
        if(!Patterns.PHONE.matcher(parentPhone).matches()){
            throw new IllegalArgumentException("Invalid parent phone.");
        }

        String mentorName = contentValues.getAsString(ClassesContract.StudentEntry.COLUMN_MENTOR_NAME);
        if(mentorName == null || mentorName.isEmpty()){
            throw new IllegalArgumentException("Mentor requires a name.");
        }

        String mentorEmail = contentValues.getAsString(ClassesContract.StudentEntry.COLUMN_MENTOR_EMAIL);
        if(!Patterns.EMAIL_ADDRESS.matcher(mentorEmail).matches()){
            throw new IllegalArgumentException("Invalid mentor email.");
        }

        String mentorClass = contentValues.getAsString(ClassesContract.StudentEntry.COLUMN_MENTOR_CLASS_NAME);
        if(mentorClass == null || mentorClass.isEmpty()){
            throw new IllegalArgumentException("Invalid mentor class.");
        }

        String logFileName = contentValues.getAsString(ClassesContract.StudentEntry.COLUMN_LOG_FILE_NAME);
        if(logFileName == null || logFileName.isEmpty()){
            throw new IllegalArgumentException("Invalid mentor class.");
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long id;
        try {
            id = db.insertOrThrow(ClassesContract.StudentEntry.TABLE_NAME, null, contentValues);
        } catch (SQLiteConstraintException e){
            id = -2;
        }
        if(id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        } else if(id == -2){
            return Uri.withAppendedPath(ClassesContract.StudentEntry.CONTENT_URI, PERSONAL_NUMBER_CLASH);
        }


        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        int rowsDeleted = 0;
        switch (match){
            case CODE_STUDENTS_WITH_ID:
                selection = ClassesContract.StudentEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                rowsDeleted = db.delete(ClassesContract.StudentEntry.TABLE_NAME, selection, selectionArgs);
                if(rowsDeleted != 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case CODE_CLASSES_WITH_ID:
                selection = ClassesContract.ClassEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateClass(uri, contentValues, selection, selectionArgs);
            case CODE_STUDENTS_WITH_ID:
                selection = ClassesContract.StudentEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateStudent(uri, contentValues, selection, selectionArgs);
        }
        return 0;
    }

    private int updateClass(Uri uri, ContentValues cv, String selection, String[] selectionArgs){
        if(cv.containsKey(ClassesContract.ClassEntry.COLUMN_CLASS_NAME)){
            String name = cv.getAsString(ClassesContract.ClassEntry.COLUMN_CLASS_NAME);
            if(name == null || name.isEmpty()){
                throw new IllegalArgumentException("Class requires a name");
            }
        }

        if(cv.containsKey(ClassesContract.ClassEntry.COLUMN_SUBJECT)){
            String subject = cv.getAsString(ClassesContract.ClassEntry.COLUMN_SUBJECT);
            if(subject == null || subject.isEmpty()){
                throw new IllegalArgumentException("Class requires a subject");
            }
        }

        if (cv.containsKey(ClassesContract.ClassEntry.COLUMN_CLASS_MENTORS)){
            String mentorNames = cv.getAsString(ClassesContract.ClassEntry.COLUMN_CLASS_MENTORS);
            if(mentorNames == null || mentorNames.isEmpty()){
                throw new IllegalArgumentException("Class requires mentors");
            }
        }

        if (cv.containsKey(ClassesContract.ClassEntry.COLUMN_MENTORS_EMAIL)){
            String mentorEmails = cv.getAsString(ClassesContract.ClassEntry.COLUMN_MENTORS_EMAIL);
            if(mentorEmails == null || mentorEmails.isEmpty()){
                throw new IllegalArgumentException("Class requires mentors' emails");
            }
        }

        if(cv.size() == 0){
            return 0;
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int rowsUpdated = db.update(ClassesContract.ClassEntry.TABLE_NAME, cv, selection, selectionArgs);
        if(rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    private int updateStudent(Uri uri, ContentValues cv, String selection, String[] selectionArgs){
        if(cv.containsKey(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME)){
            String firstName = cv.getAsString(ClassesContract.StudentEntry.COLUMN_STUDENT_FIRST_NAME);
            if(firstName == null || firstName.isEmpty()){
                throw new IllegalArgumentException("Student requires first name");
            }
        }

        if(cv.containsKey(ClassesContract.StudentEntry.COLUMN_STUDENT_LAST_NAME)){
            String firstName = cv.getAsString(ClassesContract.StudentEntry.COLUMN_STUDENT_LAST_NAME);
            if(firstName == null || firstName.isEmpty()){
                throw new IllegalArgumentException("Student requires last name");
            }
        }

        if(cv.containsKey(ClassesContract.StudentEntry.COLUMN_STUDENT_PERSONAL_IDENTIDICATION_NUMBER)){
            throw new IllegalArgumentException("Cannot update personal number; jeopardizes dependencies. Recreate entry instead.");
        }

        if(cv.containsKey(ClassesContract.StudentEntry.COLUMN_PARENT_FIRST_NAME)){
            String parentName = cv.getAsString(ClassesContract.StudentEntry.COLUMN_PARENT_FIRST_NAME);
            if(parentName == null || parentName.isEmpty()){
                throw new IllegalArgumentException("Student needs guardian name.");
            }
        }

        if(cv.containsKey(ClassesContract.StudentEntry.COLUMN_PARENT_EMAIL)){
            String parentEmail = cv.getAsString(ClassesContract.StudentEntry.COLUMN_PARENT_EMAIL);
            if(!Patterns.EMAIL_ADDRESS.matcher(parentEmail).matches()){
                throw new IllegalArgumentException("Invalid guardian email");
            }
        }

        if(cv.containsKey(ClassesContract.StudentEntry.COLUMN_PARENT_PHONE_NUMBER)){
            String parentPhone = cv.getAsString(ClassesContract.StudentEntry.COLUMN_PARENT_PHONE_NUMBER);
            if(!Patterns.PHONE.matcher(parentPhone).matches()){
                throw new IllegalArgumentException("Invalid guardian phone number");
            }
        }

        if(cv.containsKey(ClassesContract.StudentEntry.COLUMN_MENTOR_CLASS_NAME)){
            String mentorClass = cv.getAsString(ClassesContract.StudentEntry.COLUMN_MENTOR_CLASS_NAME);
            if(mentorClass == null || mentorClass.isEmpty()){
                throw new IllegalArgumentException("Student needs mentor class");
            }
        }

        if(cv.containsKey(ClassesContract.StudentEntry.COLUMN_MENTOR_NAME)){
            String mentorName = cv.getAsString(ClassesContract.StudentEntry.COLUMN_MENTOR_NAME);
            if(mentorName == null || mentorName.isEmpty()){
                throw new IllegalArgumentException("Student needs mentor's name");
            }
        }

        if(cv.containsKey(ClassesContract.StudentEntry.COLUMN_MENTOR_EMAIL)){
            String parentEmail = cv.getAsString(ClassesContract.StudentEntry.COLUMN_MENTOR_EMAIL);
            if(!Patterns.EMAIL_ADDRESS.matcher(parentEmail).matches()){
                throw new IllegalArgumentException("Invalid mentor email");
            }
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int rowsUpdated = db.update(ClassesContract.StudentEntry.TABLE_NAME, cv, selection, selectionArgs);
        if(rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
