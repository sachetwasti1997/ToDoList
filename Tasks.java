package com.sachet.database2;

import java.io.Serializable;

public class Tasks implements Serializable {
    public static final long serialVersionUID = 20181027L;

    private long m_Id;
    private final String mName;
    private final String mDescription;
    private final int mSortOrder;

    public Tasks(long m_Id, String name, String description, int sortOrder) {
        this.m_Id = m_Id;
        mName = name;
        mDescription = description;
        mSortOrder = sortOrder;
    }

    public long getId() {
        return m_Id;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getSortOrder() {
        return mSortOrder;
    }

    public void setId(long m_Id) {
        this.m_Id = m_Id;
    }

    @Override
    public String toString() {
        return "Name :"+mName
              +"\nDescription :"+mDescription
              +"\nSortOrder :"+mSortOrder;
    }
}
//we are going to assign the value to m_id field when we add Tasks to our database so we need to store

/**
 * Fragment can be thought as a subactivity, it can have its own layout, and can be embedded inside an activity's layout
 * to add some functionality in a modular fashion
 * When we dont use fragments most of our code goes into the content_main layout, one of the aim
 * of fragment is that it can be reused, so addeditactivity behaves just like any other activity as far as layout is
 * concerned but we also get the ability to reuse the fragment, in fragment's layout we put all the widgets that we want
 * to display the functional screen for our activity
 * incorporating an existing layout into a fragment is quite simple
 * -----------------------------------------------------------------------------------------------------------------------
 * getActivity()-get reference to the activity that the fragments is attached to
 */