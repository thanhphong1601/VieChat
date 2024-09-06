package com.company.viechatt;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.company.viechatt.fragments.ProfileFragment;
import com.company.viechatt.model.ChatsList;
import com.company.viechatt.model.Message;
import com.company.viechatt.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    String senderId, receiverId, message;
    CircularImageView img;
    TextView receiverName;
    ImageButton buttonBack;
    List<Message> chatsList;
    TextView receiverStatus;
    String online = "#8ABF73";
    String offline = "#B1B1B1";

    FirebaseUser firebaseUser;

    EditText msg;
    Button btnSend;

    DatabaseReference databaseReference;
    MessageAdapter messageAdapter;
    RecyclerView recyclerView;
    ValueEventListener seenlistener;

    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageReference storageReference;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        storageReference = FirebaseStorage.getInstance().getReference();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("chat_messages");

        buttonBack = findViewById(R.id.btn_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        ImageButton imageBtn = findViewById(R.id.button_select_image);
//        imageBtn.setOnClickListener(v -> openImageChooser());

        receiverName = findViewById(R.id.tv_receiverName);
        recyclerView = findViewById(R.id.msgRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        receiverStatus = findViewById(R.id.tv_receiver_status);

        btnSend = findViewById(R.id.btn_send);
        msg = findViewById(R.id.txt_msg);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        senderId = firebaseUser.getUid(); // my id or the one who is loggedin

        receiverId = getIntent().getStringExtra("friendid"); // retreive the friendid when we click on the item

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(receiverId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User users = snapshot.getValue(User.class);

                receiverName.setText(users.getName()); // set the text of the user on textivew in toolbar
                readMessages(senderId, receiverId);
//                receiverStatus.setText(users.getStatus());
                if (users.getStatus().equals("online")){
                    receiverStatus.setText("Online");
                    receiverStatus.setBackgroundColor(Color.parseColor(online));
                } else {
                    receiverStatus.setText("Offline");
                    receiverStatus.setBackgroundColor(Color.parseColor(offline));
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().length() > 0) {

                    btnSend.setEnabled(true);

                } else {

                    btnSend.setEnabled(false);


                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = msg.getText().toString();

                if (!text.startsWith(" ")) {
                    msg.getText().insert(0, " ");

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                message = msg.getText().toString();

                sendMessage(senderId, receiverId, message);

                msg.setText(" ");


            }
        });

        CircularImageView profilePic = findViewById(R.id.profilePic);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang ProfileActivity
                Intent intent = new Intent(ChatActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

    }

//    private void openImageChooser() {
//        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        galleryIntent.setType("image/*");
//
//        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        Intent chooser = Intent.createChooser(galleryIntent, "Select or Take a Picture");
//        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});
//
//        startActivityForResult(chooser, PICK_IMAGE_REQUEST);
//    }



    public void readMessages(final String myid, final String friendid) {

        chatsList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatsList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {

                    Message chats = ds.getValue(Message.class);
                    Log.d("ChatActivity", "SenderId: " + chats.getSenderId() + ", ReceiverId: " + chats.getReceiverId());


                    if (chats != null &&
                            chats.getSenderId() != null &&
                            chats.getReceiverId() != null) {
                        Log.d("ChatActivity", "SenderId: " + chats.getSenderId() + ", ReceiverId: " + chats.getReceiverId());

                        Log.d("ChatActivity", "SenderId: " + chats.getSenderId());
                        Log.d("ChatActivity", "ReceiverId: " + chats.getReceiverId());
                        Log.d("ChatActivity", "Message: " + chats.getMessage());

                        if ((chats.getSenderId().equals(myid) && chats.getReceiverId().equals(friendid)) ||
                                (chats.getSenderId().equals(friendid) && chats.getReceiverId().equals(myid))) {
                            chatsList.add(chats);
                        }
                    }

                    messageAdapter = new MessageAdapter(ChatActivity.this, chatsList);
                    recyclerView.setAdapter(messageAdapter);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(final String myid, final String friendid, final String message) {


        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("senderId", myid);
        hashMap.put("receiverId", friendid);
        hashMap.put("message", message);

        reference.child("Chats").push().setValue(hashMap);


        final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Chatslist").child(myid).child(friendid);

        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if (!snapshot.exists()) {


                    reference1.child("id").setValue(friendid);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void Status (final String status) {


        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);



    }

    @Override
    protected void onResume() {
        super.onResume();
        Status("online");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Status("offline");
    }
}