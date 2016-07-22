package net.awpspace.mynote;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by tuanhai on 7/22/16.
 */
public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText etEmail = (EditText) findViewById(R.id.activity_register_et_email);
        final EditText etPassword = (EditText) findViewById(R.id.activity_register_et_password);
        final EditText etConfirmPassword = (EditText) findViewById(R.id.activity_register_et_repassword);

        Button btnRegister = (Button) findViewById(R.id.activity_register_btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etEmail.getText().toString())
                        || TextUtils.isEmpty(etPassword.getText().toString())
                        || !etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
                    infoEnterdInvalid();
                    return;
                }

                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Register successful!",
                                    Toast.LENGTH_SHORT).show();
                            RegisterActivity.this.finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Register failed!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void infoEnterdInvalid() {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Infomations entered are invalid!")
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }
}
