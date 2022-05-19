package rafael.ninoles.parra.dailyit.ui.adapters;

import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;

import rafael.ninoles.parra.dailyit.R;
import rafael.ninoles.parra.dailyit.model.Colors;
import rafael.ninoles.parra.dailyit.model.Task;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> tasks;
    private OnClickListenerDeleteTask listenerDeleteTask;

    public void setListenerDeleteTask(OnClickListenerDeleteTask listenerDeleteTask) {
        this.listenerDeleteTask = listenerDeleteTask;
    }

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
        private static final int LINES_NUMBER = 2;
        private static final int LINES_NUMBER_EXPANDED = 5;

        private Task task;
        private boolean viewMoreEnabled;
        private final TextView tvTitle;
        private final TextView tvDesc;
        private final TextView tvExpireDate;
        private final TextView tvExpires;
        private final TextView tvCategory;
        private final View categoryBar;
        private final ImageButton ibViewMore;
        private final ImageButton ibViewLess;
        private final ImageButton ibDelete;

        private String calExpires(){
            Date expire = task.getExpires();
            long diff = expire.getTime() - new Date().getTime();
            String remainingText = calcRemaining(diff);
            return remainingText ;
        }

        private String calcRemaining(long diff) {
            //TODO String.xml
            if(diff<0){
                return "Expired";
            }
            if(diff > MILIS_IN_DAY){
                long days = diff / MILIS_IN_DAY;
                if(days > 0){
                    return "Expires in " + days + " days";
                }else{
                    long hoursInMilis = diff % MILIS_IN_DAY;
                    long hours = hoursInMilis / MILIS_IN_HOUR;
                    return "Expires in "+hours+"h";
                }
            }
            // TODO pocho
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
            this.categoryBar.setBackgroundColor(Colors.getColor(task.getCategory().getColor()));
            this.tvCategory.setText(task.getCategory().getName());
            this.tvCategory.setBackgroundColor(Colors.getColor(task.getCategory().getColor()));
        }
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvTitle = itemView.findViewById(R.id.tvTitle);
            this.tvDesc = itemView.findViewById(R.id.tvDesc);
            this.tvExpires = itemView.findViewById(R.id.tvExpires);
            this.tvExpireDate = itemView.findViewById(R.id.tvExpireDate);
            this.categoryBar = itemView.findViewById(R.id.categoryBar);
            this.ibViewMore = itemView.findViewById(R.id.ibViewMore);
            this.ibDelete = itemView.findViewById(R.id.ibDelete);
            this.tvCategory = itemView.findViewById(R.id.tvCategory);
            this.viewMoreEnabled = false;
            ibViewLess = itemView.findViewById(R.id.ibViewLess);
            ibViewLess.setOnClickListener(e->{
                handleViewMore();
            });
            ibViewMore.setOnClickListener(e->{
                handleViewMore();
            });
            ibDelete.setOnClickListener(e->{
                listenerDeleteTask.onItemClickDelete(task);
            });
        }

        private void handleViewMore() {
            System.out.println("Click");
            if(viewMoreEnabled){
                tvDesc.setMaxLines(LINES_NUMBER);
                tvCategory.setVisibility(View.GONE);
                ibViewMore.setVisibility(View.VISIBLE);
                ibViewLess.setVisibility(View.GONE);
                ibDelete.setVisibility(View.GONE);
            }else{
                tvDesc.setMaxLines(LINES_NUMBER_EXPANDED);
                tvCategory.setVisibility(View.VISIBLE);
                ibViewMore.setVisibility(View.GONE);
                ibViewLess.setVisibility(View.VISIBLE);
                ibDelete.setVisibility(View.VISIBLE);
            }
            viewMoreEnabled = !viewMoreEnabled;
        }

        public Task getTask() {
            return task;
        }
    }
}
