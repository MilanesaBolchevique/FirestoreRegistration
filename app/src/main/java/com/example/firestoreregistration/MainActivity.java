package com.example.firestoreregistration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Button btn_register, btn_login;
    private EditText  mailUser, passwordUser;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mailUser = findViewById(R.id.mail);
        passwordUser = findViewById(R.id.password);
        btn_register = findViewById(R.id.btn_register);
        btn_login = findViewById(R.id.btn_login);


        //Dar funcion al boton de registro
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creamos las variables que le pasaremos a firebase
                String mail, password;
                mail = mailUser.getText().toString();
                password = passwordUser.getText().toString();
                //Condicion por si introducimos datos vacios, si no se cumple, procedemos con el registro
                if (mail.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Completa los campos", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(mail, password);
                }
            }
        });

        //Dar funcion Log-in al boton de logueo (metodo utilizando lambda para recortar codigo)
        btn_login.setOnClickListener(v -> {
            //Creamos las variables que le pasaremos a firebase
            String mail, password;
            mail = String.valueOf(mailUser.getText());
            password = String.valueOf(passwordUser.getText());
            //Condicion por si introducimos datos vacios, si no se cumple, procedemos con el registro
            if (mail.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa los campos", Toast.LENGTH_SHORT).show();
            } else {
                //Ejecutamos el metodo de log-in
                loginUser(mail, password);
            }
        });
    }
    //Metodo para logear usuarios con correo en Firebase
    private void loginUser(String mailUser, String passwordUser) {
        mAuth.signInWithEmailAndPassword(mailUser, passwordUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Credenciales Correctas", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, InicioActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Credenciales Incorrectas", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //Metodo para registrar usuarios con correo en Firebase
    private void registerUser(String mailUser, String passwordUser) {
        mAuth.createUserWithEmailAndPassword(mailUser, passwordUser).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();

                    // Crearemos un objeto HashMap para los datos del usuario
                    Map<String, Object> user = new HashMap<>();
                    user.put("email", mailUser);

                    // Agregaremos el fichero del usuario a una colección llamada "usuarios"
                    mFirestore.collection("usuarios").document(userId)
                            .set(user)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Registrado correctamente", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error en el registro", Toast.LENGTH_SHORT).show();
                            });
                }
            } else {
                Toast.makeText(this, "Error en el registro", Toast.LENGTH_SHORT).show();
            }
        });
    }
}