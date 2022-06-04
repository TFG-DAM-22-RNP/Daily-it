package rafael.ninoles.parra.dailyit.ui.tasklist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import rafael.ninoles.parra.dailyit.model.Task;
import rafael.ninoles.parra.dailyit.repository.DailyItRepository;

/**
 * ViewModel for the TaskListFragment
 */
public class TaskListViewModel extends AndroidViewModel {
    private final DailyItRepository dailyItRepository;
    private final String status;
    private LiveData<List<Task>> tasks;
    private Date date;

    public void setDate(Date date) {
        this.date = date;
    }

    public void deleteTask(Task task) {
        dailyItRepository.deleteTask(task);
        tasks.getValue().remove(task);
    }

    public TaskListViewModel(@NonNull Application application, String status, Date date) {
        super(application);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 1);
        this.date = calendar.getTime();

        dailyItRepository = DailyItRepository.getInstance();
        this.status = status;
        tasks = dailyItRepository.getTaskByStatus(status, this.date);
    }

    public LiveData<List<Task>> updateTasks(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 1);
        this.date = calendar.getTime();
        tasks = dailyItRepository.getTaskByStatus(status, this.date);
        return tasks;
    }

    public LiveData<List<Task>> getAllTasks() {
        return tasks;
    }

    public void updateStatus(Task task, String rightStatus) {
        dailyItRepository.updateTaskStatus(task, rightStatus);
        updateTasks(date);
    }
}