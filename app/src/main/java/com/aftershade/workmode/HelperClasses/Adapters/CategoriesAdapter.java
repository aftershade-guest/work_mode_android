package com.aftershade.workmode.HelperClasses.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.HelperClasses.Listeners.Click_Listerner;
import com.aftershade.workmode.R;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesHolder> {

    Context context;
    String[] categoriesArr;
    Click_Listerner click_listerner;

    int[] drawables = new int[]{
            R.drawable.aerobics,
            R.drawable.anaerobic,
            R.drawable.calisthenics,
            R.drawable.circuit_training,
            R.drawable.strength_training,
            R.drawable.yoga

    };

    public CategoriesAdapter(Context context, String[] categories, Click_Listerner click_listerner) {
        this.context = context;
        this.click_listerner = click_listerner;
        this.categoriesArr = categories;
    }

    @NonNull
    @Override
    public CategoriesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.categories_card, parent, false);

        return new CategoriesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesHolder holder, int position) {

        holder.textView.setText(categoriesArr[position]);
        holder.imageView.setImageResource(drawables[position]);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_listerner.onClick(v, position, false);
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoriesArr.length;
    }

    public static class CategoriesHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;

        public CategoriesHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.categories_card_image);
            textView = itemView.findViewById(R.id.categories_card_text);

        }
    }

}
