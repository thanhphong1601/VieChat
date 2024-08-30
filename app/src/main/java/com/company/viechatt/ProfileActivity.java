package com.company.viechatt;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileAvatar;
    private TextView profileName, profileEmail;

    private DatabaseReference userRef;
    Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Kết nối với các view
        profileAvatar = findViewById(R.id.profile_avatar);
        profileName = findViewById(R.id.profile_name);
        profileEmail = findViewById(R.id.profile_email);

        btn_back = findViewById(R.id.btn_back);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Lấy UID người dùng hiện tại
        String currentUserId = "79bs65k2EVO4BhvcSNyL76F8fac2"; // Bạn có thể lấy từ FirebaseAuth

        // Kết nối Firebase
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        // Lấy dữ liệu người dùng từ Firebase
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);

                    // Gán giá trị lên TextView
                    profileName.setText(name);
                    profileEmail.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi
            }
        });


    }
}