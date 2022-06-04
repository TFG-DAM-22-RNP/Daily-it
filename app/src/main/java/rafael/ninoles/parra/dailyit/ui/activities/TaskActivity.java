package rafael.ninoles.parra.dailyit.ui.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import rafael.ninoles.parra.dailyit.R;
import rafael.ninoles.parra.dailyit.databinding.ActivityTaskBinding;
import rafael.ninoles.parra.dailyit.exceptions.InputNeededException;
import rafael.ninoles.parra.dailyit.model.Category;
import rafael.ninoles.parra.dailyit.model.FirebaseContract;
import rafael.ninoles.parra.dailyit.model.Task;
import rafael.ninoles.parra.dailyit.repository.DailyItRepository;
import rafael.ninoles.parra.dailyit.utilities.FirestoreUserTranslator;

public class TaskActivity extends AppCompatActivity {
    public static final String EXTRA_TASK = "EXTRA_TASK";
    public static final String EXTRA_TASK_ID = "EXTRA_TASK_ID";
    private static final long MILIS_IN_DAY = 86400000;
    private static final long MILIS_IN_TWO_HOURS = 7200000;
    private static final int NOT_SAVED = 5;
    private static final int FIVE_MINS_IN_MILIS = 300000;
    public static final int TODO = 0;
    public static final int DOING = 1;
    public static final int DONE = 2;
    Map<String, Category> availableCategories = new HashMap<>();
    private ActivityTaskBinding binding;
    private boolean isNew;
    private boolean canSave;
    private Task compareTask;
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
        canSave = true;
        initTask();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_activity_menu, menu);
        return true;
    }

    private void setListeners() {
        binding.tvTaskExpireDate.setOnClickListener(e->{
            pickDateTime();
        });
        binding.ivCalendar.setOnClickListener(e->{
            pickDateTime();
        });
    }

    private void initTask() {
        if (taskId == null) {
            isNew = true;
            newTask();
        } else {
            isNew = false;
            getTask();
        }
    }

    private void getTask() {
        setTitle(getString(R.string.loadin_task));
        TaskActivity owner = this;
        DailyItRepository.getInstance().getTaskById(taskId).observe(owner, taskReceived -> {
            task = taskReceived;
            System.out.println("MI CATEGORIA ES "+taskReceived.getCategoryId());
            DailyItRepository.getInstance().getCategoriesFromUser(FirebaseAuth.getInstance().getUid())
                    .observe(owner, categories -> {
                        for (Category category : categories) {
                            System.out.println("CATEGORIA: "+category);
                            if(category.getName().equals("Work")){
                                availableCategories.put(getString(R.string.work),category);
                                continue;
                            }
                            availableCategories.put(category.getName(), category);
                        }
                        binding.pbMain.setVisibility(View.GONE);
                        compareTask = new Task(task);
                        printTask();
                    });
        });
    }

    private void printTask() {
        printDate();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.simple_spinner_dropdown_item,
                R.id.tvItemSpinner, availableCategories.keySet().toArray(new String[0]));
        binding.spCategory.setAdapter(adapter);
        if(task.getTitle()==null){
            setTitle(getString(R.string.new_task));
        }else{
            setTitle(task.getTitle());
        }
        printStatus();
        printCategories(adapter);
        binding.etDescription.setText(task.getDescription());
        binding.etTaskName.setText(task.getTitle());
    }

    private void printStatus() {
        //TODO constantes para numeros
        if(task.getStatus().equals(FirebaseContract.TaskEntry.TODO)){
            binding.spStatus.setSelection(0);
        }else if(task.getStatus().equals(FirebaseContract.TaskEntry.DOING)){
            binding.spStatus.setSelection(1);
        }else{
            binding.spStatus.setSelection(2);
        }
    }

    private void printCategories(ArrayAdapter<String> adapter){
        // TODO
        System.out.println("BUSCANDO "+task.getCategoryId());
        for(Category category:availableCategories.values()){
            if(category.getId().equals(task.getCategoryId())){
                binding.spCategory.setSelection(adapter.getPosition(category.getName()));
                System.out.println(category.getName());
                System.out.println("PONIENDO NUEVO ID");
                System.out.println(category.getId());
                task.setCategoryId(category.getId());
                compareTask.setCategoryId(category.getId());
                return;
            }
        }
        binding.spCategory.setSelection(0);
    }

    private void printDate() {
        if(this.task.getExpires() == null){
            binding.tvTaskExpireDate.setText(getString(R.string.no_date_selected));
        }else{
            binding.tvTaskExpireDate.setText(new SimpleDateFormat("dd MMM yy, HH:mm").format(this.task.getExpires().getTime()+MILIS_IN_TWO_HOURS));
        }
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
        if(!canSave){
            return;
        }
        try {
            checkTaskInputs();
        } catch (InputNeededException e) {
            Toast toast = Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG);
            toast.show();
            e.printStackTrace();
            return;
        }
        binding.pbMain.setVisibility(View.VISIBLE);
        updateTaskValues();
        if (isNew) {
            canSave = false;
            DailyItRepository.getInstance().createNewTask(task, FirebaseAuth.getInstance().getUid())
            .observe(this, id -> {
                taskId = id;
                canSave = true;
            });
        } else {
            DailyItRepository.getInstance().updateTask(task, FirebaseAuth.getInstance().getUid());
        }
        binding.pbMain.setVisibility(View.GONE);
        Toast toast = Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT);
        toast.show();
        isNew = false;
        compareTask = new Task(task);
        setTitle(task.getTitle());
        setResult(binding.spStatus.getSelectedItemPosition());
        finish();

    }

    private void checkTaskInputs() throws InputNeededException {
        if(binding.etTaskName.getText().toString().isEmpty()){
            throw new InputNeededException(getString(R.string.name_empty_exception_message));
        }
        if(task.getExpires()==null){
            throw new InputNeededException(getString(R.string.no_expire_time));
        }
        Date inFiveMins = new Date();
        inFiveMins.setTime(inFiveMins.getTime() + FIVE_MINS_IN_MILIS);
        System.out.println("LA TAREA");
        System.out.println(new SimpleDateFormat("dd MM yy HH:mm").format(task.getExpires().getTime()+MILIS_IN_TWO_HOURS));
        System.out.println("CINCO MINS");
        System.out.println(new SimpleDateFormat("dd MM yy HH:mm").format(inFiveMins));
        if(task.getExpires().getTime()+MILIS_IN_TWO_HOURS< inFiveMins.getTime()){
            throw new InputNeededException(getString(R.string.fast_expire_time));
        }
    }

    private void updateTaskValues() {
        task.setTitle(binding.etTaskName.getText().toString());
        task.setStatus(FirestoreUserTranslator.getFirestoreWord(binding.spStatus.getSelectedItem().toString()));
        task.setCategoryId(availableCategories.get(binding.spCategory.getSelectedItem().toString()).getId());
        task.setDescription(binding.etDescription.getText().toString());
    }

    private void newTask() {
        //TODO STRING
        TaskActivity owner = this;
        setTitle(getString(R.string.new_task));
        this.task = new Task();
        Date creationDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(creationDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 1);
        this.task.setCreated(calendar.getTime());
        //TODO Borrar cuando funcione
        //this.task.setExpires(new Date(new Date().getTime() + MILIS_IN_DAY));
        this.task.setStatus(FirebaseContract.TaskEntry.TODO);
        DailyItRepository.getInstance().getCategoriesFromUser(FirebaseAuth.getInstance().getUid())
                .observe(owner, categories -> {
                    for (Category category : categories) {
                        //TODO REFACTOR CTRL C CTRL V
                        if(category.getName().equals("Work")){
                            availableCategories.put(getString(R.string.work),category);
                            continue;
                        }
                        availableCategories.put(category.getName(), category);
                    }
                    binding.pbMain.setVisibility(View.GONE);
                    printTask();
                });
    }

    public void pickDateTime() {
        //TODO Revisar cantidad de Calendars
        Calendar date = Calendar.getInstance();
        final Calendar currentDate = Calendar.getInstance();
        TaskActivity context = this;
        new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            date.set(year, monthOfYear, dayOfMonth);
            new TimePickerDialog(context, (view1, hourOfDay, minute) -> {
                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                date.set(Calendar.MINUTE, minute);
                this.task.setExpires(new Date(date.getTimeInMillis()-MILIS_IN_TWO_HOURS));
                printDate();
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    @Override
    public void onBackPressed() {
        updateTaskValues();
        if (!isNew && (compareTask != null && task.isTheSame(compareTask))) {
            setResult(0);
            finish();
        } else {
            AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
            //TODO STRINGS
            if(task.getTitle() == null || task.getTitle().isEmpty()){
                dialogo.setTitle(getString(R.string.exit_without_saving_new));
            }else{
                dialogo.setTitle(String.format(getString(R.string.exit_without_saving), task.getTitle()));
            }
            dialogo.setMessage(getString(R.string.changes_without_saving));
            dialogo.setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                //TODO CONSTANTE
                setResult(NOT_SAVED);
                finish();
            });
            dialogo.setNegativeButton(android.R.string.no, (dialogInterface, i) -> {
            });
            dialogo.show();
        }
    }

}