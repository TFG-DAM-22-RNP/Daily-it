package rafael.ninoles.parra.dailyit.helpers;

import rafael.ninoles.parra.dailyit.ui.categories.CategoriesFragment;
import rafael.ninoles.parra.dailyit.ui.tasks.TasksFragment;

/**
 * Helper task to get references to actual fragments
 */
public class MainActivityHelper {
    private static TasksFragment tasksFragment;
    private static CategoriesFragment categoriesFragment;

    public static CategoriesFragment getCategoriesFragment() {
        return categoriesFragment;
    }

    public static void setCategoriesFragment(CategoriesFragment categoriesFragment) {
        MainActivityHelper.categoriesFragment = categoriesFragment;
    }

    public static TasksFragment getTasksFragment() {
        return tasksFragment;
    }

    public static void setTasksFragment(TasksFragment tasksFragment) {
        MainActivityHelper.tasksFragment = tasksFragment;
    }
}
