package rafael.ninoles.parra.dailyit.ui.tasklist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rafael.ninoles.parra.dailyit.R;
import rafael.ninoles.parra.dailyit.databinding.FragmentHomeBinding;
import rafael.ninoles.parra.dailyit.databinding.FragmentTaskListBinding;
import rafael.ninoles.parra.dailyit.model.MyDate;
import rafael.ninoles.parra.dailyit.model.Task;
import rafael.ninoles.parra.dailyit.repository.DailyItRepository;
import rafael.ninoles.parra.dailyit.ui.adapters.TaskAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String STATUS = "status";
    private static final String DATE = "date";

    // TODO: Rename and change types of parameters
    private String status;
    private MyDate date;
    private TaskListViewModel viewModel;
    private TaskAdapter adapter;
    private FragmentTaskListBinding binding;
    private SwipeRefreshLayout swipeRefreshLayout;

    public TaskListViewModel getViewModel() {
        return viewModel;
    }

    public MyDate getDate() {
        return date;
    }

    public void setDate(MyDate date) {
        swipeRefreshLayout.setRefreshing(true);
        this.date = date;
        viewModel.setDate(date);
        viewModel.updateTasks(date).observe(getViewLifecycleOwner(),taskList -> {
            adapter.setTasks(clearOldTaskes(taskList));
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    //TODO quitar
    public void printTest(){
        System.out.println("Dentro de printTest");
        System.out.println(new SimpleDateFormat("dd MMM. yyyy").format(date));
    }

    public TaskListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param status Status..
     * @return A new instance of fragment TaskListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TaskListFragment newInstance(String status, MyDate date) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        args.putString(STATUS, status);
        args.putParcelable(DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            status = getArguments().getString(STATUS);
            date = getArguments().getParcelable(DATE);
        }
    }

    private List<Task> clearOldTaskes(List<Task> taskes){
        for (int i = taskes.size()-1; i >= 0; i--) {
            Task actual = taskes.get(i);
            if(actual.getCreated().getTime() > date.getTime() && actual.getStatus().equals("Done")){
                taskes.remove(actual);
            }
        }
        return taskes;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        viewModel = new TaskListViewModel(this.requireActivity().getApplication(), status, date);
        binding = FragmentTaskListBinding.inflate(inflater, container, false);
        adapter = new TaskAdapter();
        swipeRefreshLayout = binding.swipeContainer;
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                System.out.println(new SimpleDateFormat("dd MMM. yyyy").format(date));
                viewModel.updateTasks(date).observe(getViewLifecycleOwner(),taskList -> {

                    adapter.setTasks(clearOldTaskes(taskList));
                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                });

            }
        });
        binding.rvTasks.setAdapter(adapter);
        binding.rvTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        viewModel.getAllTasks().observe(getViewLifecycleOwner(),taskList -> {
            System.out.println("HA CAMBIADO");
            adapter.setTasks(clearOldTaskes(taskList));
            adapter.notifyDataSetChanged();
        });
        View root = binding.getRoot();
        return root;
    }

}