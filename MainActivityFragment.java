package com.sachet.database2;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.security.InvalidParameterException;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "MainActivityFragment";

    public static final int LOADER_ID = 0;

    private CursorRecyclerViewAdapter mAdapter;

    public MainActivityFragment() {
        Log.d(TAG, "MainActivityFragment: constructor called");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: starts");
        super.onActivityCreated(savedInstanceState);

        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);
        //telling the manager which loader we are initialising
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.task_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new CursorRecyclerViewAdapter(null, (CursorRecyclerViewAdapter.OnTaskClickListener)getActivity());
        recyclerView.setAdapter(mAdapter);
        Log.d(TAG, "onCreateView: returning ");

        return view;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        Log.d(TAG, "onCreateLoader: starts with an id "+i);

        String[] projection = {TaskContracts.Columns._ID, TaskContracts.Columns.TASKS_NAME,
                                TaskContracts.Columns.TASKS_DESCRIPTION, TaskContracts.Columns.TASKS_SORTORDER};
        //<order by> Tasks.SortOrder, Tasks.Name COLLATE NOCASE
        String sortOrder = TaskContracts.Columns.TASKS_SORTORDER+" , "+TaskContracts.Columns.TASKS_SORTORDER+" COLLATE NOCASE";

        switch(i){
            case LOADER_ID:
                return new CursorLoader(getActivity(),
                        TaskContracts.CONTENT_URI,
                        projection,
                        null,
                        null,
                        sortOrder);
            default:
                throw new InvalidParameterException(".onCreate() called with invalid loader id "+i);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        Log.d(TAG, "Entering onLoadFinished");
        mAdapter.swapCursor(cursor);

        int count = mAdapter.getItemCount();
        Log.d(TAG, "onLoadFinished: count is "+count);
        //we dont need to close the cursor as its not our cursor it belong to the CursorLoader cursor
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: starts");
        mAdapter.swapCursor(null);
        //now our adapter no longer holds the refrence to the cursor and then the loader is also free to close it
    }
}

/**
 * LoaderManager can handle several loaders, so each loader needs an unique number for identification
 * Fragment must implement the loadermanagers loader callback interface
 *
 * when managed by the LoaderManager, Loaders retain their existing cursor data across the activity
 * instance (for example, when it is restarted due to a configuration change), thus saving the cursor
 * from unnecessary, potentially expensive re-queries.
 *
 * As an added bonus, Loaders are intelligent enough to monitor the underlying data source for updates,
 * re-querying automatically when the data is changed.
 * ------------------------------------------------------------------------
 * onCreate()-instantiate and return a new loader for the given Id
 *
 * onLoadFinished() — Called when a previously created loader has finished its load. Note that normally
 * an application is not allowed to commit fragment transactions while in this call, since it can happen
 * after an activity's state is saved.
 *
 * onLoaderReset()-Called when a previously created loader is being reset, and thus making its data
 * unavailable. The application should at this point remove any references it has to the Loader's data.
 * LoaderManager.getInstance(this)
 * -------------------------------------------------------------------------------------------------------
 * In onCreate()
 *      projection-string array for the columns that we want to fetch from the database
 *      cursorLoader will be basically running queries for us in a background thread
 *      after returning the CursorLoader, the LoaderManager set the cursor loader awy fetching the data on a background thread
 *      when the cursorloader retrieves all the data it will let the loader manager know, and the loaderManager calls out
 *      onLoadFinished() method passing the cursor to it so that we have got the data
 * In onLoadFinished()
 *      After getting the cursor we will use it in an adapter that the recyclerView can use to display the data
 *
 * CursorLoader fetches data from the database via content provider
 * CursorLoader returns a cursor and we use that to provide the data for our RecyclerView
 * Now we need an adapter that sits between the RecyclerView and the data we want to display
 * CursorLoader requeries the database to refresh its cursor and triggers a call to fragments onLoadFinished method and
 * the the cursor is swapped using swapCursor method, the change that we had to make to the AppProvider were
 * a call to setNotificationUri on the cursor before the query method returned it
 *-------------------------------------------------------------------------------------------------------------------------------
 * where fragments become useful is when we use them dynamically in codes to change the display while the app is running
 */
