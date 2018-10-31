package com.sachet.database2;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

class CursorRecyclerViewAdapter extends RecyclerView.Adapter<CursorRecyclerViewAdapter.TaskViewHolder>{
    private static final String TAG = "CursorRecyclerViewAdapt";
    private Cursor mCursor;

    private OnTaskClickListener mOnTaskClickListener;

    interface OnTaskClickListener{
        void onEditClick(Tasks tasks);
        void onDeleteClick(Tasks task);
    }

    public CursorRecyclerViewAdapter(Cursor cursor, OnTaskClickListener onTaskClickListener) {
        Log.d(TAG, "CursorRecyclerViewAdapter: constructor called");
        mCursor = cursor;
        mOnTaskClickListener = onTaskClickListener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d(TAG, "onCreateViewHolder: new view requested ");
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.task_list_items, viewGroup, false);
        return new TaskViewHolder(view);
        //this will be followed by the call to onBindViewHolder, and there we put the data into the view
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder taskViewHolder, int i) {
        Log.d(TAG, "onBindViewHolder: starts");

        if(mCursor == null||(mCursor.getCount() == 0)){
            Log.d(TAG, "onBindViewHolder: providing instructions");
            taskViewHolder.name.setText(R.string.instruction_heading);

            taskViewHolder.description.setText(R.string.text_lines);

            taskViewHolder.editbutton.setVisibility(View.GONE);//TODO add onClickListener
            taskViewHolder.deletebutton.setVisibility(View.GONE);//TODO add onClickListener
        }else{
            if(!mCursor.moveToPosition(i)){
                throw new IllegalStateException("Couldn't move the cursor to position "+i);
            }

            final Tasks tasks = new Tasks(mCursor.getLong(mCursor.getColumnIndex(TaskContracts.Columns._ID)),
                    mCursor.getString(mCursor.getColumnIndex(TaskContracts.Columns.TASKS_NAME)),
                    mCursor.getString(mCursor.getColumnIndex(TaskContracts.Columns.TASKS_DESCRIPTION)),
                    mCursor.getInt(mCursor.getColumnIndex(TaskContracts.Columns.TASKS_SORTORDER)));

            taskViewHolder.name.setText(tasks.getName());
            taskViewHolder.description.setText(tasks.getDescription());
            taskViewHolder.editbutton.setVisibility(View.VISIBLE);
            taskViewHolder.deletebutton.setVisibility(View.VISIBLE);



            View.OnClickListener buttonListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.tli_edit:
                            if(mOnTaskClickListener!=null){
                                mOnTaskClickListener.onEditClick(tasks);
                            }
                            break;
                        case R.id.tli_delete:
                            if(mOnTaskClickListener!=null){
                                mOnTaskClickListener.onDeleteClick(tasks);
                            }
                            break;
                        default:
                            Log.d(TAG, "onClick: unknown button id");
                    }
                    Log.d(TAG, "onClick: starts");
                    Log.d(TAG, "onClick: button with id "+v.getId()+" clicked and taskname is"+ tasks.getName());
                }
            };
            taskViewHolder.editbutton.setOnClickListener(buttonListener);
            taskViewHolder.deletebutton.setOnClickListener(buttonListener);
        }

    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: starts ");
        if((mCursor == null)||(mCursor.getCount()) == 0){
            return 1;
        }else{
            return mCursor.getCount();
        }
    }

    Cursor swapCursor(Cursor newCursor){
        if(newCursor == mCursor){
            return null;
        }
        final Cursor oldCursor = mCursor;
        mCursor = newCursor;
        if(mCursor!=null){
            //notify the observer about the dataset change
            notifyDataSetChanged();
        }else{
            //notify the observer about the lack of dataset
            notifyItemRangeRemoved(0, getItemCount());//Notify any registered observers that the getItemCount()
            // items previously located at positionStart(0) have been removed from the data set.
        }
        return oldCursor;
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "TaskViewHolder";

        TextView name;
        TextView description;
        ImageButton deletebutton;
        ImageButton editbutton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.tli_name);
            this.description = itemView.findViewById(R.id.tli_description);
            this.editbutton = itemView.findViewById(R.id.tli_edit);
            this.deletebutton = itemView.findViewById(R.id.tli_delete);
        }
    }

}

/**
 * TaskViewHolder will be static inner class, which will not only safeguards against the memory leaks
 * and is effectivly the same as creating the class in its own file, the only difference being that we have to
 * fully qualify the class name when we refer to it from outside the containing class
 * -------------------------------------------------------------------------------------------------------------
 * This adapter is going to use the cursor as the source of data to be displayed
 * -------------------------------------------------------------------------------------------------------------
 * onCreateViewHolder() called by the RecyclerView when it needs new data to display
 *                      here it will inflate the {@link task_list_items} layout and then return that view
 * -------------------------------------------------------------------------------------------------------------
 * The buildin cursorLoader monitors changes from the content provider
 * -------------------------------------------------------------------------------------------------------------
 * 1)The CursorRecyclerAdapter is created by our mainActivityFragment and it also owns the layout that is displaying
 *  our RecyclerView
 * 2)But the fragment doesnt know anything about the AddEditActiviyFragment that will be used to edit task detail
 * 3)Fragment are managed by activities and Fragment can always get a refrence to the activity that its attached to
 *  Getting fragments to create another fragment is not a good idea. So editing and deleting will be initiated by
 *  mainActivity
 */
