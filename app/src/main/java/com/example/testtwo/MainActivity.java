package com.example.testtwo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "testapp";
    private RecyclerView mPlantListView;
    private PlantListAdapter mAdapter;
    private ArrayList<PlantItem> mPlants = new ArrayList<PlantItem>();
    private PlantDBHelper mDbhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPlantListView = (RecyclerView) findViewById(R.id.plantListView);
        mPlantListView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new PlantListAdapter(mPlants);
        mPlantListView.setAdapter(mAdapter);

        new LoadDataTask().execute("https://gist.githubusercontent.com/edwingsm/11368543/raw/dd30694a3b176606f025c33b5e3a9edc6e300c51/plants.json");

        mDbhelper = new PlantDBHelper(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort) {
            new LoadFromDBTask().execute("sort");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    class LoadFromDBTask extends  AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String sort = PlantContract.PlantsTable.COLUMN_BOTANICAL;
            if (params[0].equals("")) { sort = null; }

            SQLiteDatabase db = mDbhelper.getReadableDatabase();

            String[] projection = {
                    PlantContract.PlantsTable._ID
                    , PlantContract.PlantsTable.COLUMN_COMMON
                    , PlantContract.PlantsTable.COLUMN_BOTANICAL
                    , PlantContract.PlantsTable.COLUMN_ZONE
                    , PlantContract.PlantsTable.COLUMN_LIGHT
                    , PlantContract.PlantsTable.COLUMN_PRICE
                    , PlantContract.PlantsTable.COLUMN_AVAILABILITY
            };
            Cursor c = db.query(PlantContract.PlantsTable.TABLE_NAME, projection, null, null, null, null, sort);

            mPlants.clear();
            c.moveToFirst();
            do {
                PlantItem item = new PlantItem(
                        c.getString(c.getColumnIndex(PlantContract.PlantsTable.COLUMN_COMMON))
                        , c.getString(c.getColumnIndex(PlantContract.PlantsTable.COLUMN_BOTANICAL))
                        , c.getString(c.getColumnIndex(PlantContract.PlantsTable.COLUMN_ZONE))
                        , c.getString(c.getColumnIndex(PlantContract.PlantsTable.COLUMN_LIGHT))
                        , c.getString(c.getColumnIndex(PlantContract.PlantsTable.COLUMN_PRICE))
                        , c.getString(c.getColumnIndex(PlantContract.PlantsTable.COLUMN_AVAILABILITY))
                );

                mPlants.add(item);
            } while (c.moveToNext());

            c.close();

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            mAdapter.notifyDataSetChanged();
        }
    }


    class LoadDataTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {

            JSONObject obj = getJSONFromUrl(params[0]);
            if (obj == null) return null;

            try {
                JSONObject cat = obj.getJSONObject("CATALOG");
                JSONArray plant = cat.getJSONArray("PLANT");

                // init database
                SQLiteDatabase  db = mDbhelper.getWritableDatabase();

                for (int i = 0; i < plant.length(); i++) {
                    JSONObject obj1 = plant.getJSONObject(i);

                    // saving to database
                    ContentValues values = new ContentValues();
                    values.put(PlantContract.PlantsTable.COLUMN_COMMON, obj1.getString("COMMON"));
                    values.put(PlantContract.PlantsTable.COLUMN_BOTANICAL, obj1.getString("BOTANICAL"));
                    values.put(PlantContract.PlantsTable.COLUMN_ZONE, obj1.getString("ZONE"));
                    values.put(PlantContract.PlantsTable.COLUMN_LIGHT, obj1.getString("LIGHT"));
                    values.put(PlantContract.PlantsTable.COLUMN_PRICE, obj1.getString("PRICE"));
                    values.put(PlantContract.PlantsTable.COLUMN_AVAILABILITY, obj1.getString("AVAILABILITY"));

                    db.insert(PlantContract.PlantsTable.TABLE_NAME, null, values);
                }

                Log.d(TAG, "doInBackground data saved to database");
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            new LoadFromDBTask().execute("");
        }
    }

    private JSONObject getJSONFromUrl (String Url) {
        try {
            URL url = new URL(Url);
            InputStream in;
            in = url.openConnection().getInputStream();

            BufferedReader streamReader = new BufferedReader(new InputStreamReader(in));
            StringBuilder strBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                strBuilder.append(inputStr);
            }

            JSONObject obj;
            obj = new JSONObject(strBuilder.toString());

            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
