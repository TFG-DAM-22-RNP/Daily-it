package rafael.ninoles.parra.dailyit.ui.tasks;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import rafael.ninoles.parra.dailyit.databinding.FragmentTasksBinding;
import rafael.ninoles.parra.dailyit.helpers.MainActivityHelper;
import rafael.ninoles.parra.dailyit.model.MyDate;
import rafael.ninoles.parra.dailyit.ui.adapters.task.TaskListAdapter;
import rafael.ninoles.parra.dailyit.ui.tasklist.TaskListFragment;

/**
 * Fragment to show tasks in the MainActivity. It contains a ViewPager2 with 3 fragments inside it.
 * A simple {@link Fragment} subclass.
 * Use the {@link TasksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TasksFragment extends Fragment {
    private static final String LOG_TAG = "TaskFragment";
    private static final String DATE_PATTERN = "dd MMM. yyyy";
    private static final long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
    public static final int TODO = 0;
    public static final int DOING = 1;
    public static final int DONE = 2;

    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN);
    private Date actualDate = new Date();
    private Date currentDate = actualDate;
    private FragmentTasksBinding binding;
    private TaskListAdapter taskListAdapter;

    public TasksFragment() {
        // Required empty public constructor
    }

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
        setCurrentDate();
        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adBottom.loadAd(adRequest);
        addDaySelectorListeners();
        binding.tvDate.setText(simpleDateFormat.format(currentDate));
        binding.tvDate.setOnClickListener(e->{
            Calendar newCalendar = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), (view, year, monthOfYear, dayOfMonth) -> {
                calendar.set(year, monthOfYear, dayOfMonth);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 1);
                Date newDate = calendar.getTime();
                currentDate = newDate;
                moveDay(newDate);
            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });
        taskListAdapter = new TaskListAdapter(getChildFragmentManager(), getLifecycle(), new MyDate(currentDate.getTime()), this);
        binding.pager.setAdapter(taskListAdapter);
        binding.pager.setUserInputEnabled(false);
        addChangeTabOnClick();
        return binding.getRoot();
    }

    private void setCurrentDate() {
        Calendar currentDateCalendar = Calendar.getInstance();
        currentDateCalendar.setTime(actualDate);
        currentDateCalendar.set(Calendar.HOUR_OF_DAY,0);
        currentDateCalendar.set(Calendar.MINUTE,0);
        currentDateCalendar.set(Calendar.SECOND,1);
        actualDate = currentDateCalendar.getTime();
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
        Date newDate = new Date(currentDate.getTime() + MILLIS_IN_A_DAY);
        newDate = moveCurrentDate(newDate);
        moveDay(newDate);
    }

    private void movePreviousDay(){
        Date newDate = new Date(currentDate.getTime() - MILLIS_IN_A_DAY);
        newDate = moveCurrentDate(newDate);
        moveDay(newDate);
    }

    @NonNull
    private Date moveCurrentDate(Date newDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(newDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 1);
        newDate = calendar.getTime();
        currentDate = newDate;
        return newDate;
    }

    public void moveDay(Date date){
        Log.i(LOG_TAG,"Changing date to "+new SimpleDateFormat("dd MM yy HH:mm:ss").format(date));
        currentDate = date;
        binding.tvDate.setText(simpleDateFormat.format(currentDate));
        updateAllStatus();
    }

    public void moveDay(){
        moveDay(currentDate);
    }

    public void updateAllStatus() {
        for(TaskListFragment taskListFragment : taskListAdapter.getFragments()){
            taskListFragment.setDate(currentDate);
        }
    }

    public void moveToTab(int position){
        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position));
    }

    /**
     * Not used because it make harder to swipe and open the menu
     */
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