package etna.androiduniverse;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiCall("http://pokeapi.co/api/v2/type", "");
    }

    private void apiCall(String url, String parameters) {
        final Handler mHandler = new Handler(Looper.getMainLooper());

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, parameters);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("cache-control", "no-cache")
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                final String mMessage = e.toString();
                Log.e("MyApp", mMessage);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("MyApp", "ON FAILURE API CALL");
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {

                final String mMessage = response.body().string();
                Log.i("MyApp", mMessage);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mMessage != null || !mMessage.isEmpty()) {

                            JSONObject json = null;
                            try {
                                json = new JSONObject(mMessage);
                                List<String> list = new ArrayList<String>();
                                final JSONArray array = json.getJSONArray("results");
                                ListView mListView = (ListView) findViewById(R.id.listView);
                                for (int i = 0; i < array.length(); i++) {
                                    list.add(array.getJSONObject(i).getString("name"));

                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                                            android.R.layout.simple_list_item_1, list);
                                    mListView.setAdapter(adapter);
                                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            // Object listItem = list.getItemAtPosition(position);
                                            try {
                                                Intent myIntent = new Intent(MainActivity.this, Pokemons.class);
                                                myIntent.putExtra("type", String.valueOf(array.getJSONObject((int) id).getString("name")));
                                                myIntent.putExtra("url", String.valueOf(array.getJSONObject((int) id).getString("url")));
                                                startActivity(myIntent);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            ;
                                        }
                                    });

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }
}
