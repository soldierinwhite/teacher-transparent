package io.github.soldierinwhite.teachertransparent.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import io.github.soldierinwhite.teachertransparent.data.ClassesContract.*;

/**
 * Created by schoo on 2017/03/07.
 */

public class ClassesDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "my_classes.db";

    public ClassesDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ENTRIES_CLASSES = "CREATE TABLE " +
                ClassEntry.TABLE_NAME + " (" +
                ClassEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ClassEntry.COLUMN_CLASS_NAME + " TEXT NOT NULL, " +
                ClassEntry.COLUMN_SUBJECT + " TEXT NOT NULL, " +
                ClassEntry.COLUMN_CLASS_MENTORS + " TEXT NOT NULL, " +
                ClassEntry.COLUMN_MENTORS_EMAIL + " TEXT NOT NULL," +
                "UNIQUE (" + ClassEntry.COLUMN_CLASS_NAME + ", " + ClassEntry.COLUMN_SUBJECT + ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_ENTRIES_CLASSES);

        String SQL_CREATE_ENTRIES_STUDENTS = "CREATE TABLE " +
                StudentEntry.TABLE_NAME + " (" +
                StudentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                StudentEntry.COLUMN_STUDENT_PERSONAL_IDENTIDICATION_NUMBER + " TEXT NOT NULL, " +
                StudentEntry.COLUMN_STUDENT_FIRST_NAME + " TEXT NOT NULL, " +
                StudentEntry.COLUMN_STUDENT_LAST_NAME + " TEXT NOT NULL, " +
                StudentEntry.COLUMN_STUDENT_PHOTO + " BLOB, " +
                StudentEntry.COLUMN_MENTOR_CLASS_NAME + " TEXT NOT NULL, " +
                StudentEntry.COLUMN_PARENT_EMAIL + " TEXT NOT NULL, " +
                StudentEntry.COLUMN_MENTOR_NAME + " TEXT NOT NULL, " +
                StudentEntry.COLUMN_MENTOR_EMAIL + " TEXT NOT NULL, " +
                StudentEntry.COLUMN_PARENT_PHONE_NUMBER + " TEXT NOT NULL, " +
                StudentEntry.COLUMN_PARENT_FIRST_NAME + " TEXT NOT NULL, " +
                StudentEntry.COLUMN_LOG_FILE_NAME + " TEXT NOT NULL," +
                "UNIQUE (" + StudentEntry.COLUMN_STUDENT_PERSONAL_IDENTIDICATION_NUMBER + ") ON CONFLICT ABORT);";

        db.execSQL(SQL_CREATE_ENTRIES_STUDENTS);

        String SQL_CREATE_ENTRIES_STUDENTS_CLASSES = "CREATE TABLE " +
                StudentsClassesEntry.TABLE_NAME + " (" +
                StudentsClassesEntry.CLASSES_ID + " INTEGER NOT NULL, " +
                StudentsClassesEntry.STUDENTS_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + StudentsClassesEntry.CLASSES_ID + ") REFERENCES " + ClassEntry.TABLE_NAME + "(" + ClassEntry._ID + ")," +
                "FOREIGN KEY (" + StudentsClassesEntry.STUDENTS_ID + ") REFERENCES " + StudentEntry.TABLE_NAME + "(" + StudentEntry._ID + ")," +
                "PRIMARY KEY (" + StudentsClassesEntry.CLASSES_ID + ", " + StudentsClassesEntry.STUDENTS_ID + "));";

        db.execSQL(SQL_CREATE_ENTRIES_STUDENTS_CLASSES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ClassEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + StudentEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + StudentsClassesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
