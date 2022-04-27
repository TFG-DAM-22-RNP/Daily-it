package rafael.ninoles.parra.dailyit.ui.tasks;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Date;

import rafael.ninoles.parra.dailyit.R;
import rafael.ninoles.parra.dailyit.databinding.FragmentTaskListBinding;
import rafael.ninoles.parra.dailyit.databinding.FragmentTasksBinding;
import rafael.ninoles.parra.dailyit.ui.adapters.TaskAdapter;
import rafael.ninoles.parra.dailyit.ui.adapters.TaskListAdapter;
import rafael.ninoles.parra.dailyit.ui.tasklist.TaskListViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TasksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TasksFragment extends Fragment {

    private static final String DATE_PATTERN = "dd MMM. yyyy";
    private final Date actualDate = new Date();
    private FragmentTasksBinding binding;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN);

    public TasksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TasksFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TasksFragment newInstance() {
        TasksFragment fragment = new TasksFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTasksBinding.inflate(inflater, container, false);
        binding.tvDate.setText(simpleDateFormat.format(actualDate));
        binding.pager.setAdapter(new TaskListAdapter(getParentFragmentManager(),getLifecycle()));
        binding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position));
            }
        });
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        View root = binding.getRoot();
        return root;
    }
}