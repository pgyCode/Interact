package com.example.rtyui.mvptalk.tool;

import android.os.AsyncTask;

public class NetTaskCodeEasy extends AsyncTask<Void, Void, Integer> {

    private NetTaskCodeEasyListener listener;
    public NetTaskCodeEasy(NetTaskCodeEasyListener listener){
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
        switch (integer){
            case App.NET_SUCCEED:
                listener.succeed();
                break;
            default:
                listener.failed();
                break;
        }
    }
}
