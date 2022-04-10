package rafael.ninoles.parra.dailyit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import rafael.ninoles.parra.dailyit.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity{

    private static final int SIGN_IN_GOOGLE = 100;
    private ActivityLoginBinding binding;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        createListeners();
    }

    private void createListeners() {
        System.out.println("Creando listeners");
        binding.btSignUp.setOnClickListener(e->{
            register();
        });
        
        binding.btLogIn.setOnClickListener(e->{
            System.out.println("AAAAAAA");
            logIn();
            System.out.println("LOGE");
        });

        binding.btLogInGoogle.setOnClickListener(e->{
            System.out.println("CLCIK EN GOOGLE");
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.server_id)).requestEmail().build();
            GoogleSignInClient client = GoogleSignIn.getClient(this,gso);

            startActivityForResult(client.getSignInIntent(),SIGN_IN_GOOGLE);
        });
    }

    private void logIn() {
        if(!verifyInputs()){
            //TODO muestra mensaje
            return;
        }
        System.out.println("Logeando");
        auth.signInWithEmailAndPassword(
                binding.etEmail.getText().toString(),
                binding.etPassword.getText().toString()
        ).addOnCompleteListener(this::logInResult);
    }

    private void logInResult(Task<AuthResult> result) {
        if(result.isSuccessful()){
            //TODO cambio de Activity
            System.out.println("Correcto");
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }else{
            //TODO muestra error
            System.out.println("Fallito");
        }
    }

    private boolean verifyInputs() {
        //TODO verificar inputs vacios
        return true;
    }

    private void register() {
        auth.createUserWithEmailAndPassword(
                binding.etEmail.getText().toString(),
                binding.etPassword.getText().toString()
        ).addOnCompleteListener(this::logInResult);
    }

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
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        }else{
                            //TODO manejar fallo
                            System.out.println("TODO Fallito");
                        }
                    });
                }
            } catch (Exception e) {
                //TODO CUIDAR Exc no google play, no google play services updated...
                e.printStackTrace();
            }
        }
    }
}