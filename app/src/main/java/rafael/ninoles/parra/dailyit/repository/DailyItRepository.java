package rafael.ninoles.parra.dailyit.repository;

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
import rafael.ninoles.parra.dailyit.model.Task;

public class DailyItRepository {
    private static volatile DailyItRepository INSTANCE;
    private static final long MILIS_IN_WEEK = 604800000;

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

    public void createNewTask(Task task, String uid) {
        DocumentReference docRef = FirebaseFirestore.getInstance().collection(FirebaseContract.UserEntry.COLLECTION_NAME)
                .document(uid).collection(FirebaseContract.TaskEntry.COLLECTION_NAME).document();
        String id = docRef.getId();
        task.setId(id);
        FirebaseFirestore.getInstance().collection(FirebaseContract.UserEntry.COLLECTION_NAME).document(FirebaseAuth.getInstance().getUid())
                .collection(FirebaseContract.TaskEntry.COLLECTION_NAME).document(id).set(task);
    }

    public void updateTask(Task task, String uid) {
        FirebaseFirestore instance = FirebaseFirestore.getInstance();
        DocumentReference docRef = instance.collection(FirebaseContract.UserEntry.COLLECTION_NAME).document(uid)
                .collection(FirebaseContract.TaskEntry.COLLECTION_NAME).document(task.getId());
        docRef.set(task);
    }
}
