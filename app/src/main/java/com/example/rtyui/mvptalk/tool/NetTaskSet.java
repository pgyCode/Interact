package com.example.rtyui.mvptalk.tool;

import android.os.AsyncTask;

public class NetTaskSet extends AsyncTask<Void, Void, String> {

    private NetTaskSetListener listener;
    public NetTaskSet(NetTaskSetListener listener){
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        return listener.middle();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.before();
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        listener.after(string);
    }
}
