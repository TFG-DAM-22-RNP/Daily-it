package rafael.ninoles.parra.dailyit.ui.tasklist;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;
import java.util.List;

import rafael.ninoles.parra.dailyit.R;
import rafael.ninoles.parra.dailyit.ui.activities.TaskActivity;
import rafael.ninoles.parra.dailyit.databinding.FragmentTaskListBinding;
import rafael.ninoles.parra.dailyit.model.FirebaseContract;
import rafael.ninoles.parra.dailyit.model.MyDate;
import rafael.ninoles.parra.dailyit.model.Task;
import rafael.ninoles.parra.dailyit.ui.adapters.task.OnClickListenerDeleteTask;
import rafael.ninoles.parra.dailyit.ui.adapters.task.OnClickListenerOpenTask;
import rafael.ninoles.parra.dailyit.ui.adapters.task.TaskAdapter;
import rafael.ninoles.parra.dailyit.ui.tasks.TasksFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskListFragment extends Fragment {
    private static final String STATUS = "status";
    private static final String DATE = "date";
    private String status;
    private String rightStatus;
    private String leftStatus;
    private Date date;
    private TasksFragment tasksFragment;
    private TaskListViewModel viewModel;
    private TaskAdapter adapter;
    private FragmentTaskListBinding binding;
    private SwipeRefreshLayout swipeRefreshLayout;

    private final OnClickListenerDeleteTask deleteTask = new OnClickListenerDeleteTask() {
        @Override
        public void onItemClickDelete(Task task) {
            AlertDialog.Builder dialogo = new AlertDialog.Builder(getActivity());
            dialogo.setTitle(String.format(getString(R.string.delete_task), task.getTitle()));
            dialogo.setMessage(String.format(getString(R.string.confirm_delete_task), task.getTitle()));
            dialogo.setPositiveButton(android.R.string.yes
                    , (dialogInterface, i) -> {
                        // Qué hacemos en caso ok
                        viewModel.deleteTask(task);
                        adapter.notifyDataSetChanged();
                    });
            dialogo.setNegativeButton(android.R.string.no
                    , (dialogInterface, i) -> {
                    });
            dialogo.show();
        }
    };

    private final OnClickListenerOpenTask openTask = task -> {
        Intent intent = new Intent(this.getActivity(), TaskActivity.class);
        intent.putExtra(TaskActivity.EXTRA_TASK_ID, task.getId());
        startActivityForResult(intent, 0);
    };

    public void setTasksFragment(TasksFragment tasksFragment) {
        this.tasksFragment = tasksFragment;
    }

    public TaskListViewModel getViewModel() {
        return viewModel;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        try{
            swipeRefreshLayout.setRefreshing(true);
            this.date = date;
            viewModel.setDate(date);
            viewModel.updateTasks(date).observe(getViewLifecycleOwner(), taskList -> {
                adapter.setTasks(clearOldTaskes(taskList));
                adapter.notifyDataSetChanged();
                checkIfTaskes();
                swipeRefreshLayout.setRefreshing(false);
            });
        }catch (IllegalStateException e){
            e.printStackTrace();
        }
    }

    private void checkIfTaskes() {
        if (adapter.getTasks().size() > 0) {
            binding.tvNoTaskes.setVisibility(View.GONE);
        } else {
            binding.tvNoTaskes.setVisibility(View.VISIBLE);
        }
    }

    public TaskListFragment() {
        // Required empty public constructor
    }

    public static TaskListFragment newInstance(String status, MyDate date, TasksFragment tasksFragment) {
        TaskListFragment fragment = new TaskListFragment();
        fragment.setTasksFragment(tasksFragment);
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
            // TODO EXTRACT METHOD
            switch (status) {
                case FirebaseContract
                        .TaskEntry.TODO:
                    leftStatus = null;
                    rightStatus = FirebaseContract.TaskEntry.DOING;
                    break;
                case FirebaseContract.TaskEntry.DONE:
                    leftStatus = FirebaseContract.TaskEntry.DOING;
                    rightStatus = null;
                    break;
                default:
                    leftStatus = FirebaseContract.TaskEntry.TODO;
                    rightStatus = FirebaseContract.TaskEntry.DONE;
                    break;
            }
        }
    }

    private List<Task> clearOldTaskes(List<Task> taskes) {
        for (int i = taskes.size() - 1; i >= 0; i--) {
            Task actual = taskes.get(i);
            if (actual.getCreated().getTime() > date.getTime()) {
                taskes.remove(actual);
            }
        }
        return taskes;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new TaskListViewModel(this.requireActivity().getApplication(), status, date);
        binding = FragmentTaskListBinding.inflate(inflater, container, false);
        adapter = new TaskAdapter(this.getContext());
        adapter.setListenerDeleteTask(deleteTask);
        adapter.setListenerOpenTask(openTask);
        swipeRefreshLayout = binding.swipe;
        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.updateTasks(date).observe(getViewLifecycleOwner(), taskList -> {
            adapter.setTasks(clearOldTaskes(taskList));
            adapter.notifyDataSetChanged();
            checkIfTaskes();
            binding.pbTaskes.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }));
        binding.rvTasks.setAdapter(adapter);
        binding.rvTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT |
                        ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        if (swipeDir == ItemTouchHelper.RIGHT) {
                            if (rightStatus == null) {
                                adapter.notifyDataSetChanged();
                                return;
                            }
                            Task task = ((TaskAdapter.TaskViewHolder) viewHolder).getTask();
                            viewModel.updateStatus(task, rightStatus);
                            tasksFragment.moveDay(date);
                        } else {
                            if (leftStatus == null) {
                                adapter.notifyDataSetChanged();
                                return;
                            }
                            Task task = ((TaskAdapter.TaskViewHolder) viewHolder).getTask();
                            viewModel.updateStatus(task, leftStatus);
                            tasksFragment.moveDay(date);
                        }
                    }
                };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(binding.rvTasks);
        viewModel.getAllTasks().observe(getViewLifecycleOwner(), taskList -> {
            adapter.setTasks(clearOldTaskes(taskList));
            adapter.notifyDataSetChanged();
            binding.pbTaskes.setVisibility(View.GONE);
            checkIfTaskes();
        });
        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        swipeRefreshLayout.setRefreshing(true);
        tasksFragment.updateAllStatus();
    }
}