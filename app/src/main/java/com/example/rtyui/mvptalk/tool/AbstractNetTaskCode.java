package com.example.rtyui.mvptalk.tool;

import android.content.Intent;
import android.os.AsyncTask;

public abstract class AbstractNetTaskCode extends AsyncTask<Void, Void, Integer> {

    @Override
    protected Integer doInBackground(Void... voids) {
        return middle();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        before();
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        after(integer);
    }

    protected abstract void before();
    protected abstract int middle();
    protected abstract void after(int integer);

}
