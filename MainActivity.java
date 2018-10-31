package com.sachet.database2;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity implements CursorRecyclerViewAdapter.OnTaskClickListener{

    private static final String TAG = "MainActivity";

    private boolean mTwoPanes = false;

    public static final String ADD_EDIT_FRAGMENT = "AddEditFragment";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(findViewById(R.id.task_details_container)!= null){
            mTwoPanes = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id){
            case R.id.menumain_addTask:
                taskEditRequest(null);
                break;
            case R.id.addedit_description:
                break;
            case R.id.menumain_showAbout:
                break;
            case R.id.menumain_showDurations:
                break;
            case R.id.mainmenu_settings:
                break;
            case R.id.menumain_generate:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEditClick(Tasks tasks) {
        taskEditRequest(tasks);
    }

    @Override
    public void onDeleteClick(Tasks task) {
        getContentResolver().delete(TaskContracts.buildUri(task.getId()), null, null);
    }

    private void taskEditRequest(Tasks task){
        Log.d(TAG, "taskEditRequest: starts");
        if(mTwoPanes){
            Log.d(TAG, "taskEditRequest: in two pane mode");
            AddEditActivityFragment activityFragment = new AddEditActivityFragment();//reusing that fragment

            Bundle bundle = new Bundle();
            bundle.putSerializable(Tasks.class.getSimpleName(), task);
            activityFragment.setArguments(bundle);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.task_details_container, activityFragment);
            fragmentTransaction.commit();

        }else{
            Log.d(TAG, "taskEditRequest: in phone mode");
            Intent intent = new Intent(this, AddEditActivity.class);
            if(task!=null){
                intent.putExtra(Tasks.class.getSimpleName(), task);
                startActivity(intent);
            }else{
                startActivity(intent);
            }
        }
    }
}

/**
 * We are progrmatically going to add fragments to the existing viewgroup
 * AddEditActivityFragment contains the UI that is to be displayed while we add or update task details
 * At any time while your activity is running, you can add fragments to your activity layout. You simply
 * need to specify a ViewGroup in which to place the fragment.
 *
 * To make fragment transactions in your activity (such as add, remove, or replace a fragment), you must
 * use APIs from FragmentTransaction. You can get an instance of FragmentTransaction from your
 * FragmentActivity like this:
 *      FragmentManager fragmentManager = getSupportFragmentManager();
 *      FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
 * --------------------------------------------------------------------------------------------------------
 * Once we got our fragmentManager on line 89 we call the beginTransaction method on line 90, that then
 * gives us the Fragment transaction object that we use to perform operations that we want on our fragments
 * If there are many transactions to be performed, it queues up all our changes then perform them once we call commit
 */