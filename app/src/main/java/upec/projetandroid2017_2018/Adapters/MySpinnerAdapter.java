package upec.projetandroid2017_2018.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import upec.projetandroid2017_2018.R;

/**
 * Created by Sasig on 23/03/2018.
 */

public class MySpinnerAdapter extends ArrayAdapter<String> {
    public MySpinnerAdapter(Context context, ArrayList<String> categories){
        super(context,0, categories);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.myspinner, parent, false
            );
        }

        TextView textViewName = convertView.findViewById(R.id.textViewSpin);

        String category = getItem(position);
        if (category != null) {
            //imageViewFlag.setImageResource(currentItem.getFlagImage());
            textViewName.setText(category);
        }

        return convertView;
    }
}
