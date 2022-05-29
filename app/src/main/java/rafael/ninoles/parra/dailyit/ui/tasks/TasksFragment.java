package rafael.ninoles.parra.dailyit.ui.tasks;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import rafael.ninoles.parra.dailyit.R;
import rafael.ninoles.parra.dailyit.databinding.FragmentTaskListBinding;
import rafael.ninoles.parra.dailyit.databinding.FragmentTasksBinding;
import rafael.ninoles.parra.dailyit.helpers.MainActivityHelper;
import rafael.ninoles.parra.dailyit.model.MyDate;
import rafael.ninoles.parra.dailyit.ui.adapters.TaskAdapter;
import rafael.ninoles.parra.dailyit.ui.adapters.TaskListAdapter;
import rafael.ninoles.parra.dailyit.ui.tasklist.TaskListFragment;
import rafael.ninoles.parra.dailyit.ui.tasklist.TaskListViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TasksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TasksFragment extends Fragment {

    private static final String DATE_PATTERN = "dd MMM. yyyy";
    private static final long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;

    private final MyDate actualDate = new MyDate();
    private final Calendar calendar = Calendar.getInstance();
    private MyDate currentDate = actualDate;
    private FragmentTasksBinding binding;
    private TaskListAdapter taskListAdapter;
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
        MainActivityHelper.setTasksFragment(this);
        binding = FragmentTasksBinding.inflate(inflater, container, false);
        addDaySelectorListeners();
        binding.tvDate.setText(simpleDateFormat.format(currentDate));
        binding.tvDate.setOnClickListener(e->{
            Calendar newCalendar = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), (view, year, monthOfYear, dayOfMonth) -> {
                calendar.set(year, monthOfYear, dayOfMonth);
                MyDate newDate = new MyDate(calendar.getTimeInMillis());
                moveDay(newDate);
            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });
        taskListAdapter = new TaskListAdapter(getParentFragmentManager(), getLifecycle(), currentDate, this);
        binding.pager.setAdapter(taskListAdapter);
        binding.pager.setUserInputEnabled(false);
        addChangeTabOnClick();
        View root = binding.getRoot();
        return root;
    }

    private void addDaySelectorListeners() {
        binding.ivPrevDay.setOnClickListener(e->{
            movePreviousDay();
        });

        binding.ivNextDay.setOnClickListener(e->{
            moveNextDay();
        });
    }

    private void moveNextDay() {
        MyDate newDate = new MyDate(currentDate.getTime() + MILLIS_IN_A_DAY);
        newDate.setHours(0);
        newDate.setMinutes(0);
        newDate.setSeconds(0);
        moveDay(newDate);
    }

    private void movePreviousDay(){
        MyDate newDate = new MyDate(currentDate.getTime() - MILLIS_IN_A_DAY);
        newDate.setHours(0);
        newDate.setMinutes(0);
        newDate.setSeconds(0);
        moveDay(newDate);
    }

    public void moveDay(MyDate date){
        currentDate = date;
        binding.tvDate.setText(simpleDateFormat.format(currentDate));
        updateAllStatus();
    }

    public void updateAllStatus() {
        for(TaskListFragment taskListFragment : taskListAdapter.getFragments()){
            taskListFragment.setDate(currentDate);
        }
    }

    private void addChangeTabOnSwipe() {
        binding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position));
            }
        });
    }

    private void addChangeTabOnClick() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
}