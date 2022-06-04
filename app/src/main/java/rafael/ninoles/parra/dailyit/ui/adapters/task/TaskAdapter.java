package rafael.ninoles.parra.dailyit.ui.adapters.task;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import rafael.ninoles.parra.dailyit.R;
import rafael.ninoles.parra.dailyit.utilities.Colors;
import rafael.ninoles.parra.dailyit.model.Task;

/**
 * Adapter to use in recycler views to print tasks
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private final Context context;
    private List<Task> tasks;
    private OnClickListenerDeleteTask listenerDeleteTask;
    private OnClickListenerOpenTask listenerOpenTask;

    public void setListenerDeleteTask(OnClickListenerDeleteTask listenerDeleteTask) {
        this.listenerDeleteTask = listenerDeleteTask;
    }

    public void setListenerOpenTask(OnClickListenerOpenTask listenerOpenTask) {
        this.listenerOpenTask = listenerOpenTask;
    }

    public TaskAdapter(Context context) {
        this.context = context;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
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
        if (tasks != null) {
            return tasks.size();
        }
        return 0;
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        private static final long MILIS_IN_HOUR = 1000 * 60 * 60;
        private static final long TWO_HOURS_IN_MILIS = MILIS_IN_HOUR * 2;
        private static final int LINES_NUMBER = 2;
        private static final int LINES_NUMBER_EXPANDED = 5;
        private static final long MILIS_IN_DAY = 1000 * 60 * 60 * 24;

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
        private final MaterialCardView cardView;

        private String calExpires() {
            Date expire = task.getExpires();
            long diff = expire.getTime() - new Date().getTime();
            String remainingText = calcRemaining(diff);
            return remainingText;
        }

        private String calcRemaining(long diff) {
            diff += TWO_HOURS_IN_MILIS;
            if (diff < 0) {
                return context.getString(R.string.expired);
            }
            if (diff > MILIS_IN_DAY) {
                long days = diff / MILIS_IN_DAY;
                if (days > 0) {
                    return String.format(context.getString(R.string.expires_in_days), days);
                }
            } else {
                if (diff > MILIS_IN_HOUR) {
                    long hoursInMilis = diff % MILIS_IN_DAY;
                    long hours = hoursInMilis / MILIS_IN_HOUR;
                    return String.format(context.getString(R.string.expires_in_hours), hours);
                } else {
                    long hoursInMilis = diff % MILIS_IN_HOUR;
                    long minutes = hoursInMilis / 1000 / 60;
                    return String.format(context.getString(R.string.expires_in_minutes), minutes);
                }
            }
            return "";
        }

        private long milisToHours(long milis) {
            return milis / 1000 / 60 / 60;
        }

        /**
         * Set the task and print the data
         *
         * @param task
         */
        public void setTask(Task task) {
            this.task = task;
            this.tvTitle.setText(task.getTitle());
            if (task.getDescription() == null || task.getDescription().isEmpty()) {
                this.tvDesc.setText(context.getString(R.string.no_description));
            } else {
                this.tvDesc.setText(task.getDescription());
            }
            this.tvExpires.setText(calExpires());
            this.tvExpireDate.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(task.getExpires().getTime() + TWO_HOURS_IN_MILIS));
            this.categoryBar.setBackgroundColor(Colors.getColor(task.getCategory().getColor()));
            if (task.getCategory().getName().equals("Work")) {
                this.tvCategory.setText(context.getString(R.string.work));
            } else {
                this.tvCategory.setText(task.getCategory().getName());
            }
            this.tvCategory.setBackgroundColor(Colors.getColor(task.getCategory().getColor()));
        }

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvTitle = itemView.findViewById(R.id.tvTitle);
            this.tvDesc = itemView.findViewById(R.id.tvDesc);
            this.tvExpires = itemView.findViewById(R.id.tvExpires);
            this.tvExpireDate = itemView.findViewById(R.id.tvExpireDate);
            this.categoryBar = itemView.findViewById(R.id.categoryColor);
            this.ibViewMore = itemView.findViewById(R.id.ibViewMore);
            this.ibDelete = itemView.findViewById(R.id.ibDelete);
            this.tvCategory = itemView.findViewById(R.id.tvCategory);
            this.viewMoreEnabled = false;
            this.cardView = itemView.findViewById(R.id.cardView);
            ibViewLess = itemView.findViewById(R.id.ibViewLess);
            cardView.setOnClickListener(e -> {
                listenerOpenTask.onItemClickOpen(task);
            });
            ibViewLess.setOnClickListener(e -> {
                handleViewMore();
            });
            ibViewMore.setOnClickListener(e -> {
                handleViewMore();
            });
            ibDelete.setOnClickListener(e -> {
                listenerDeleteTask.onItemClickDelete(task);
            });

        }

        /**
         * Handle the click in the view more icon, hiding and showing some buttons
         */
        private void handleViewMore() {
            if (viewMoreEnabled) {
                tvDesc.setMaxLines(LINES_NUMBER);
                tvCategory.setVisibility(View.GONE);
                ibViewMore.setVisibility(View.VISIBLE);
                ibViewLess.setVisibility(View.INVISIBLE);
            } else {
                tvDesc.setMaxLines(LINES_NUMBER_EXPANDED);
                tvCategory.setVisibility(View.VISIBLE);
                ibViewMore.setVisibility(View.INVISIBLE);
                ibViewLess.setVisibility(View.VISIBLE);
            }
            viewMoreEnabled = !viewMoreEnabled;
        }

        public Task getTask() {
            return task;
        }
    }
}
