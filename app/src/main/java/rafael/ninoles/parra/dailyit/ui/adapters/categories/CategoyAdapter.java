package rafael.ninoles.parra.dailyit.ui.adapters.categories;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import rafael.ninoles.parra.dailyit.R;
import rafael.ninoles.parra.dailyit.model.Category;
import rafael.ninoles.parra.dailyit.utilities.FirebaseContract;
import rafael.ninoles.parra.dailyit.utilities.Colors;

/**
 * Adapter to use in recycler views to print categories
 */
public class CategoyAdapter extends RecyclerView.Adapter<CategoyAdapter.CategoryViewHolder> {
    private final Context context;
    private List<Category> categories;
    private OnClickListenerOpenCategory listenerOpenCategory;

    public void setListenerOpenCategory(OnClickListenerOpenCategory listenerOpenCategory) {
        this.listenerOpenCategory = listenerOpenCategory;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public CategoyAdapter(Context context) {
        this.context = context;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        if (categories != null) {
            final Category category = categories.get(position);
            holder.setCategory(category);
        }
    }

    @Override
    public int getItemCount() {
        if (categories != null) {
            return categories.size();
        }
        return 0;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCategoryName;
        private final View categoryColor;
        private final CardView cardView;
        private Category category;


        public Category getCategory() {
            return category;
        }

        public void setCategory(Category category) {
            this.category = category;
            this.categoryColor.setBackgroundColor(Colors.getColor(category.getColor()));
            if (category.getName().equals(FirebaseContract.CategoryEntry.WORK)) {
                this.tvCategoryName.setText(context.getString(R.string.work));
            } else {
                this.tvCategoryName.setText(category.getName());
            }
            this.cardView.setOnClickListener(e -> {
                listenerOpenCategory.onItemClickOpen(category);
            });
        }

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            this.categoryColor = itemView.findViewById(R.id.categoryColor);
            this.cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
