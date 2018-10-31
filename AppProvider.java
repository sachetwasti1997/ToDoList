package com.sachet.database2;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;


public class AppProvider extends ContentProvider{
    private static final String TAG = "AppProvider";

    private static AppDatabase mOpenHelper = null;

    private static UriMatcher sUriMatcher = buildUri();

    public static final String CONTENT_AUTHORITY = "com.sachet.database2.provider";
    public static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    private static final int TASK = 100;
    private static final int TASK_ID = 101;

    private static UriMatcher buildUri(){
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //eg content://com.sachet.databases.provider/Tasks
        uriMatcher.addURI(CONTENT_AUTHORITY,TaskContracts.TABLE_NAME, TASK);
        //eg content://com.sachet.databases.provider/Tasks/8 -- if we have something like this as the uri
        uriMatcher.addURI(CONTENT_AUTHORITY,TaskContracts.TABLE_NAME+"/#", TASK_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = AppDatabase.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.d(TAG, "query: method called with Uri "+uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "query: the match is "+match);

        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        String selectionQuery = null;

        switch (match){
            case TASK:
                sqLiteQueryBuilder.setTables(TaskContracts.TABLE_NAME);
                break;
            case TASK_ID:
                sqLiteQueryBuilder.setTables(TaskContracts.TABLE_NAME);
                long id = TaskContracts.getTaskId(uri);
                selectionQuery = TaskContracts.Columns._ID+" = "+id;

//                if(selection != null && selection.length()>0){
//                    selectionQuery += selection;
//                }
                sqLiteQueryBuilder.appendWhere(selectionQuery);

                break;
            default:
                throw new IllegalArgumentException("Invalid match parameter "+match);
        }
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
//        return sqLiteQueryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        Cursor cursor = sqLiteQueryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        Log.d(TAG, "query: number of rows in returned cursor "+cursor.getCount());

        cursor.setNotificationUri(getContext().getContentResolver(), uri);//register to watch content provider for changes
        //sets notification to any listener that have registered and the cursorloader are automatically registered
        /**
         * next we make sure that all those methods that can make changes to the database call the content resolver
         * notify change method to notify the listeners that there have been a change
         */
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.d(TAG, "insert: starts with Uri "+uri.toString());
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "insert: the match code is "+match);
        SQLiteDatabase db;
        long uri1;
        Uri return1;
        switch (match){
            case TASK:
                db = mOpenHelper.getWritableDatabase();
                uri1 = db.insert(TaskContracts.TABLE_NAME, null, values);
                if(uri1 > 0){
                    return1 = TaskContracts.buildUri(uri1);
                }else{
                    throw new android.database.SQLException("Failed to insert into the Uri "+uri.toString());
                }
                break;

            default:
                throw new IllegalArgumentException("Insert called with unknown arguments");
        }

        if(uri1 > 0){
            Log.d(TAG, "insert: Setting notify changed with "+uri);
            getContext().getContentResolver().notifyChange(uri, null);
        }else{
            Log.d(TAG, "insert: nothing inserted");
        }
        Log.d(TAG, "insert: returning "+return1.toString());
        return return1;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "delete: starts with uri "+uri.toString());
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "delete: match code is "+uri);

        int count;
        SQLiteDatabase db;

        switch(match){
            case TASK:
                db = mOpenHelper.getWritableDatabase();
                count = db.delete(TaskContracts.TABLE_NAME, selection, selectionArgs);
                break;
            case TASK_ID:
                db = mOpenHelper.getWritableDatabase();
                long id = TaskContracts.getTaskId(uri);
                String selectionCriteria = TaskContracts.Columns._ID+" = "+ id;

//                if(selection!=null||selection.length()>0){
//                    selectionCriteria += "AND ("+selection+");";
//                }

                count = db.delete(TaskContracts.TABLE_NAME, selectionCriteria, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown Uri "+uri.toString());
        }

        if(count > 0){
            Log.d(TAG, "delete: Setting notify Change with "+uri);
            getContext().getContentResolver().notifyChange(uri, null);
        }else{
            Log.d(TAG, "delete: nothing was deleted");
        }
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "update: starts with uri "+uri.toString());
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "update: match code is "+match);

        int count;
        SQLiteDatabase db;
        String selectionCriteria;

        switch (match){
            case TASK:
                db = mOpenHelper.getWritableDatabase();
                Log.d(TAG, "update: selected "+selection);
                count = db.update(TaskContracts.TABLE_NAME, values, selection, selectionArgs);
                break;
            case TASK_ID:
                db = mOpenHelper.getWritableDatabase();
                long taskId = TaskContracts.getTaskId(uri);
                selectionCriteria = TaskContracts.Columns._ID+" = "+taskId;
//
//                if(selection!=null&& selection.length()>0){
//                    selectionCriteria += "AND ("+selection+");";
//                }

                count = db.update(TaskContracts.TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Called with wrong uri "+uri.toString());
        }
        Log.d(TAG, "update: returning "+count);
        if(count > 0){
            Log.d(TAG, "update: Setting notify Change with "+uri);
            getContext().getContentResolver().notifyChange(uri, null);
        }else{
            Log.d(TAG, "update: nothing was deleted");
        }
        return count;
    }
}
//The built in cursorLoader class takes care of registering itself as a listener with the content resolver that it queries

/**
 * so now when theres a change to the database the cursorloader will receive notification that the data has changed and it will trigger
 * a call in our case to th mainActivityFragments onLoadFinished method
 */