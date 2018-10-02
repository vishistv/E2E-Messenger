package com.example.vishistvarugeese.e2emessenger;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vishist Varugeese on 04-04-2018.
 */

public class MessageAdapter extends ArrayAdapter<Message> {

    private List<Message> chat_list = new ArrayList<Message>();
    private TextView txtMessage;
    Context ctx;

    public MessageAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        ctx = context;
    }

    @Override
    public void add(@Nullable Message object) {
        chat_list.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return chat_list.size();
    }

    @Nullable
    @Override
    public Message getItem(int position) {
        return chat_list.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.message_list_item, parent, false);
        }

        txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
        String stringMessage;
        boolean POSITION;

        Message provider = getItem(position);
        stringMessage = provider.message;
        POSITION = provider.position;
        txtMessage.setText(stringMessage);

        if(POSITION == true){
            txtMessage.setBackgroundResource(R.drawable.round_textview);
            txtMessage.setTextColor(ContextCompat.getColor(ctx, R.color.colorWhite));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.RIGHT;
            txtMessage.setLayoutParams(params);

        } else if(POSITION == false) {
            txtMessage.setBackgroundResource(R.drawable.round_textview1);
            txtMessage.setTextColor(ContextCompat.getColor(ctx, R.color.colorWhite));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.LEFT;
            txtMessage.setLayoutParams(params);
        }

        return convertView;
    }
}
