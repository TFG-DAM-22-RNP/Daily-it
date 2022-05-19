package rafael.ninoles.parra.dailyit.model;

public class FirebaseContract {
    public static class UserEntry{
        public static final String COLLECTION_NAME = "users";
        public static final String AUTH_UID = "AuthUID";
        public static final String IMG_PROFILE = "imgProfile";
        public static final String NAME = "name";
        public static final String TASKS = "tasks";
    }

    public static class  TaskEntry{
        public static final String COLLECTION_NAME = "tasks";
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String CREATED = "created";
        public static final String EXPIRES = "expires";
        public static final String STATUS = "status";
        public static final String CATEGORY = "categoryId";
        public static final String TODO = "ToDo";
        public static final String DOING = "Doing";
        public static final String DONE = "Done";
    }

    public static class CategoryEntry{
        public static final String COLLECTION_NAME = "categories";
        public static final String COLOR = "color";
        public static final String ID = "id";
        public static final String NAME = "name";
    }
}
