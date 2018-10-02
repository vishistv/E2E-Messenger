package com.example.vishistvarugeese.e2emessenger;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ChatActivity extends AppCompatActivity {


    Firebase reference1, reference2;

    RSA rsa;
    String nonEncryptedMessage;

    ListView listView;
    boolean position = true;
    MessageAdapter adapter;

    private EditText messageArea;
    private ImageButton send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        listView = (ListView)findViewById(R.id.list_message);

        messageArea = (EditText)findViewById(R.id.messageArea);
        send = (ImageButton)findViewById(R.id.btn_send);

        adapter = new MessageAdapter(this,  R.layout.message_list_item);

        listView.setAdapter(adapter);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(adapter.getCount() - 1);
            }
        });

        rsa = new RSA();

        Firebase.setAndroidContext(this);
        reference1 = new Firebase("https://e2emessenger-1acd9.firebaseio.com/messages/" + UserDetails.username + "_" + UserDetails.chatWith);
        reference2 = new Firebase("https://e2emessenger-1acd9.firebaseio.com/messages/" + UserDetails.chatWith + "_" + UserDetails.username);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();
                nonEncryptedMessage = messageText;

                if(!messageText.equals("")){
                    try {
                        messageText = rsa.Encrypt(messageText, UserDetails.public_key);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    }

                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", UserDetails.username);
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    messageArea.setText("");
                }
            }
        });

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString().trim();
                String userName = map.get("user").toString();

                if(userName.equals(UserDetails.username)){

                    //addMessageBox("You:-\n" + nonEncryptedMessage, 1);
                    position = true;
                    adapter.add(new Message(position, "You:-\n" + nonEncryptedMessage));
                }
                else{
                    try {
                        Log.d("message", message);
                        message = rsa.Decrypt(message, getApplicationContext(), UserDetails.username);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    }
                    position = false;
                    adapter.add(new Message(position,  UserDetails.chatWith + ":-\n" + message));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

//    public void addMessageBox(String message, int type){
////        TextView textView = new TextView(ChatActivity.this);
////        textView.setText(message);
////
////        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
////        lp2.weight = 1.0f;
////
////        if(type == 1) {
////            lp2.gravity = Gravity.RIGHT;
////            textView.setBackgroundResource(R.drawable.bubble_in);
////        }
////        else{
////            lp2.gravity = Gravity.LEFT;
////            textView.setBackgroundResource(R.drawable.bubble_out);
////        }
////        textView.setLayoutParams(lp2);
////        layout.addView(textView);
////        scrollView.fullScroll(View.FOCUS_DOWN);
//    }
}