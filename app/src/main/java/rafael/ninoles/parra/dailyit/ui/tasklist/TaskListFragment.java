package rafael.ninoles.parra.dailyit.ui.tasklist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rafael.ninoles.parra.dailyit.R;
import rafael.ninoles.parra.dailyit.databinding.FragmentHomeBinding;
import rafael.ninoles.parra.dailyit.databinding.FragmentTaskListBinding;
import rafael.ninoles.parra.dailyit.model.Task;
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

    // TODO: Rename and change types of parameters
    private String status;
    private TaskListViewModel viewModel;
    private TaskAdapter adapter;
    private FragmentTaskListBinding binding;

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
    public static TaskListFragment newInstance(String status) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        args.putString(STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            status = getArguments().getString(STATUS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        viewModel = new TaskListViewModel(status);
        binding = FragmentTaskListBinding.inflate(inflater, container, false);
        adapter = new TaskAdapter();
        binding.rvTasks.setAdapter(adapter);
        binding.rvTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Task> test = new ArrayList<>();
        Task testTask = new Task();
        testTask.setCreated(new Date(32456));
        testTask.setDescription("Descripcion");
        testTask.setExpires(new Date(3455667));
        testTask.setId("435565dfgd35");
        testTask.setStatus("Doing");
        testTask.setTitle("Titulazo");
        testTask.setUserUid("534534dfsfsd");
        test.add(testTask);
        test.add(testTask);
        test.add(testTask);
        test.add(testTask);
        test.add(testTask);
        test.add(testTask);
        test.add(testTask);
        test.add(testTask);
        adapter.setTasks(test);
        adapter.notifyDataSetChanged();
        View root = binding.getRoot();
        return root;
    }

}