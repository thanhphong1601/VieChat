package com.company.viechatt;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    FirebaseUser firebaseUser;

    EditText msg;
    Button btnSend;

    DatabaseReference databaseReference;
    MessageAdapter messageAdapter;
    RecyclerView recyclerView;
    ValueEventListener seenlistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        buttonBack = findViewById(R.id.btn_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        receiverName = findViewById(R.id.tv_receiverName);
        recyclerView = findViewById(R.id.msgRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

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



    }

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
    }