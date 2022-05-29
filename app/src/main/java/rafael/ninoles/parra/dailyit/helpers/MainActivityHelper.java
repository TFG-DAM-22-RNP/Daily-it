package rafael.ninoles.parra.dailyit.helpers;

import rafael.ninoles.parra.dailyit.ui.tasks.TasksFragment;

public class MainActivityHelper {
    private static TasksFragment tasksFragment;

    public static TasksFragment getTasksFragment() {
        return tasksFragment;
    }

    public static void setTasksFragment(TasksFragment tasksFragment) {
        MainActivityHelper.tasksFragment = tasksFragment;
    }
}
