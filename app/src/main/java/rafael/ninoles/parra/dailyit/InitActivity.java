package rafael.ninoles.parra.dailyit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class InitActivity extends AppCompatActivity {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        checkAuth();
    }

    private void checkAuth() {
        if(auth.getCurrentUser() == null){
            startActivity(new Intent(this, LoginActivity.class));
        }else{
            auth.signOut();
        }
        finish();
    }
}