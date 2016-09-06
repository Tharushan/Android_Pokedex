package etna.androiduniverse;

import android.content.Intent;
import android.media.Image;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SingleView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_view);

        Intent myIntent = getIntent();
        String apiUrl = myIntent.getStringExtra("url");

        apiCall(apiUrl);

    }

    private void apiCall(String url) {
        final Handler mHandler = new Handler(Looper.getMainLooper());

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        Request request = new Request.Builder()
                .url(url)
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
                                TextView title = (TextView) findViewById(R.id.title);
                                TextView heightValue = (TextView) findViewById(R.id.heightValue);
                                TextView weightValue = (TextView) findViewById(R.id.weightValue);
                                TextView height = (TextView) findViewById(R.id.height);
                                TextView weight = (TextView) findViewById(R.id.weight);

                                final int[] stats = {R.id.stats1, R.id.stats2, R.id.stats3, R.id.stats4, R.id.stats5, R.id.stats6};
                                final int[] statsValue = {R.id.stats1Value, R.id.stats2Value, R.id.stats3Value, R.id.stats4Value, R.id.stats5Value, R.id.stats6Value};

                                TextView stats1Value = (TextView) findViewById(R.id.stats1Value);

                                json = new JSONObject(mMessage);
                                String id = json.getString("id");
                                String imagesrc = "http://pokeapi.co/media/sprites/pokemon/" + id + ".png";
                                float heightFloat = Float.parseFloat(json.getString("height")) / 10;
                                float weightFloat = Float.parseFloat(json.getString("weight")) / 10;
                                String heightMeter = Float.toString(heightFloat) + "m";
                                String weightKilo = Float.toString(weightFloat) + "kg";
                                JSONArray array = json.getJSONArray("stats");
                                for (int i = 0; i < array.length(); i++) {
                                    TextView statsText = (TextView) findViewById(stats[i]);
                                    TextView statsValueText = (TextView) findViewById(statsValue[i]);
                                    JSONObject thisObject = array.getJSONObject(i);
                                    String name = thisObject.getJSONObject("stat").getString("name");
                                    statsValueText.setText(thisObject.getString("base_stat"));
                                    statsText.setText(name);
                                }

                                new DownloadImageTask((ImageView) findViewById(R.id.imageView)).execute(imagesrc);
                                String pokemon_name = json.getString("name");
                                       pokemon_name = pokemon_name.substring(0, 1).toUpperCase() + pokemon_name.substring(1);
                                title.setText(pokemon_name);

                                heightValue.setText(heightMeter);
                                weightValue.setText(weightKilo);
                                height.setVisibility(View.VISIBLE);
                                weight.setVisibility(View.VISIBLE);
                                TextView statsTitle = (TextView) findViewById(R.id.statsTitle);
                                statsTitle.setVisibility(View.VISIBLE);

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
