package com.example.zachschlesinger.firebasepractice;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    // declare UI components
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button authButton;
    private Button signInButton;
    private TextView textViewDisplayAuth;

    // declare firebase components
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize UI components
        editTextEmail = (EditText) findViewById(R.id.email_input);
        editTextPassword = (EditText) findViewById(R.id.password_input);
        authButton = (Button) findViewById(R.id.auth_button);
        textViewDisplayAuth = (TextView) findViewById(R.id.displayAuth);
        signInButton = (Button) findViewById(R.id.sign_in_button);

        // initialize firebase
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference myRef = database.getReference("message");
        myRef.setValue("Hello, World");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("firebase", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("firebase", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        textViewDisplayAuth.setText("Firebase not setup");

        authButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void createAccount() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("firebase", "createUserWithEmail:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            textViewDisplayAuth.setText("worked");
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            DatabaseReference myRef = database.getReference(user.getUid());

                            myRef.setValue(user.getEmail());
                        } else if (!task.isSuccessful()) {
                            Toast.makeText(getBaseContext(), "Auth failed.", Toast.LENGTH_SHORT).show();
                            textViewDisplayAuth.setText("did not work");
                        }
                    }
                });
    }

    private void signIn() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("firebase", "signInWithEmail:onComplete:" + task.isSuccessful());
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            textViewDisplayAuth.setText(uid);
                        }
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("firebase", "signInWithEmail", task.getException());
                            Toast.makeText(getBaseContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}