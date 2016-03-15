package com.example.erik.jsonparsingandimageloading;

import android.support.v7.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    ArrayList<Actors> actorsList;

    ActorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actorsList = new ArrayList<Actors>();
        new JSONAsyncTask().execute("http://microblogging.wingnity.com/JSONParsingTutorial/jsonActors");

        ListView listview = (ListView)findViewById(R.id.list);
        adapter = new ActorAdapter(getApplicationContext(), R.layout.row, actorsList);

        Log.v("count", String.valueOf(adapter.getCount()));

        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long id) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), actorsList.get(position).getName(), Toast.LENGTH_LONG).show();
            }
        });
    }


    class JSONAsyncTask extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Loading, please wait");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                String data = getDataFromUrl(urls[0]);

                JSONObject jsono = new JSONObject(data);
                JSONArray jarray = jsono.getJSONArray("actors");

                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject object = jarray.getJSONObject(i);

                    Actors actor = new Actors();

                    actor.setName(object.getString("name"));
                    actor.setDescription(object.getString("description"));
                    actor.setDob(object.getString("dob"));
                    actor.setCountry(object.getString("country"));
                    actor.setHeight(object.getString("height"));
                    actor.setSpouse(object.getString("spouse"));
                    actor.setChildren(object.getString("children"));
                    actor.setImage(object.getString("image"));

                    actorsList.add(actor);
                }
                return true;

            } catch (ParseException e1) {
                e1.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            dialog.cancel();
            adapter.notifyDataSetChanged();
            if(result == false)
                Toast.makeText(getApplicationContext(), "Unable to fetch data from server", Toast.LENGTH_LONG).show();

        }

        public String getDataFromUrl(String url) {
            String result = null;
//        System.out.println("URL comes in jsonparser class is:  " + url);
            try {
                URL myurl=new URL(url);
                HttpURLConnection urlConnection = (HttpURLConnection) myurl
                        .openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.connect();
                InputStream is=urlConnection.getInputStream();
                if (is != null) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    try {
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(is));
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                        reader.close();
                    } finally {
                        is.close();
                    }
                    result = sb.toString();
                }
            }catch (Exception e){
                result=null;
            }
            return result;
        }
    }
}
