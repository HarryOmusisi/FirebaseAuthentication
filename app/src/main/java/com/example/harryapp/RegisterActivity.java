package com.example.harryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextRegisterFullName,editTextRegisterEmail,editTextRegisterDoB,editTextRegisterMobile
            ,editTextRegisterPwd,editTextRegisterConfirmPwd;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterGender;
    private RadioButton radioButtonRegisterGenderSelected;
    private DatePickerDialog picker;
    private static final String TAG="RegisterActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        getSupportActionBar().setTitle("Register");

        Toast.makeText(RegisterActivity.this, "You can register now", Toast.LENGTH_SHORT).show();

        progressBar= findViewById(R.id.progressBar);
        editTextRegisterFullName=findViewById(R.id.editText_register_full_name);
        editTextRegisterEmail=findViewById(R.id.editText_register_email);
        editTextRegisterDoB=findViewById(R.id.editText_register_dob);
        editTextRegisterMobile=findViewById(R.id.editText_register_mobile);
        editTextRegisterPwd=findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPwd=findViewById(R.id.editText_register_confirm_password);

        // Radio Button for gender
        radioGroupRegisterGender=findViewById(R.id.radioGroup_register_gender);
        radioGroupRegisterGender.clearCheck();

        //setting up date picker on edit text
        editTextRegisterDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar= Calendar.getInstance();
                int day =  calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                //Date Picker dialog
                picker= new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        editTextRegisterDoB.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                },year,month,day);
                picker.show();
            }
        });

        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedGenderId= radioGroupRegisterGender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected=findViewById(selectedGenderId);

                //obtain the data
                String textFullName=editTextRegisterFullName.getText().toString();
                String textEmail=editTextRegisterEmail.getText().toString();
                String textDoB=editTextRegisterDoB.getText().toString();
                String textMobile=editTextRegisterMobile.getText().toString();
                String textPassword=editTextRegisterPwd.getText().toString();
                String textConfirmPassword=editTextRegisterConfirmPwd.getText().toString();
                String textGender; //cant obtain value of a gender

                if(TextUtils.isEmpty(textFullName)){
                    Toast.makeText(RegisterActivity.this, "Please enter your full name", Toast.LENGTH_SHORT).show();
                    editTextRegisterFullName.setError("Full name is required");
                    editTextRegisterFullName.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(RegisterActivity.this, "Enter a valid Email address", Toast.LENGTH_SHORT).show();
                    editTextRegisterEmail.setError("Email address is required");
                    editTextRegisterEmail.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(RegisterActivity.this, "Enter a valid Email address", Toast.LENGTH_SHORT).show();
                    editTextRegisterEmail.setError("valid Email address required");
                    editTextRegisterEmail.requestFocus();
                }else if(TextUtils.isEmpty(textDoB)){
                    Toast.makeText(RegisterActivity.this, "Date of Birth Required", Toast.LENGTH_SHORT).show();
                    editTextRegisterDoB.setError("DoB Required");
                    editTextRegisterDoB.requestFocus();
                }else if (radioGroupRegisterGender.getCheckedRadioButtonId()==-1){
                    Toast.makeText(RegisterActivity.this, "Please Select the gender", Toast.LENGTH_SHORT).show();
                    radioButtonRegisterGenderSelected.setError("Gender is Required");
                    radioButtonRegisterGenderSelected.requestFocus();
                }else if(TextUtils.isEmpty(textMobile)){
                    Toast.makeText(RegisterActivity.this, "Enter a mobile number", Toast.LENGTH_SHORT).show();
                    editTextRegisterMobile.setError("mobile number required");
                    editTextRegisterMobile.requestFocus();
                }else if(textMobile.length()!=10){
                    Toast.makeText(RegisterActivity.this, "Please re-enter your mobile number", Toast.LENGTH_SHORT).show();
                    editTextRegisterMobile.setError("Mobile number should be 10 digits starting with +254");
                    editTextRegisterMobile.requestFocus();
                }else if(TextUtils.isEmpty(textPassword)){
                    Toast.makeText(RegisterActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    editTextRegisterPwd.setError("Password is required");
                    editTextRegisterPwd.requestFocus();
                }else if(textPassword.length() < 6){
                    Toast.makeText(RegisterActivity.this, "Password should be at least 6 digits", Toast.LENGTH_SHORT).show();
                    editTextRegisterPwd.setError("Enter a valid Password");
                    editTextRegisterPwd.requestFocus();
                }else if(TextUtils.isEmpty(textConfirmPassword)){
                    Toast.makeText(RegisterActivity.this, "Please confirm your password", Toast.LENGTH_SHORT).show();
                    editTextRegisterConfirmPwd.setError("Password confirmation required");
                    editTextRegisterConfirmPwd.requestFocus();
                }else if(!textPassword.equals(textConfirmPassword)){
                    Toast.makeText(RegisterActivity.this, "Input a similar password", Toast.LENGTH_SHORT).show();
                    editTextRegisterConfirmPwd.setError("Password confirmation is required");
                    editTextRegisterConfirmPwd.requestFocus();

                    //clear the entered passwords
                    editTextRegisterPwd.clearComposingText();
                    editTextRegisterConfirmPwd.clearComposingText();
                }else{
                    textGender=radioButtonRegisterGenderSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFullName,textEmail,textDoB,textGender,textMobile,textPassword);
                }
            }
        });
    }

    private void registerUser(String textFullName, String textEmail, String textDoB, String textGender, String textMobile, String textPassword) {
        FirebaseAuth auth= FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(textEmail,textPassword).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    FirebaseUser firebaseUser= auth.getCurrentUser();

                    UserProfileChangeRequest profileChangeRequest= new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                    firebaseUser.updateProfile(profileChangeRequest);

                    //User data info into a real data base
                    ReadWriteUserDetails writeUserDetails= new ReadWriteUserDetails(textDoB,textGender,textMobile);

                    //Extracting User references from database for "Registered Users"
                    //An instance of DatabaseReference is required
                    DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Registered Users");
                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){


                                //send verification email
                                firebaseUser.sendEmailVerification();

                                Toast.makeText(RegisterActivity.this, "User created successfully. Please verify your email",
                                        Toast.LENGTH_SHORT).show();


                    //open user profile after verification
                    Intent intent= new Intent(RegisterActivity.this,UserProfile.class);
                    //To prevent user from returning to register activity
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                            }else{

                                Toast.makeText(RegisterActivity.this, "User registration failed. Please try again",
                                        Toast.LENGTH_SHORT).show();
                            }
                            //Hide progressbar when User creation is successful or not
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                } else{
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e){
                        editTextRegisterPwd.setError("Your password is too weak. Kindly Use a stronger one with a mix of alphanumeric keys");
                        editTextRegisterPwd.requestFocus();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        editTextRegisterPwd.setError("Your Email is invalid or already in use.Kindly re-enter.");
                        editTextRegisterPwd.requestFocus();
                    }catch (FirebaseAuthUserCollisionException e){
                        editTextRegisterPwd.setError("User is already with this email. Use another email");
                        editTextRegisterPwd.requestFocus();
                    }catch (Exception e){
                       Log.e(TAG, e.getMessage());
                       Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                    }
                    //Hide progressbar when User creation is successful or not
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}