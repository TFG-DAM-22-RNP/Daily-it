package rafael.ninoles.parra.dailyit.ui.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

import rafael.ninoles.parra.dailyit.R;
import rafael.ninoles.parra.dailyit.databinding.ActivityMainBinding;
import rafael.ninoles.parra.dailyit.helpers.MainActivityHelper;
import rafael.ninoles.parra.dailyit.model.FirebaseContract;
import rafael.ninoles.parra.dailyit.model.User;
import rafael.ninoles.parra.dailyit.ui.tasks.TasksFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private static final String LOG_TAG = "MAIN_ACTIVITY";
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageRef = storage.getReference();
    private final StorageReference defaultImage = storageRef.child("profile-images/profile.jpg");
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private File defaultLocalImage = null;
    private TextView tvEmail;
    private TextView tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //setSupportActionBar(binding.appBarMain.toolbar);
        /*binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleFabClick();
            }
        });*/
        // INIT ADS
        MobileAds.initialize(this, initializationStatus -> {
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navView = binding.navView;
        navView.setNavigationItemSelectedListener(this);
        // navView.bringToFront();
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
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_tasks)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        navView.getMenu().getItem(0).setChecked(true);
        //NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        // TODO click en log out
        //NavigationUI.setupWithNavController(navView, navController);
        MainActivity mainActivity = this;
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> Log.e("CAMBIADO", "onDestinationChanged: " + destination.getLabel()));
    }

    private void showNewTaskDialog() {
        final Dialog dialog = new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.new_category_dialog);

        final EditText etCategoryName = dialog.findViewById(R.id.etCategoryName);
        final TextView tvDialogTitle = dialog.findViewById(R.id.tvCategoryDialogTitle);
        final Spinner spinner = dialog.findViewById(R.id.spCategorySelected);
        final Button btSave = dialog.findViewById(R.id.btSave);
        final Button btCancel = dialog.findViewById(R.id.btCancel);
        final View categoryColor = dialog.findViewById(R.id.categoryColor);

        tvDialogTitle.setText(getString(R.string.new_category));

        btCancel.setOnClickListener(e->{
            dialog.dismiss();
        });

        dialog.show();
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
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(LOG_TAG, document.getId() + " => " + document.getData());
                                User user = document.toObject(User.class);
                                //getUserImage(user.getImgProfile());
                                tvName.setText(user.getName());
                            }
                        } else {
                            // TODO actuar mejor en error
                            Log.e(LOG_TAG, "Error reading the user data");
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("HE VUELTO");
        TasksFragment tasksFragment = MainActivityHelper.getTasksFragment();
        if (tasksFragment == null) {
            return;
        }
        tasksFragment.moveDay();
    }

    //TODO eliminar
    private void getDefaultImage() {
        try {
            defaultLocalImage = File.createTempFile("images", "jpg");
            defaultImage.getFile(defaultLocalImage).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.v(LOG_TAG, "Default profile picture downloaded from firebase");
                    System.out.println(defaultLocalImage.toURI().toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e(LOG_TAG, "Error downloading the default profile picture from firebase");
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

    @Override
    public void onBackPressed() {
        finish(); //This would close the app
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
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
            default:
                //TODO prevent volver a cargar si ya esta en ese
                Toast toast = Toast.makeText(this, getString(R.string.not_implemented_yet), Toast.LENGTH_LONG);
                toast.show();
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                return false;
        }
        System.out.println("Clickado en nav");
        //close navigation drawer
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}