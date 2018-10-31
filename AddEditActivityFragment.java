package com.sachet.database2;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddEditActivityFragment extends Fragment {

    private static final String TAG = "AddEditActivityFragment";

    private enum FragmentEditMode{ ADD, EDIT }
    private FragmentEditMode mMode;

    private EditText mNameText;
    private EditText mDescriptionText;
    private EditText mSortOrder;
    private Button mSaveButton;
    
    public AddEditActivityFragment() {
        Log.d(TAG, "AddEditActivityFragment: constructor called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        View view = inflater.inflate(R.layout.fragment_add_edit, container, false);
        mNameText = view.findViewById(R.id.addedit_name);
        mDescriptionText = view.findViewById(R.id.addedit_description);
        mSortOrder = view.findViewById(R.id.addedit_sortorder);
        mSaveButton = view.findViewById(R.id.addedit_save);

//        Bundle arguments = getActivity().getIntent().getExtras();
        Bundle arguments = getArguments();

        final Tasks task;

        if(arguments!=null){

            task = (Tasks)arguments.getSerializable(Tasks.class.getSimpleName());
            if(task!= null){
                mNameText.setText(task.getName());
                mDescriptionText.setText(task.getDescription());
                mSortOrder.setText(Integer.toString(task.getSortOrder()));
                mMode = FragmentEditMode.EDIT;
            }else{
                mMode = FragmentEditMode.ADD;
            }

        }else{
            task = null;
            Log.d(TAG, "onCreateView: No arguments adding new record ");
            mMode = FragmentEditMode.ADD;
        }
        
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Iside the onClickListener");
                int so;
                if(mSortOrder.length()>0){
                    so = Integer.parseInt(mSortOrder.getText().toString());
                }else{
                    so = 0;
                }

                ContentResolver contentResolver = getActivity().getContentResolver();
                ContentValues contentValues = new ContentValues();

                switch (mMode){
                    case EDIT:
                        if(!mNameText.getText().toString().equals(task.getName())){
                            contentValues.put(TaskContracts.Columns.TASKS_NAME, mNameText.getText().toString());
                        }
                        if(!mDescriptionText.getText().toString().equals(task.getDescription())){
                            contentValues.put(TaskContracts.Columns.TASKS_DESCRIPTION, mDescriptionText.getText().toString());
                        }
                        if(so != task.getSortOrder()){
                            contentValues.put(TaskContracts.Columns.TASKS_SORTORDER, Integer.parseInt(mSortOrder.getText().toString()));
                        }
                        if(contentValues.size()>0){
                            Log.d(TAG, "onClick: updating task ");
                            contentResolver.update(TaskContracts.buildUri(task.getId()), contentValues, null, null);
                        }
                        break;
                    case ADD:
                        if(mNameText.length()>0){
                            Log.d(TAG, "onClick: adding new Task");
                            contentValues.put(TaskContracts.Columns.TASKS_NAME, mNameText.getText().toString());
                            contentValues.put(TaskContracts.Columns.TASKS_DESCRIPTION, mDescriptionText.getText().toString());
                            contentValues.put(TaskContracts.Columns.TASKS_SORTORDER, so);
                            contentResolver.insert(TaskContracts.CONTENT_URI,contentValues);
                        }
                        break;
                }
                Log.d(TAG, "onClick: done editing");
            }
        });

        return view;
    }
}

/**
 * This class will be used even when we run AddEditActivity, so even when we run the app on a phone
 * The code on this fragment will be used, so we can have both the fragment code and its layout as a sort of module
 * If we had to respond to all the clicks in the activity then we would had to duplicate all the code in here,
 * when we ran app in tablet.
 * Even though the AddEditActivity uses the activity_add_edit layout which includes the fragment, its this class
 * thats included not the layout
 * --------------------------------------------------------------------------------------------------------------
 * Idea of the fragment is that it can be reused and doesnt only have to be created by any particular activity,
 * so if we keep on relying on the addEditactivity's oject being passed to the fragment then we wont be able to reuse the fragment
 * --------------------------------------------------------------------------------------------------------------------------------
 * we need an adapter that is going to provide recycler view with views to display, and some sort of loader to provide the data for
 * the adapter
 * Google provides cursor loader class that lets us run queries in the background
 * We will also use cursor adapter to be used with the recycler view
 * --------------------------------------------------------------------------------------------------------------------------------
 * 1) MainActivity will display the MainActivityFragment and will deal with hings like delete and edit request
 * 2)MainActivityFragment contains the recyclerView and uses cursorLoader class to retrive the daata from the database
 * 3)The data will be retrived in the background thread, so that we dont block the UI thread, and that will be done by the
 *   CursorLoader.MainActivityFragment will start the loader, and loader fetches data in its loadInBackground method. When all the data
 *   is available, loader calls fragment's onLoadFinished method and passes the cursor to it
 * 4)MainActivityFragment then passes the cursor to an instance of CursorRecyclerAdapter class
 * 5)At that point, the TaskViewHolder object is passed back and forth between the recyclerView and the adpter
 * 6)Both of the buttons will be in the TaskViewHolder so thats a good place to put their onClickListener. When
 *   either of the button is tapped its listener calls the appropriate method in MainActivity
 * 7)If the user clicks the edit button for the task, the TaskEditRequest methods is called that creates AddEditActivityFragment
 *   to handle the editing, which is also created by tapping on the add menu item
 * 8)The AddEditActivityFragment is also created by the AddEditActivity when the phone is running in the portrait mode
 * ------------------------------------------------------------------------------------------------------------------------------------
 * we have change contentprovider to provide support for this automatic reloading of the data
 * LoaderManager manages the activities loaders, we use it to initialise loader and it takes care of everything up and starting the
 * loaders background thread, setting up the callbacks so the activity is notified of the data change, activity has only one loader
 * manager but that loader manager can itself manage many loaders, if our activity is getting data from different data source
 * we will use dufferent loaders but they are managed by the same loader manager
 * RecyclerView is displayed by our MainActivityFragment, so that is where we are going to add our loader
 * -------------------------------------------------------------------------------------------------------------------------------------
 * Usual technique for adding fragment on to the layout at runtime is by using a placeholder on the layout
 * If the layout contains task_detail_container (the frameLayout) then we will put the AddEditFragment into it otherwise we will
 * launch the AddEditActivity
 */