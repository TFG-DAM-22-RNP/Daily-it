package rafael.ninoles.parra.dailyit.ui.adapters;

import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;

import rafael.ninoles.parra.dailyit.R;
import rafael.ninoles.parra.dailyit.model.Task;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> tasks;

    public TaskAdapter(){
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public TaskAdapter(List<Task> tasks) {
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task,parent,false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        if (tasks != null) {
            final Task task = tasks.get(position);
            holder.setTask(task);
        }
    }

    @Override
    public int getItemCount() {
        if(tasks!=null){
            return tasks.size();
        }
        return 0;
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        private static final long MILIS_IN_DAY = 1000*60*60*24;
        private static final long MILIS_IN_HOUR = 1000*60*60;
        private static final long MILIS_IN_MIN = 60000;

        private Task task;
        private final TextView tvTitle;
        private final TextView tvDesc;
        private final TextView tvExpireDate;
        private final TextView tvExpires;
        private final View categoryBar;

        private String calExpires(){
            Date expire = task.getExpires();
            long diff = expire.getTime() - new Date().getTime();
            String remainingText = calcRemaining(diff);
            return remainingText ;
        }

        private String calcRemaining(long diff) {
            if(diff<0){
                return "Expired";
            }
            if(diff > MILIS_IN_DAY){
                long days = diff / MILIS_IN_DAY;
                return "Expires in " + days + " days";
            }
            return "";
        }

        private long milisToHours(long milis){
            return milis/1000/60/60;
        }

        public void setTask(Task task) {
            this.task = task;
            this.tvTitle.setText(task.getTitle());
            this.tvDesc.setText(task.getDescription());
            this.tvExpires.setText(calExpires());
            this.tvExpireDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(task.getExpires()));
            this.categoryBar.setBackgroundColor(Color.RED);
        }

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvTitle = itemView.findViewById(R.id.tvTitle);
            this.tvDesc = itemView.findViewById(R.id.tvDesc);
            this.tvExpires = itemView.findViewById(R.id.tvExpires);
            this.tvExpireDate = itemView.findViewById(R.id.tvExpireDate);
            this.categoryBar = itemView.findViewById(R.id.categoryBar);
        }
        public Task getTask() {
            return task;
        }
    }
}
