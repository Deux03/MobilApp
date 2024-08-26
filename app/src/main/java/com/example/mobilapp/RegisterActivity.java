package com.example.mobilapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String REGISTER_TAG = RegisterActivity.class.getName();
    private static final int SECRET_KEY = 9871564;

    private static final String PREF_KEY = Objects.requireNonNull(RegisterActivity.class.getPackage()).toString();
    private FirebaseAuth mAuth;

    EditText usernameET;
    EditText emailET;
    EditText passwordET;
    EditText passwordAgainET;
    EditText phoneET;
    EditText adressET;

    Spinner phoneSpinner;

    RadioGroup accountTypeGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);

        if(secret_key != SECRET_KEY)
        {
            finish();
        }

        usernameET = findViewById(R.id.usernameEditText);
        emailET = findViewById(R.id.emailEditText);
        passwordET = findViewById(R.id.passwordEditText);
        passwordAgainET = findViewById(R.id.passwordAgainEditText);
        phoneET = findViewById(R.id.phoneNumberEditText);
        adressET = findViewById(R.id.adressEditText);

        phoneSpinner = findViewById(R.id.phoneSpinner);
        phoneSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.phone_modes, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        phoneSpinner.setAdapter(arrayAdapter);

        accountTypeGroup = findViewById(R.id.accountTypeGroup);
        accountTypeGroup.check(R.id.costumerRadioBtn);


        mAuth = FirebaseAuth.getInstance();

        SharedPreferences preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String email = preferences.getString("EmailPref", "");
        emailET.setText(email);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void register(View view) {
        String username = String.valueOf(usernameET.getText());
        String email = String.valueOf(emailET.getText());
        String password = String.valueOf(passwordET.getText());
        String passwordAgain = String.valueOf(passwordAgainET.getText());
        String phone = String.valueOf(phoneET.getText());
        String phoneType = phoneSpinner.getSelectedItem().toString();
        String address = String.valueOf(adressET.getText());

        int checkedId = accountTypeGroup.getCheckedRadioButtonId();
        RadioButton radioBtn = accountTypeGroup.findViewById(checkedId);
        String accountType = String.valueOf(radioBtn.getText());

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || passwordAgain.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Please fill in all required fields!", Toast.LENGTH_LONG).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(RegisterActivity.this, "Please enter a valid email address!", Toast.LENGTH_LONG).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters long!", Toast.LENGTH_LONG).show();
            return;
        }

        if(!password.equals(passwordAgain)){
            Toast.makeText(RegisterActivity.this, "Passwords doesn't match!", Toast.LENGTH_LONG).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                User user = new User(username, email, phone, phoneType, address, accountType);
                writeUserDataToFirestore(userId, user);
                Toast.makeText(RegisterActivity.this, "Registration was successful!", Toast.LENGTH_SHORT).show();
                startShowingCourses();
            } else {
                Toast.makeText(RegisterActivity.this, "Registration failed: some1 already uses this email!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void cancel(View view) {
        finish();
    }

    private void startShowingCourses(){
        Intent intent = new Intent(this, CourseListActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selectedItem = adapterView.getItemAtPosition(i).toString();
        Log.i(REGISTER_TAG, selectedItem);
        //TODO
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //TODO
    }

    private void writeUserDataToFirestore(String userId, User user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> Toast.makeText(RegisterActivity.this, "User data stored successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterActivity.this, "Error storing user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(REGISTER_TAG, "Error storing user data", e);
                });
    }
}