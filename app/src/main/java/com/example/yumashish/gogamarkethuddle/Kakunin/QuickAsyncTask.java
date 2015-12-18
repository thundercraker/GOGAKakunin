package com.example.yumashish.gogamarkethuddle.Kakunin;

import android.os.AsyncTask;

//!-------------------------------------------------------------------------+
//!                          Delay Asynctask                                |
//!-------------------------------------------------------------------------+
public class QuickAsyncTask<T1, T2, T3> extends AsyncTask<T1, T2, T3> {
    public interface QuickTask<QT1, QT2> {
        void Pre();
        QT2 Do(QT1... params);
        void Post(QT2 param);
    }

    QuickTask<T1, T3> quickie;

    public QuickAsyncTask(QuickTask<T1, T3> q) {
        quickie = q;
    }

    public void setQuickie(QuickTask<T1, T3> q) { quickie = q; }

    @Override
    protected void onPreExecute() {
        quickie.Pre();
    }

    @Override
    protected T3 doInBackground(T1... params) {
        return quickie.Do(params);
    }

    @Override
    protected void onPostExecute(T3 param) {
        quickie.Post(param);
    }
}