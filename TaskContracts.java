package com.sachet.database2;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class TaskContracts {

    public static final String TABLE_NAME = "Tasks";

    class Columns{
        public static final String _ID = BaseColumns._ID;
        public static final String TASKS_NAME = "Name";
        public static final String TASKS_DESCRIPTION = "Description";
        public static final String TASKS_SORTORDER = "SortOrder";

        private Columns(){}
    }

    public static final Uri CONTENT_URI = Uri.withAppendedPath(AppProvider.CONTENT_AUTHORITY_URI, TABLE_NAME);

    public static Uri buildUri(long taskId){
        return ContentUris.withAppendedId(CONTENT_URI,taskId);
    }

    public static long getTaskId(Uri uri){
        return ContentUris.parseId(uri);
    }

}
