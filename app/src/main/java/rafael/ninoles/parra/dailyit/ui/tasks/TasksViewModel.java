package rafael.ninoles.parra.dailyit.ui.tasks;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import rafael.ninoles.parra.dailyit.model.Task;
import rafael.ninoles.parra.dailyit.repository.DailyItRepository;

public class TasksViewModel extends ViewModel {

    private MutableLiveData<List<Task>> tasks;
    private DailyItRepository dailyItRepository;

    public TasksViewModel() {
        dailyItRepository = DailyItRepository.getInstance();
    }
}
