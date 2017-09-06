package io.github.soldierinwhite.teachertransparent.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by schoo on 2017/03/06.
 */

public final class ClassesContract {
    public static final String CONTENT_AUTHORITY = "io.github.soldierinwhite.teachertransparent";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_CLASSES = "classes";
    public static final String PATH_STUDENTS = "students";
    public static final String PATH_STUDENTS_CLASSES = "students_classes";

    public static final class ClassEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CLASSES);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CLASSES;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CLASSES;

        public static final String TABLE_NAME = "classes";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_CLASS_NAME = "class_name";
        public static final String COLUMN_SUBJECT = "subject";
        public static final String COLUMN_CLASS_MENTORS = "class_mentors";
        public static final String COLUMN_MENTORS_EMAIL = "mentors_email";

    }

    public static final class StudentEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_STUDENTS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STUDENTS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STUDENTS;

        public static final String TABLE_NAME = "students";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_STUDENT_PERSONAL_IDENTIDICATION_NUMBER = "student_personal_identification_number";
        public static final String COLUMN_STUDENT_FIRST_NAME = "student_first_name";
        public static final String COLUMN_STUDENT_LAST_NAME = "student_last_name";
        public static final String COLUMN_STUDENT_PHOTO = "student_photo";
        public static final String COLUMN_MENTOR_CLASS_NAME = "mentor_class";
        public static final String COLUMN_PARENT_EMAIL = "parent_email";
        public static final String COLUMN_MENTOR_EMAIL = "mentor_email";
        public static final String COLUMN_MENTOR_NAME = "mentor_name";
        public static final String COLUMN_PARENT_PHONE_NUMBER = "parent_phone_number";
        public static final String COLUMN_PARENT_FIRST_NAME = "parent_first_name";
        public static final String COLUMN_LOG_FILE_NAME = "log_file_name";
    }

    public static final class StudentsClassesEntry{
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_STUDENTS_CLASSES);

        public static final String TABLE_NAME = "students_classes";
        public static final String CLASSES_ID = "classes_id";
        public static final String STUDENTS_ID = "students_id";
    }

}
