package rafael.ninoles.parra.dailyit.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import rafael.ninoles.parra.dailyit.R;
import rafael.ninoles.parra.dailyit.databinding.ActivityMainBinding;
import rafael.ninoles.parra.dailyit.helpers.MainActivityHelper;
import rafael.ninoles.parra.dailyit.model.FirebaseContract;
import rafael.ninoles.parra.dailyit.model.User;
import rafael.ninoles.parra.dailyit.ui.dialogs.NewCategoryDialog;
import rafael.ninoles.parra.dailyit.ui.tasks.TasksFragment;

/**
 * Activity that manages the TaskFragment, CategoryFragment, StatsFragment
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String LOG_TAG = "MAIN_ACTIVITY";
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageRef = storage.getReference();
    private final StorageReference defaultImage = storageRef.child("profile-images/profile.jpg");
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private File defaultLocalImage = null;
    private TextView tvEmail;
    private TextView tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // INIT ADS
        MobileAds.initialize(this, initializationStatus -> {
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navView = binding.navView;
        navView.setNavigationItemSelectedListener(this);
        tvEmail = navView.getHeaderView(0).findViewById(R.id.tvEmail);
        tvName = navView.getHeaderView(0).findViewById(R.id.tvName);
        printUserData();
        binding.appBarMain.addTask.setOnClickListener(e -> {
            Intent intent = new Intent(this, TaskActivity.class);
            startActivityForResult(intent, 0);
        });
        binding.appBarMain.addCategory.setOnClickListener(e -> {
            showNewTaskDialog();
        });
        binding.appBarMain.addCash.setOnClickListener(e -> {
            Toast toast = Toast.makeText(this, getString(R.string.not_implemented_yet), Toast.LENGTH_LONG);
            toast.show();
        });
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_tasks)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        navView.getMenu().getItem(0).setChecked(true);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            Log.e("CAMBIADO", "onDestinationChanged: " + destination.getLabel());
        });
    }

    private void showNewTaskDialog() {
        new NewCategoryDialog(this,this);
    }

    private void getUserImage(String path) {
        StringBuilder fullPath = new StringBuilder("profile-images/");
        fullPath.append(path);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(fullPath.toString());
        ImageView iv = binding.navView.findViewById(R.id.ivProfile);
        Glide.with(this /* context */)
                .load(storageReference)
                .into(iv);
    }

    private void printUserData() {
        tvEmail.setText(auth.getCurrentUser().getEmail());
        Log.d(LOG_TAG, auth.getCurrentUser().getUid());
        db.collection(FirebaseContract.UserEntry.COLLECTION_NAME).document(auth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(LOG_TAG, document.getId() + " => " + document.getData());
                            User user = document.toObject(User.class);
                            //TODO Load profile image in next versions
                            //getUserImage(user.getImgProfile());
                            tvName.setText(user.getName());
                        }
                    } else {
                        Toast toast = Toast.makeText(this, getString(R.string.error_reading_user_data), Toast.LENGTH_LONG);
                        toast.show();
                        Log.e(LOG_TAG, "Error reading the user data");
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TasksFragment tasksFragment = MainActivityHelper.getTasksFragment();
        if (tasksFragment == null) {
            return;
        }
        if(resultCode<=TaskActivity.DONE){
            tasksFragment.moveToTab(resultCode);
        }
        Date newDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(newDate);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,1);
        tasksFragment.moveDay(calendar.getTime());
    }

    //TODO Fix problems in next versions
    private void getDefaultImage() {
        try {
            defaultLocalImage = File.createTempFile("images", "jpg");
            defaultImage.getFile(defaultLocalImage).addOnSuccessListener(taskSnapshot -> {
                Log.v(LOG_TAG, "Default profile picture downloaded from firebase");
                System.out.println(defaultLocalImage.toURI().toString());
            }).addOnFailureListener(exception -> Log.e(LOG_TAG, "Error downloading the default profile picture from firebase"));
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error loading the default profile image");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_tasks:
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.nav_tasks);
                break;
            case R.id.nav_categories:
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.nav_categories);
                break;
            case R.id.nav_stats:
                Navigation.findNavController(this,R.id.nav_host_fragment_content_main).navigate(R.id.nav_stats);
                break;
            default:
                Toast toast = Toast.makeText(this, getString(R.string.not_implemented_yet), Toast.LENGTH_LONG);
                toast.show();
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                return false;
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}