package rafael.ninoles.parra.dailyit.ui.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.regex.Pattern;

import rafael.ninoles.parra.dailyit.R;
import rafael.ninoles.parra.dailyit.databinding.ActivityLoginBinding;
import rafael.ninoles.parra.dailyit.exceptions.InputNeededException;
import rafael.ninoles.parra.dailyit.repository.DailyItRepository;

/**
 * Acitity that let the user to log in or register, using Google or Email and password
 */
public class LoginActivity extends AppCompatActivity{

    private static final int SIGN_IN_GOOGLE = 100;
    private static final String LOG_TAG = "LoginActivity";
    private static final String REGEX_EMAIL = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    /**
     * The password need to have 1 lowercase, 1 uppercase, 1 number, 1 special char and
     * a minimum of 12 chars
     */
    private static final String REGEX_PASSWORD = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{12,}$";
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        createListeners();
    }

    private void createListeners() {
        binding.btSignUp.setOnClickListener(e->{
            register();
        });
        
        binding.btLogIn.setOnClickListener(e->{
            logIn();
        });

        binding.btLogInGoogle.setOnClickListener(e->{
            Log.i(LOG_TAG,"Clicked in log in with Google");
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.server_id)).requestEmail().build();
            GoogleSignInClient client = GoogleSignIn.getClient(this,gso);

            startActivityForResult(client.getSignInIntent(),SIGN_IN_GOOGLE);
        });
    }

    private void logIn() {
        try {
            verifyInputs();
        }catch (InputNeededException e){
            Toast toast = Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        auth.signInWithEmailAndPassword(
                binding.etEmail.getText().toString(),
                binding.etPassword.getText().toString()
        ).addOnCompleteListener(this::logInResult);
    }

    private void logInResult(Task<AuthResult> result) {
        if(result.isSuccessful()){
            DailyItRepository.getInstance().createUser(FirebaseAuth.getInstance().getUid(),binding.etEmail.getText().toString());
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }else{
            Toast toast = Toast.makeText(this, getString(R.string.invalid_login), Toast.LENGTH_LONG);
            toast.show();
        }
    }

    /**
     * Verify the required fields and throws an exception is one of them is empty
     * @throws InputNeededException
     */
    private void verifyInputs() throws InputNeededException {
        if(!patternMatches(binding.etEmail.getText().toString(),REGEX_EMAIL)){
            throw new InputNeededException(getString(R.string.email_invalid));
        }
        if(!patternMatches(binding.etPassword.getText().toString(),REGEX_PASSWORD)){
            throw new InputNeededException(getString(R.string.password_invalid));
        }
    }

    private void register() {
        try {
            verifyInputs();
        }catch (InputNeededException e){
            Toast toast = Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        auth.createUserWithEmailAndPassword(
                binding.etEmail.getText().toString(),
                binding.etPassword.getText().toString()
        ).addOnCompleteListener(this::logInResult);
    }

    /**
     * Check if a String match a patern
     * @param compared
     * @param regexPattern
     * @return true or false
     */
    public static boolean patternMatches(String compared, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(compared)
                .matches();
    }

    /*
     * Used for the google log in / register
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_GOOGLE){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult();
                if(account != null){
                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
                    auth.signInWithCredential(credential).addOnCompleteListener(complete->{
                        if(complete.isSuccessful()){
                            boolean isNew = complete.getResult().getAdditionalUserInfo().isNewUser();
                            if(isNew){
                                DailyItRepository.getInstance().createUser(auth.getUid(), auth.getCurrentUser().getEmail());
                            }
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        }else{
                            Toast toast = Toast.makeText(this, getString(R.string.error_loging_with_google), Toast.LENGTH_LONG);
                            toast.show();
                            complete.getException().printStackTrace();
                            Log.e(LOG_TAG, complete.getException().getMessage());
                        }
                    });
                }
            } catch (Exception e) {
                Toast toast = Toast.makeText(this, getString(R.string.error_loging_with_google), Toast.LENGTH_LONG);
                toast.show();
                Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }
        }
    }
}