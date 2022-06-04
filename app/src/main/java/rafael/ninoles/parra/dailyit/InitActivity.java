package rafael.ninoles.parra.dailyit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import rafael.ninoles.parra.dailyit.ui.activities.LoginActivity;
import rafael.ninoles.parra.dailyit.ui.activities.MainActivity;

/**
 * Entrypoint of the app. Activity that checks if the user is logged or not.
 * It opens the LoginActivity or the MainActivity
 */
public class InitActivity extends AppCompatActivity {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        checkAuth();
    }

    private void checkAuth() {
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }
}