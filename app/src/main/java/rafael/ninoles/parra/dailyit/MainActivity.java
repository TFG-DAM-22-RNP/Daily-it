package rafael.ninoles.parra.dailyit;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

import rafael.ninoles.parra.dailyit.databinding.ActivityMainBinding;
import rafael.ninoles.parra.dailyit.model.FirebaseContract;
import rafael.ninoles.parra.dailyit.model.User;

public class MainActivity extends AppCompatActivity{

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private static final String LOG_TAG = "MAIN_ACTIVITY";
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageRef = storage.getReference();
    private final StorageReference defaultImage = storageRef.child("profile-images/profile.jpg");
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private File defaultLocalImage = null;
    private ImageView ivProfile;
    private TextView tvEmail;
    private TextView tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, auth.getCurrentUser().getEmail(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getDefaultImage();
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navView = binding.navView;
        // navView.bringToFront();
        ivProfile = navView.getHeaderView(0).findViewById(R.id.ivProfile);
        tvEmail = navView.getHeaderView(0).findViewById(R.id.tvEmail);
        tvName = navView.getHeaderView(0).findViewById(R.id.tvName);
        printUserData();
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_tasks)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        //NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        // TODO click en log out
        NavigationUI.setupWithNavController(navView, navController);
    }

    private void printUserData() {
        tvEmail.setText(auth.getCurrentUser().getEmail());
        Log.d(LOG_TAG,auth.getCurrentUser().getUid());
        db.collection(FirebaseContract.UserEntry.COLLECTION_NAME).document(auth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()){
                                Log.d(LOG_TAG, document.getId() + " => " + document.getData());
                                User user = document.toObject(User.class);
                                tvName.setText(user.getName());
                            }
                        }else {
                            // TODO actuar mejor en error
                            Log.e(LOG_TAG,"Error reading the user data");
                        }
                    }
                });
    }

    private void getDefaultImage() {
        try {
            defaultLocalImage = File.createTempFile("images", "jpg");
            defaultImage.getFile(defaultLocalImage).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.v(LOG_TAG,"Default profile picture downloaded from firebase");
                    System.out.println(defaultLocalImage.toURI().toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e(LOG_TAG,"Error downloading the default profile picture from firebase");
                }
            });
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error loading the default profile image");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}