package com.example.harryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfile extends AppCompatActivity {

    private TextView textViewWelcome,textViewFullName,textViewEmail,textViewDoB,textViewGender,textViewMobile;
    private ProgressBar progressBar;
    private String fullName,email,doB,gender,mobile;
    private ImageView imageView;
    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        getSupportActionBar().setTitle("Home");

        textViewFullName=findViewById(R.id.textView_show_fullName);
        textViewWelcome=findViewById(R.id.textView_show_welcome);
        textViewEmail=findViewById(R.id.textView_show_email);
        textViewDoB=findViewById(R.id.textView_show_dob);
        textViewGender=findViewById(R.id.textView_show_gender);
        textViewMobile=findViewById(R.id.textView_show_mobile);
        progressBar=findViewById(R.id.progressBar);

        authProfile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser= authProfile.getCurrentUser();

        if(firebaseUser == null){
            Toast.makeText(UserProfile.this, "Something went wrong!User's details are not available at the moment", Toast.LENGTH_SHORT).show();

        }else{
            checkIfEmailVerified(firebaseUser);
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }
    }
        //User coming to UserProfile after successful registration
    private void checkIfEmailVerified(FirebaseUser firebaseUser) {
        if(firebaseUser.isEmailVerified()){
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        //set up the alert builder
        AlertDialog.Builder builder=new AlertDialog.Builder(UserProfile.this);
        builder.setTitle("Email is not Verified");
        builder.setMessage("Please verify your email now.You can not login without email verification next time!");

        //open email if user clicks continue button
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        //create Alert dialog box
        AlertDialog alertDialog= builder.create();
        alertDialog.show();
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID=firebaseUser.getUid();

        //Extracting user reference from Database
        DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Obtaining the user
                ReadWriteUserDetails readUserDetails=snapshot
                        .getValue(ReadWriteUserDetails.class);
                if(readUserDetails !=null){
                    fullName=firebaseUser.getDisplayName();
                    email=firebaseUser.getEmail();
                    doB=readUserDetails.dob;
                    gender=readUserDetails.gender;
                    mobile=readUserDetails.mobile;

                    textViewWelcome.setText("Welcome, "+ fullName + "!");
                    textViewFullName.setText(fullName);
                    textViewEmail.setText(email);
                    textViewDoB.setText(doB);
                    textViewGender.setText(gender);
                    textViewMobile.setText(mobile);


                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfile.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);

            }
        });
    }

    //Creating ActionBar Menu


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu items
        getMenuInflater().inflate(R.menu.common_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    //where items are selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id= item.getItemId();

        if(id == R.id.menu_refresh){
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
//        }else if(id==R.id.menu_update_profile){
//            Intent intent=new Intent(UserProfile.this,UpdateProfile.class);
//            startActivity(intent);
//        }else if(id==R.id.menu_update_email){
//            Intent intent=new Intent(UserProfile.this,UpdateEmail.class);
//            startActivity(intent);
//        }else if(id==R.id.menu_settings){
//            Toast.makeText(UserProfile.this, "Menu Settings", Toast.LENGTH_SHORT).show();
//        }else if(id==R.id.menu_change_password){
//            Intent intent=new Intent(UserProfile.this,ChangePassword.class);
//            startActivity(intent);
//        }else if(id==R.id.menu_delete_profile){
//            Intent intent=new Intent(UserProfile.this,DeleteProfile.class);
//            startActivity(intent);
        }else if(id==R.id.menu_logout){
            authProfile.signOut();
            Toast.makeText(UserProfile.this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(UserProfile.this,MainActivity.class);


            //Clear stack to prevent user from coming back to the activity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();       //close UserProfile activit
        }else{
            Toast.makeText(UserProfile.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}