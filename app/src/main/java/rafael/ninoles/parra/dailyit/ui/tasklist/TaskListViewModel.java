package rafael.ninoles.parra.dailyit.ui.tasklist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import rafael.ninoles.parra.dailyit.model.Task;

public class TaskListViewModel extends ViewModel {

    private MutableLiveData<List<Task>> tasks;
    private String status;

    public LiveData<List<Task>> getTask() {
        return tasks;
    }

    public TaskListViewModel(String status) {
        tasks = new MutableLiveData<>();
        this.status = status;
    }
}