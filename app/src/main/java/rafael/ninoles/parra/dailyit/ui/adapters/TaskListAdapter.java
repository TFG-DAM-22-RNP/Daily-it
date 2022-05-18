package rafael.ninoles.parra.dailyit.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

import rafael.ninoles.parra.dailyit.model.MyDate;
import rafael.ninoles.parra.dailyit.ui.tasklist.TaskListFragment;

public class TaskListAdapter extends FragmentStateAdapter {
    private MyDate myDate;
    List<TaskListFragment> fragments = new ArrayList<>();
    public TaskListAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, MyDate myDate) {
        super(fragmentManager, lifecycle);
        this.myDate = myDate;
    }

    public List<TaskListFragment> getFragments() {
        return fragments;
    }

    //TODO Seguramente esto se puede borrar, investigar
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        TaskListFragment fragment = null;
        switch (position){
            case 0:
                fragment = TaskListFragment.newInstance("ToDo", myDate);
                fragments.add(fragment);
                // TODO BORRAR
                System.out.println("EL SIZE ES "+fragments.size());
                return fragment;
            case 1:
                fragment = TaskListFragment.newInstance("Doing", myDate);
                fragments.add(fragment);
                // TODO BORRAR
                System.out.println("EL SIZE ES "+fragments.size());
                return fragment;
            case 2:
                fragment = TaskListFragment.newInstance("Done", myDate);
                fragments.add(fragment);
                // TODO BORRAR
                System.out.println("EL SIZE ES "+fragments.size());
                return fragment;
            default:
                fragment = TaskListFragment.newInstance("Doing", myDate);
                fragments.add(fragment);
                // TODO BORRAR
                System.out.println("EL SIZE ES "+fragments.size());
                return fragment;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
