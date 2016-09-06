package etna.androiduniverse;

/**
 * Created by Roxi on 29/04/2016.
 */
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MySimpleAdapter extends SimpleAdapter {
    private Context mContext;
    public LayoutInflater inflater = null;

    public MySimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from,
                           int[] to) {
        super(context, data, resource, from, to);
        mContext = context;
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.layout_pokemon, null);
        HashMap<String, Object> data = (HashMap<String, Object>) getItem(position);

        new ImageDisp((ImageView) vi.findViewById(R.id.avatar))
                .execute((String) data.get("avatar"));
        TextView pseudo = (TextView) vi.findViewById(R.id.pseudo);
        pseudo.setText(data.get("pseudo").toString());
        TextView pokedexnbr = (TextView) vi.findViewById(R.id.pokedexnbr);
        pokedexnbr.setText(data.get("pokedexnbr").toString());
        return vi;
    }

}