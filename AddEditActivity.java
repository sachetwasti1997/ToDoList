package com.sachet.database2;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class AddEditActivity extends AppCompatActivity {

    private static final String TAG = "AddEditActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AddEditActivityFragment activityFragment = new AddEditActivityFragment();

//        Bundle arguments = getActivity().getIntent().getExtras(); since this is the required activity
        //so there isn't any need to call getActivity() as above
        Bundle arguments = getIntent().getExtras();
        activityFragment.setArguments(arguments);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment, activityFragment);
        fragmentTransaction.commit();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
