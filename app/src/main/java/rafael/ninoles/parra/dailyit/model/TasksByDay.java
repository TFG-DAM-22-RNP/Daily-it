package rafael.ninoles.parra.dailyit.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TasksByDay {
    private final String day;
    private final List<Task> taskList;

    public String getDay() {
        return day;
    }

    public TasksByDay(String day) {
        this.day = day;
        this.taskList = new ArrayList<>();
    }

    public void addTask(Task task){
        this.taskList.add(task);
    }

    public List<Task> getTaskList() {
        return new ArrayList<>(taskList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TasksByDay that = (TasksByDay) o;
        return day.equals(that.day);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day);
    }
}
