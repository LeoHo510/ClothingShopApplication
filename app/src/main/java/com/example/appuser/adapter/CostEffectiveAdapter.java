package com.example.appuser.adapter;

import android.content.Context;
import android.graphics.drawable.PictureDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.appuser.R;
import com.example.appuser.model.CostEffective;

import java.util.List;

public class CostEffectiveAdapter extends ArrayAdapter<CostEffective> {
    Context context;
    List<CostEffective> items;

    public CostEffectiveAdapter(@NonNull Context context, @NonNull List<CostEffective> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cost_effective_item_view, parent, false);
        }

        CostEffective effective = items.get(position);

        if (effective != null) {
            ImageView ce_image = convertView.findViewById(R.id.ce_image);
            TextView ce_title = convertView.findViewById(R.id.ce_title);
            TextView ce_content = convertView.findViewById(R.id.ce_content);

            Glide.with(context)
                    .as(PictureDrawable.class)
                    .load(effective.getImage_url())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ce_image);
            ce_title.setText(effective.getTitle());
            ce_content.setText(effective.getContent());
        }

        return convertView;
    }
}
