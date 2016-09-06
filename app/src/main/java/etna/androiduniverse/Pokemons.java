package etna.androiduniverse;

import android.content.Intent;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Pokemons extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemons);
        Intent myIntent = getIntent();
        TextView tv1 = (TextView)findViewById(R.id.type_title);
        String type = myIntent.getStringExtra("type");
        type = type.substring(0, 1).toUpperCase() + type.substring(1);
        tv1.setText(type + " pokemons");
        apiCall(myIntent.getStringExtra("url"), "");

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
                                List<HashMap<String, Object>> list = new ArrayList<>();
                                final JSONArray array = json.getJSONArray("pokemon");
                                for (int i = 0; i < array.length(); i++) {
                                    HashMap<String, Object> hm = new HashMap<String, Object>();
                                    String pokedexnbr = array.getJSONObject(i).getJSONObject("pokemon").getString("url");

                                    pokedexnbr = pokedexnbr.substring(33, pokedexnbr.length() - 1);
                                    String poke_url = "http://pokeapi.co/media/sprites/pokemon/"+pokedexnbr+".png";
                                    String pokemon_name = array.getJSONObject(i).getJSONObject("pokemon").getString("name");
                                    pokemon_name = pokemon_name.substring(0, 1).toUpperCase() + pokemon_name.substring(1);
                                    ImageView imge = new ImageView(Pokemons.this);
                                    hm.put("pseudo", pokemon_name);
                                    hm.put("pokedexnbr", "Pokemon n°" + pokedexnbr);
                                    hm.put("avatar", poke_url);
                                    list.add(hm);
                                    //ListView mListView = (ListView) findViewById(R.id.listView2);
                                    // Keys used in Hashmap
                                    String[] from = {"avatar", "pseudo","pokedexnbr" };

                                    // Ids of views in listview_layout
                                    int[] to = {R.id.avatar, R.id.pseudo,R.id.pokedexnbr};

                                    // SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), list, R.layout.layout_pokemon, from, to);
                                    MySimpleAdapter adapter = new MySimpleAdapter(getBaseContext(), list,
                                            R.layout.layout_pokemon, from, to);


                                   // list.add(array.getJSONObject(i).getJSONObject("pokemon").getString("name"));
                                    ListView mListView = (ListView) findViewById(R.id.listView2);

                                   // ArrayAdapter<String> adapter = new ArrayAdapter<String>(Pokemons.this,
                                    //        android.R.layout.simple_list_item_1, list);


                                    mListView.setAdapter(adapter);
                                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            // Object listItem = list.getItemAtPosition(position);
                                            try {
                                                Intent myIntent = new Intent(Pokemons.this, SingleView.class);
                                                myIntent.putExtra("url", String.valueOf(array.getJSONObject((int) id).getJSONObject("pokemon").getString("url")));
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
