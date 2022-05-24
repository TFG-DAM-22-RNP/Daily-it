package rafael.ninoles.parra.dailyit.ui.tasklist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rafael.ninoles.parra.dailyit.model.MyDate;
import rafael.ninoles.parra.dailyit.model.Task;
import rafael.ninoles.parra.dailyit.repository.DailyItRepository;

public class TaskListViewModel extends AndroidViewModel {
    private LiveData<List<Task>> tasks;
    private MyDate date;
    private DailyItRepository dailyItRepository;
    private String status;

    public void setDate(MyDate date) {
        this.date = date;
    }

    public void deleteTask(Task task){
        dailyItRepository.deleteTask(task);
        tasks.getValue().remove(task);
    }

    public TaskListViewModel(@NonNull Application application, String status, MyDate date) {
        super(application);
        this.date = date;

        dailyItRepository = DailyItRepository.getInstance();
        this.status = status;
        tasks = dailyItRepository.getTaskByStatus(status,date);
    }

    public LiveData<List<Task>> updateTasks(MyDate date){
        this.date = date;
        tasks = dailyItRepository.getTaskByStatus(status,date);
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