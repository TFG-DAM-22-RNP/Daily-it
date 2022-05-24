package rafael.ninoles.parra.dailyit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import rafael.ninoles.parra.dailyit.databinding.ActivityMainBinding;
import rafael.ninoles.parra.dailyit.databinding.ActivityTaskBinding;
import rafael.ninoles.parra.dailyit.model.Colors;
import rafael.ninoles.parra.dailyit.model.Task;

public class TaskActivity extends AppCompatActivity {
    public static final String EXTRA_TASK = "EXTRA_TASK";
    private ActivityTaskBinding binding;
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        this.task = getIntent().getParcelableExtra(EXTRA_TASK);
        initTask();
    }

    private void initTask() {
        if(task == null){
            System.out.println("ES NULL");
            newTask();
        }else{
            printTask();
        }
    }

    private void printTask() {
        System.out.println("PRINTEANDO TASK");
        System.out.println(task.getTitle());
        binding.etDescription.setText(task.getDescription());
        System.out.println("EL TEXTO");
        System.out.println(binding.etDescription.getText());
        binding.etTaskName.setText(task.getTitle());
        binding.tvTaskCategory.setText(task.getCategory().getName());
        binding.tvTaskCategory.setBackgroundColor(Colors.getColor(task.getCategory().getColor()));
    }

    private void newTask() {
    }
}