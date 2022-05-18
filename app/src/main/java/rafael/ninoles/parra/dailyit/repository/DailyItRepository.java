package rafael.ninoles.parra.dailyit.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rafael.ninoles.parra.dailyit.model.Category;
import rafael.ninoles.parra.dailyit.model.FirebaseContract;
import rafael.ninoles.parra.dailyit.model.Task;

public class DailyItRepository {
    private static volatile DailyItRepository INSTANCE;

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

    @NonNull
    private MutableLiveData<List<Task>> getTasksFromQuery(Query colRef) {
        MutableLiveData<List<Task>> tasks = new MutableLiveData<>();
        colRef.get().addOnCompleteListener(result -> {
            List<Task> taskList = new ArrayList<>();
            for(DocumentSnapshot snap : result.getResult().getDocuments()){
                Task task = snap.toObject(Task.class);
                taskList.add(task);
            }
            tasks.setValue(taskList);
        });
        return tasks;
    }

    private com.google.android.gms.tasks.Task<DocumentSnapshot> getCategory(String categoryId) {
        com.google.android.gms.tasks.Task<DocumentSnapshot> task = FirebaseFirestore.getInstance().collection(FirebaseContract.UserEntry.COLLECTION_NAME)
                .document(FirebaseAuth.getInstance().getUid()).collection(FirebaseContract.CategoryEntry.COLLECTION_NAME).document(categoryId).get();
        return task;
    }
}
