package rafael.ninoles.parra.dailyit.ui.stats;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import rafael.ninoles.parra.dailyit.R;
import rafael.ninoles.parra.dailyit.databinding.FragmentStatsBinding;
import rafael.ninoles.parra.dailyit.databinding.FragmentTaskListBinding;
import rafael.ninoles.parra.dailyit.model.FirebaseContract;
import rafael.ninoles.parra.dailyit.model.Task;
import rafael.ninoles.parra.dailyit.model.TasksByDay;
import rafael.ninoles.parra.dailyit.repository.DailyItRepository;
import rafael.ninoles.parra.dailyit.utilities.Colors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatsFragment extends Fragment {
    private static final long MILIS_IN_WEEK = 604800000;
    private FragmentStatsBinding binding;
    private BarChart barChart;
    private int doneCuantity;
    private int doingCuantity;
    private int toDoCuantity;
    private int totalCuantity;

    public StatsFragment() {
        // Required empty public constructor
    }

    public static StatsFragment newInstance() {
        StatsFragment fragment = new StatsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStatsBinding.inflate(inflater, container, false);
        barChart = binding.chart;
        doneCuantity = 0;
        doingCuantity = 0;
        toDoCuantity = 0;
        totalCuantity = 0;
        DailyItRepository.getInstance().getAllTasks().observe(this, tasks -> {
            calcData(tasks);
            addDataToChart(getLastWeek(tasks));
        });
        View root = binding.getRoot();
        return root;
    }

    private void calcData(List<Task> tasks) {
        for(Task task : tasks){
            totalCuantity++;
            switch (task.getStatus()){
                case FirebaseContract
                        .TaskEntry.DONE:
                    doneCuantity++;
                    break;
                case FirebaseContract.TaskEntry.DOING:
                    doingCuantity++;
                    break;
                default:
                    toDoCuantity++;
                    break;
            }
        }
        binding.tvDoing.setText(String.format(getContext().getString(R.string.total_doing),doingCuantity));
        binding.tvToDo.setText(String.format(getContext().getString(R.string.total_todo),toDoCuantity));
        binding.tvDone.setText(String.format(getContext().getString(R.string.total_done),doneCuantity));
        binding.tvTotalTaskes.setText(String.format(getContext().getString(R.string.total_task_count),totalCuantity));
    }

    private List<Task> getLastWeek(List<Task> tasks) {
        Date minDate = new Date();
        minDate.setTime(minDate.getTime() - MILIS_IN_WEEK);
        List<Task> result = tasks.stream().filter(task -> task.getCreated().getTime() > minDate.getTime()).collect(Collectors.toList());
        result.sort(Comparator.comparing(Task::getCreated));
        return result;
    }

    private String formatDay(Task task){
        return new SimpleDateFormat("dd/MM/yy").format(task.getCreated());
    }

    private void addDataToChart(List<Task> tasks) {
        if(tasks.size()<1){
            binding.tvNoData.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
            return;
        }
        ArrayList<BarEntry> entries = new ArrayList<>();
        String title = getString(R.string.dia);
        TasksByDay actualDay = new TasksByDay(formatDay(tasks.get(0)));
        List<TasksByDay> tasksByDays = new ArrayList<>();
        for(Task task : tasks){
            if(!formatDay(task).equals(actualDay.getDay())){
                tasksByDays.add(actualDay);
                actualDay = new TasksByDay(formatDay(task));
            }
            actualDay.addTask(task);
        }
        if(tasksByDays.size()<2){
            binding.tvNoData.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
            return;
        }
        //fit the data into a bar
        float count = 0;
        for(TasksByDay actual : tasksByDays){
            BarEntry barEntry = new BarEntry(count, new Integer(actual.getTaskList().size()).floatValue());
            count+=1;
            entries.add(barEntry);
        }

        BarDataSet barDataSet = new BarDataSet(entries, title);
        barDataSet.setColor(Colors.getColor("green"));
        barDataSet.setFormSize(15f);
        barDataSet.setValueTextSize(12f);
        BarData data = new BarData(barDataSet);
        barChart.setData(data);
        customizeBarChart();
        barChart.invalidate();
        binding.linearChart.setBackgroundColor(Color.WHITE);
        barChart.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }

    private void customizeBarChart() {
        barChart.animateY(1000);
        barChart.getDescription().setEnabled(false);
        barChart.setBackgroundColor(Color.WHITE);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawGridLines(false);
    }
}