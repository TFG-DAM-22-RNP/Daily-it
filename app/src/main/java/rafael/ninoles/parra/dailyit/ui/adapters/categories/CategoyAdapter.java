package rafael.ninoles.parra.dailyit.ui.adapters.categories;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import rafael.ninoles.parra.dailyit.R;
import rafael.ninoles.parra.dailyit.model.Category;
import rafael.ninoles.parra.dailyit.model.FirebaseContract;
import rafael.ninoles.parra.dailyit.model.Task;
import rafael.ninoles.parra.dailyit.ui.adapters.task.OnClickListenerDeleteTask;
import rafael.ninoles.parra.dailyit.ui.adapters.task.OnClickListenerOpenTask;
import rafael.ninoles.parra.dailyit.ui.adapters.task.TaskAdapter;
import rafael.ninoles.parra.dailyit.utilities.Colors;

public class CategoyAdapter extends RecyclerView.Adapter<CategoyAdapter.CategoryViewHolder> {
    private List<Category> categories;
    private OnClickListenerDeleteCategory listenerDeleteCategory;
    private Context context;

    public List<Category> getCategories() {
        return categories;
    }

    public CategoyAdapter(Context context){
        this.context = context;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public void setListenerDeleteCategory(OnClickListenerDeleteCategory listenerDeleteCategory) {
        this.listenerDeleteCategory = listenerDeleteCategory;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category,parent,false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        if (categories != null) {
            final Category category = categories.get(position);
            // TODO QUITAR
            System.out.println("LA CATEGORIA ES");
            System.out.println(category.getName());
            holder.setCategory(category);
        }
    }

    @Override
    public int getItemCount() {
        if(categories!=null){
            return categories.size();
        }
        return 0;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private Category category;
        private TextView tvCategoryName;
        private View categoryColor;

        public Category getCategory() {
            return category;
        }

        public void setCategory(Category category) {
            this.category = category;
            this.categoryColor.setBackgroundColor(Colors.getColor(category.getColor()));
            if(category.getName().equals(FirebaseContract.CategoryEntry.WORK)){
                this.tvCategoryName.setText(context.getString(R.string.work));
            }else{
                this.tvCategoryName.setText(category.getName());
            }
        }

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            this.categoryColor = itemView.findViewById(R.id.categoryColor);
        }
    }
}
