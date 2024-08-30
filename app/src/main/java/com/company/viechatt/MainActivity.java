package com.company.viechatt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.company.viechatt.model.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    FirebaseAuth auth;
    Button logoutBtn;
    static String TAG = "FirebaseHelper";



    @Override
    public void onStart() {
        super.onStart();

        // Kiểm tra nếu user đã đăng nhập
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            return;
        }

        // Chưa đăng nhập thì chuyển ra màn hình đăng nhập
        else {
            Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        init();
        initProfile();




        //test button
//        logoutBtn = findViewById(R.id.btn_logout_test);
//        logoutBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
    }

    private void initProfile() {
        TextView tvName = findViewById(R.id.name);
        TextView email = findViewById(R.id.email);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("UserInfo", "User data not found!");

        //xu li
    }

    private void init(){

        toolbar = findViewById(R.id.toolbar);
        drawerLayout= findViewById(R.id.draw_layout);


        setSupportActionBar(toolbar);
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        NavigationView nav_view = findViewById(R.id.nav_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            public void onDrawerOpened(View drawerView) {super.onDrawerOpened(drawerView);}
        };

        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
                try {
                    int itemId = item.getItemId();
                    if (itemId == R.id.nav_search){
                        Intent profileIntent = new Intent(getApplicationContext(), TestActivity.class);
                        startActivity(profileIntent);
                        return true;
                    }
                    if (itemId == R.id.nav_home){
                        Intent profileIntent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(profileIntent);
                        return true;
                    }
                    if (itemId == R.id.nav_profile)
                    {
                        Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(profileIntent);
                        return true;
                    }
                } catch (Exception e){
                    throw new RuntimeException(e);
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

}