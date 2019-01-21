package com.iamchuckss.foodcart.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.iamchuckss.foodcart.R;

public class WorkCounter {

    private int runningTasks;
    private final Context mContext;

    public WorkCounter(int numberOfTasks, Context context) {
        this.runningTasks = numberOfTasks;
        this.mContext = context;
    }
    // Only call this in onPostExecute! (or add synchronized to method declaration)
    public void taskFinished() {
        if (--runningTasks == 0) {
            LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(this.mContext);
            mgr.sendBroadcast(new Intent(mContext.getString(R.string.task_finished_broadcast)));
        }
    }

    public int getRunningTasks() {
        return runningTasks;
    }
}
