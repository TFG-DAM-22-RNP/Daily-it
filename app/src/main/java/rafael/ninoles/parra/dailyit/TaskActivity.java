package rafael.ninoles.parra.dailyit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewTreeLifecycleOwner;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import rafael.ninoles.parra.dailyit.databinding.ActivityTaskBinding;
import rafael.ninoles.parra.dailyit.model.Colors;
import rafael.ninoles.parra.dailyit.model.FirebaseContract;
import rafael.ninoles.parra.dailyit.model.Task;
import rafael.ninoles.parra.dailyit.repository.DailyItRepository;

public class TaskActivity extends AppCompatActivity {
    public static final String EXTRA_TASK = "EXTRA_TASK";
    public static final String EXTRA_TASK_ID = "EXTRA_TASK_ID";
    private static final long MILIS_IN_DAY = 86400000;
    private ActivityTaskBinding binding;
    private boolean isNew;
    private String taskId;
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskBinding.inflate(getLayoutInflater());
        taskId = getIntent().getStringExtra(EXTRA_TASK_ID);
        setSupportActionBar(binding.tbTask);
        setContentView(binding.getRoot());
        setListeners();
        initTask();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_activity_menu, menu);
        return true;
    }


    private void setListeners() {
        binding.tvTaskExpireDate.setOnClickListener(e->{
            selectExpireDate();
        });
        binding.ivCalendar.setOnClickListener(e->{
            selectExpireDate();
        });
    }

    private void selectExpireDate() {
        //openContextMenu(this.ge);
    }

    private void initTask() {
        if(taskId == null){
            isNew = true;
            newTask();
        }else{
            isNew = false;
            getTask();
        }
    }

    private void getTask() {
        setTitle("Loading task...");
        DailyItRepository.getInstance().getTaskById(taskId).observe(this, new Observer<Task>() {
            @Override
            public void onChanged(Task taskReceived) {
                task = taskReceived;
                binding.pbMain.setVisibility(View.GONE);
                printTask();
            }
        });
    }

    private void printTask() {
        printDate();
        setTitle(task.getTitle());
        binding.etDescription.setText(task.getDescription());
        binding.etTaskName.setText(task.getTitle());
        binding.spCategory.setSelection(0);
    }

    private void printDate() {
        binding.tvTaskExpireDate.setText(new SimpleDateFormat("dd MMM yy").format(this.task.getExpires()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mi_save) {
            saveTask();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveTask() {
        binding.pbMain.setVisibility(View.VISIBLE);
        updateTaskValues();
        if(isNew){
            DailyItRepository.getInstance().createNewTask(task, FirebaseAuth.getInstance().getUid());
        }
    }

    private void updateTaskValues() {
        task.setTitle(binding.etTaskName.getText().toString());
        task.setDescription(binding.etDescription.getText().toString());
    }

    private void newTask() {
        //TODO STRING
        setTitle("New Task");
        binding.pbMain.setVisibility(View.GONE);
        this.task = new Task();
        Date creationDate = new Date();
        creationDate.setHours(0);
        creationDate.setMinutes(0);
        creationDate.setSeconds(0);
        this.task.setCreated(creationDate);
        this.task.setExpires(new Date(new Date().getTime() + MILIS_IN_DAY));
        this.task.setStatus(FirebaseContract.TaskEntry.TODO);
        printDate();
    }

    public void showDateTimePicker() {
        //TODO Revisar cantidad de Calendars
        Calendar date;
        final Calendar currentDate = Calendar.getInstance();
        TaskActivity context = this;
        date = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            date.set(year, monthOfYear, dayOfMonth);
            new TimePickerDialog(context, (view1, hourOfDay, minute) -> {
                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                date.set(Calendar.MINUTE, minute);
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

}