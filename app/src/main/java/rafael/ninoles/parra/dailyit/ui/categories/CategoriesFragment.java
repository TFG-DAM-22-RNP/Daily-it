package rafael.ninoles.parra.dailyit.ui.categories;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import rafael.ninoles.parra.dailyit.R;
import rafael.ninoles.parra.dailyit.databinding.FragmentCategoriesBinding;
import rafael.ninoles.parra.dailyit.databinding.FragmentTasksBinding;
import rafael.ninoles.parra.dailyit.model.Category;
import rafael.ninoles.parra.dailyit.repository.DailyItRepository;
import rafael.ninoles.parra.dailyit.ui.adapters.categories.CategoyAdapter;
import rafael.ninoles.parra.dailyit.ui.adapters.categories.OnClickListenerOpenCategory;
import rafael.ninoles.parra.dailyit.ui.adapters.task.TaskAdapter;
import rafael.ninoles.parra.dailyit.ui.dialogs.NewCategoryDialog;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoriesFragment extends Fragment {

    private FragmentCategoriesBinding binding;
    private CategoyAdapter adapter;
    private SwipeRefreshLayout swipe;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    public static CategoriesFragment newInstance() {
        return new CategoriesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCategoriesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        adapter = new CategoyAdapter(this.getContext());
        adapter.setListenerOpenCategory(this::showNewTaskDialog);
        binding.rvCategories.setAdapter(adapter);
        binding.rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        swipe = binding.swipe;
        swipe.setOnRefreshListener(() -> loadCategories());
        loadCategories();

        return root;
    }

    private void showNewTaskDialog(Category category) {
        new NewCategoryDialog(this.getContext(),this.getActivity(),category.getId());
    }

    private void loadCategories() {
        swipe.setRefreshing(true);
        DailyItRepository.getInstance().getCategoriesFromUser(FirebaseAuth.getInstance().getUid())
                .observe(this, categories -> {
                    adapter.setCategories(categories);
                    adapter.notifyDataSetChanged();
                    swipe.setRefreshing(false);
                });
    }
}