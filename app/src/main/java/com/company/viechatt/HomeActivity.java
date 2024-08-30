package com.company.viechatt;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.viechatt.adapter.UserAdapter;
import com.company.viechatt.model.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements UserAdapter.OnUserClickListener{
    FirebaseAuth auth;
    FirebaseUser user;
    private ActionBar actionBar;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    RecyclerView recyclerViewUser;
    List<User> userList;
    UserAdapter userAdapter;


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

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, this);

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
                    if (itemId == R.id.logout){
                        auth.signOut();
                        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(loginIntent);
                        finish();
                        return true;
                    }
                } catch (Exception e){
                    throw new RuntimeException(e);
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });



        // Inflate nội dung của HomeActivity vào FrameLayout
        getLayoutInflater().inflate(R.layout.activity_home, findViewById(R.id.contentFrame), true);

        if (user == null) {
            Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }

        recyclerViewUser = findViewById(R.id.recycler_user);
        recyclerViewUser.setAdapter(userAdapter);
        recyclerViewUser.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<User> newUsers = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    if (user != null) {
                        newUsers.add(user);
                        Log.d("FirebaseData", "User: " + user.getName() + ", " + user.getEmail());
                    }
                }
                updateUsers(newUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Failed to load posts.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void updateUsers(List<User> users) {
        userList.clear();
        userList.addAll(users);
        Log.d("UpdatePosts", "Number of users: " + users.size());

        userAdapter.notifyDataSetChanged();
    }

    @Override
    public void onUserClick(int position) {
        User clickedUser = userList.get(position);

        // Chuyển sang Activity khác với thông tin của user được chọn
        Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
        intent.putExtra("name", clickedUser.getName());
        intent.putExtra("email", clickedUser.getEmail());
        intent.putExtra("friendid", clickedUser.getUserId());
        startActivity(intent);
    }


}