package com.example.rtyui.mvptalk.tool;

import android.os.AsyncTask;

public class NetTaskCode extends AsyncTask<Void, Void, Integer> {

    private NetTaskCodeListener listener;
    public NetTaskCode(NetTaskCodeListener listener){
        this.listener = listener;
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        return listener.middle();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.before();
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        listener.after(integer);
    }
}
