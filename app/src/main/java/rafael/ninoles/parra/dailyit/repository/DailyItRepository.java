package rafael.ninoles.parra.dailyit.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import rafael.ninoles.parra.dailyit.model.Category;
import rafael.ninoles.parra.dailyit.model.FirebaseContract;
import rafael.ninoles.parra.dailyit.enums.RequestStatus;
import rafael.ninoles.parra.dailyit.model.Task;
import rafael.ninoles.parra.dailyit.model.User;

public class DailyItRepository {
    private static final String LOG_TAG = "DailyItRespository";
    private static volatile DailyItRepository INSTANCE;
    private static final long MILIS_IN_HOUR = 1000*60*60;
    private static final long TWO_HOURS_IN_MILIS = MILIS_IN_HOUR*2;
    private static final String DEFAULT_CATEGORY_ID = "Work";
    private static final String DEFAULT_CATEGORY_COLOR = "grey";
    private static final String DEFAULT_CATEGORY_NAME = "Work";

    public static DailyItRepository getInstance(){
        if(INSTANCE == null){
            synchronized (DailyItRepository.class){
                if(INSTANCE == null){
                    INSTANCE = new DailyItRepository();
                }
            }
        }
        return INSTANCE;
    }

    public MutableLiveData<List<Task>> getAllTasks(){
        CollectionReference colRef = FirebaseFirestore.getInstance().collection(FirebaseContract.UserEntry.COLLECTION_NAME).document(FirebaseAuth.getInstance().getUid())
                .collection(FirebaseContract.TaskEntry.COLLECTION_NAME);
        return getTasksFromQuery(colRef);
    }

    public MutableLiveData<List<Task>> getTaskByStatus(String status, Date date){
        Log.i(LOG_TAG, "Getting tasks starting "+new SimpleDateFormat("dd MM yy HH:mm:ss").format(date));
        date.setTime(date.getTime() + TWO_HOURS_IN_MILIS );
        Query colRef = FirebaseFirestore.getInstance().collection(FirebaseContract.UserEntry.COLLECTION_NAME).document(FirebaseAuth.getInstance().getUid())
                .collection(FirebaseContract.TaskEntry.COLLECTION_NAME).whereEqualTo(FirebaseContract.TaskEntry.STATUS,status)
                .whereGreaterThanOrEqualTo(FirebaseContract.TaskEntry.EXPIRES, date);
        return getTasksFromQuery(colRef);
    }

    public MutableLiveData<Task> getTaskById(String id){
        MutableLiveData<Task> result = new MutableLiveData<>();
        DocumentReference taskRef = FirebaseFirestore.getInstance().collection(FirebaseContract.UserEntry.COLLECTION_NAME).document(FirebaseAuth.getInstance().getUid())
                .collection(FirebaseContract.TaskEntry.COLLECTION_NAME).document(id);
        taskRef.get().addOnCompleteListener(taskResult -> {
            result.setValue(taskResult.getResult().toObject(Task.class));
        });
        return result;
    }

    public MutableLiveData<List<Category>> getCategoriesFromUser(String uid){
        MutableLiveData<List<Category>> result = new MutableLiveData<>();
        CollectionReference colRef = FirebaseFirestore.getInstance().collection(FirebaseContract.UserEntry.COLLECTION_NAME).document(uid)
                .collection(FirebaseContract.CategoryEntry.COLLECTION_NAME);
        final List<Category> categoryList = new ArrayList<>();
        colRef.get().addOnCompleteListener(categoryResult -> {
            for(DocumentSnapshot snapshot : categoryResult.getResult()){
                Category category = snapshot.toObject(Category.class);
                categoryList.add(category);
            }
            result.setValue(categoryList);
        });
        return result;
    }

    @NonNull
    private MutableLiveData<List<Task>> getTasksFromQuery(Query colRef) {
        AtomicBoolean gotCategories = new AtomicBoolean(false);
        AtomicBoolean gotTaskes = new AtomicBoolean(false);
        MutableLiveData<List<Task>> tasks = new MutableLiveData<>();
        final List<Task>[] taskList = new List[]{new ArrayList<>()};
        Map<String, Category> categories = new HashMap<>();
        FirebaseFirestore.getInstance().collection(FirebaseContract.UserEntry.COLLECTION_NAME).document(FirebaseAuth.getInstance().getUid())
                .collection(FirebaseContract.CategoryEntry.COLLECTION_NAME).get().addOnCompleteListener(categoryResult->{
                for(DocumentSnapshot snap : categoryResult.getResult()){
                    Category category = snap.toObject(Category.class);
                    categories.put(category.getId(), category);
                }
                gotCategories.set(true);
                if(gotCategories.get() && gotTaskes.get()){
                    tasks.setValue(joinTasksWithCategories(categories, taskList[0]));
                }
        });
        colRef.get().addOnCompleteListener(result -> {
            for(DocumentSnapshot snap : result.getResult().getDocuments()){
                Task task = snap.toObject(Task.class);
                taskList[0].add(task);
            }
            //tasks.setValue(taskList);
            gotTaskes.set(true);
            if(gotCategories.get() && gotTaskes.get()){
                tasks.setValue(joinTasksWithCategories(categories, taskList[0]));
            }
        });
        return tasks;
    }

    private List<Task> joinTasksWithCategories(Map<String, Category> categories, List<Task> tasks) {
        List<Task> result = new ArrayList<>();
        for(Task task : tasks){
            task.setCategory(categories.get(task.getCategoryId()));
            result.add(task);
        }
        return result;
    }


    public void deleteTask(Task task) {
        // TODO implementar
       FirebaseFirestore.getInstance().collection(FirebaseContract.UserEntry.COLLECTION_NAME).document(FirebaseAuth.getInstance().getUid())
               .collection(FirebaseContract.TaskEntry.COLLECTION_NAME).document(task.getId()).delete();
    }

    public void updateTaskStatus(Task task, String rightStatus) {
        task.setStatus(rightStatus);
        updateTask(task, FirebaseAuth.getInstance().getUid());
    }

    public MutableLiveData<String> createNewTask(Task task, String uid) {
        MutableLiveData<String> result = new MutableLiveData<>();
        task.getCreated().setHours(0);
        task.getCreated().setMinutes(0);
        task.getCreated().setSeconds(0);
        DocumentReference docRef = FirebaseFirestore.getInstance().collection(FirebaseContract.UserEntry.COLLECTION_NAME)
                .document(uid).collection(FirebaseContract.TaskEntry.COLLECTION_NAME).document();
        String id = docRef.getId();
        task.setId(id);
        FirebaseFirestore.getInstance().collection(FirebaseContract.UserEntry.COLLECTION_NAME).document(FirebaseAuth.getInstance().getUid())
                .collection(FirebaseContract.TaskEntry.COLLECTION_NAME).document(id).set(task)
                .addOnCompleteListener(task1 -> result.setValue(id));
        return result;
    }

    public void updateTask(Task task, String uid) {
        FirebaseFirestore instance = FirebaseFirestore.getInstance();
        DocumentReference docRef = instance.collection(FirebaseContract.UserEntry.COLLECTION_NAME).document(uid)
                .collection(FirebaseContract.TaskEntry.COLLECTION_NAME).document(task.getId());
        docRef.set(task);
    }

    public void createUser(String uid, String email) {
        User newUser = new User();
        newUser.setId(uid);
        newUser.setName(email.split("@")[0]);
        newUser.setEmail(email);
        Category defaultCategory = new Category(DEFAULT_CATEGORY_ID,DEFAULT_CATEGORY_COLOR,DEFAULT_CATEGORY_NAME);
        defaultCategory.setDeleteable(false);
        FirebaseFirestore.getInstance().collection(FirebaseContract.UserEntry.COLLECTION_NAME).document(uid).set(newUser);
        FirebaseFirestore.getInstance().collection(FirebaseContract.UserEntry.COLLECTION_NAME).document(uid)
                .collection(FirebaseContract.CategoryEntry.COLLECTION_NAME).document(DEFAULT_CATEGORY_ID).set(defaultCategory);
    }

    public MutableLiveData<RequestStatus> createCategory(Category category, String uid) {
        //TODO MIRAR POSIBLES PROBLEMAS EN COMPLETE LISTNER
        MutableLiveData<RequestStatus> result = new MutableLiveData<>(RequestStatus.FETCH_REQUESTED);
        DocumentReference docRef = FirebaseFirestore.getInstance().collection(FirebaseContract.UserEntry.COLLECTION_NAME)
                .document(uid).collection(FirebaseContract.CategoryEntry.COLLECTION_NAME).document();
        String id = docRef.getId();
        category.setId(id);
        FirebaseFirestore.getInstance().collection(FirebaseContract.UserEntry.COLLECTION_NAME).document(FirebaseAuth.getInstance().getUid())
                .collection(FirebaseContract.CategoryEntry.COLLECTION_NAME).document(id).set(category)
                .addOnCompleteListener(task -> result.setValue(RequestStatus.FETCH_CORRECT));
        return result;
    }

    public MutableLiveData<RequestStatus> updateCategory(Category category, String uid) {
        //TODO IGUAL QUE ARRIBA
        MutableLiveData<RequestStatus> result = new MutableLiveData<>(RequestStatus.FETCH_REQUESTED);
        FirebaseFirestore.getInstance().collection(FirebaseContract.UserEntry.COLLECTION_NAME).document(FirebaseAuth.getInstance().getUid())
                .collection(FirebaseContract.CategoryEntry.COLLECTION_NAME).document(category.getId()).set(category)
                .addOnCompleteListener(task -> result.setValue(RequestStatus.FETCH_CORRECT));
        return result;
    }

    public MutableLiveData<Category> getCategorieById(String uid, String id) {
        MutableLiveData<Category> result = new MutableLiveData<>();
        DocumentReference catRef = FirebaseFirestore.getInstance().collection(FirebaseContract.UserEntry.COLLECTION_NAME)
                .document(uid)
                .collection(FirebaseContract.CategoryEntry.COLLECTION_NAME).document(id);
        catRef.get().addOnCompleteListener(categoryResult -> {
            result.setValue(categoryResult.getResult().toObject(Category.class));
        });
        return result;
    }
}
