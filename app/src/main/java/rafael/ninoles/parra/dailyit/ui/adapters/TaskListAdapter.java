package rafael.ninoles.parra.dailyit.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import rafael.ninoles.parra.dailyit.ui.tasklist.TaskListFragment;

public class TaskListAdapter extends FragmentStateAdapter {
    public TaskListAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return TaskListFragment.newInstance("ToDo");
            case 1:
                return TaskListFragment.newInstance("Doing");
            case 2:
                return TaskListFragment.newInstance("Done");
            default:
                return TaskListFragment.newInstance("Doing");
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
