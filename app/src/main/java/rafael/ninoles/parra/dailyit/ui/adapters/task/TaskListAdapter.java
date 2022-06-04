package rafael.ninoles.parra.dailyit.ui.adapters.task;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

import rafael.ninoles.parra.dailyit.model.MyDate;
import rafael.ninoles.parra.dailyit.ui.tasklist.TaskListFragment;
import rafael.ninoles.parra.dailyit.ui.tasks.TasksFragment;

public class TaskListAdapter extends FragmentStateAdapter {
    private MyDate myDate;
    private final TasksFragment tasksFragment;
    List<TaskListFragment> fragments = new ArrayList<>();
    public TaskListAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, MyDate myDate, TasksFragment tasksFragment) {
        super(fragmentManager, lifecycle);
        this.myDate = myDate;
        this.tasksFragment = tasksFragment;
    }

    public List<TaskListFragment> getFragments() {
        return fragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        TaskListFragment fragment = null;
        switch (position){
            case 0:
                fragment = TaskListFragment.newInstance("ToDo", myDate, tasksFragment);
                fragments.add(fragment);
                return fragment;
            case 1:
                fragment = TaskListFragment.newInstance("Doing", myDate, tasksFragment);
                fragments.add(fragment);
                return fragment;
            case 2:
                fragment = TaskListFragment.newInstance("Done", myDate, tasksFragment);
                fragments.add(fragment);
                return fragment;
            default:
                fragment = TaskListFragment.newInstance("Doing", myDate, tasksFragment);
                fragments.add(fragment);
                return fragment;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
