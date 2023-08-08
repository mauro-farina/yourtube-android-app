package it.units.sim.yourtube;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.units.sim.yourtube.model.Category;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private List<Category> categories;
    private final View.OnClickListener onItemClickListener;

    public CategoriesAdapter(List<Category> categories, View.OnClickListener onItemClickListener) {
        this.categories = categories;
        this.onItemClickListener = onItemClickListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setCategoriesList(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_item_category, parent, false);
        return new CategoriesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView categoryNameTextView = holder.getCategoryNameTextView();
        ImageView categoryIconImageView = holder.getCategoryIconImageView();
        categoryNameTextView.setText(categories.get(position).getName());
        categoryIconImageView.setImageResource(categories.get(position).getDrawableIconResId());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView categoryNameTextView;
        private final ImageView categoryIconimageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameTextView = itemView.findViewById(R.id.list_item_category_name);
            categoryIconimageView = itemView.findViewById(R.id.list_item_category_icon);
            itemView.setOnClickListener(onItemClickListener);
        }

        public TextView getCategoryNameTextView() {
            return categoryNameTextView;
        }
        public ImageView getCategoryIconImageView() {
            return categoryIconimageView;
        }
    }
}
