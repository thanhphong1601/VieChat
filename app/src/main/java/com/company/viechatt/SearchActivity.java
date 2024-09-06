package com.company.viechatt;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity{
    FirebaseAuth auth;
    FirebaseUser user;
    RecyclerView recyclerViewUser;
    List<User> userList;
    UserAdapter userAdapter;
    EditText etSearchEmail;
    Button btnSearch;
    ImageButton btnBack;
    DatabaseReference usersRef;


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
        setContentView(R.layout.activity_search);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        etSearchEmail = findViewById(R.id.et_search_email);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, new UserAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(int position) {
                User clickedUser = userList.get(position);

                // Chuyển sang Activity khác với thông tin của user được chọn
                Intent intent = new Intent(SearchActivity.this, ChatActivity.class);
                intent.putExtra("name", clickedUser.getName());
                intent.putExtra("email", clickedUser.getEmail());
                intent.putExtra("friendid", clickedUser.getUserId());
                startActivity(intent);
            }
        });

        recyclerViewUser = findViewById(R.id.recycler_user_search);
        recyclerViewUser.setAdapter(userAdapter);
        recyclerViewUser.setLayoutManager(new LinearLayoutManager(this));

        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        // Sự kiện khi nhấn nút Tìm
        btnSearch = findViewById(R.id.btn_search_friends);
        btnSearch.setOnClickListener(v -> searchUserByEmail());

        btnBack = findViewById(R.id.btn_back_search);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void searchUserByEmail() {
        String email = etSearchEmail.getText().toString().trim();
        if (!email.isEmpty()) {
            // Truy vấn Firebase để tìm user có email khớp
            Query query = usersRef.orderByChild("email").equalTo(email);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userList.clear(); // Xóa danh sách hiện tại
                    if (dataSnapshot.exists()) {
                        // Duyệt qua từng kết quả (mỗi kết quả là một user)
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            User user = userSnapshot.getValue(User.class);
                            userList.add(user); // Thêm user vào danh sách
                        }
                        userAdapter.notifyDataSetChanged(); // Cập nhật adapter
                    } else {
                        Toast.makeText(SearchActivity.this, "Không tìm thấy user!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Xử lý lỗi nếu có
                }
            });
        } else {
            Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
        }
    }
}