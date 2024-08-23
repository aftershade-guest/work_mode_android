package com.aftershade.workmode.HelperClasses.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.R;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    public TextView dateText, receiverText, senderText, receiverTime, senderTime;
    public ImageView senderImage, receiverImage, senderFileIcon, receiverFileIcon;
    public ConstraintLayout senderConstraint, receiverConstraint;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        dateText = itemView.findViewById(R.id.message_date);
        receiverText = itemView.findViewById(R.id.receiver_messages_text);
        senderText = itemView.findViewById(R.id.sender_message_text);
        receiverTime = itemView.findViewById(R.id.receive_message_time);
        senderTime = itemView.findViewById(R.id.sender_message_time);
        senderImage = itemView.findViewById(R.id.message_sender_image_view);
        receiverImage = itemView.findViewById(R.id.message_receiver_image_view);
        senderConstraint = itemView.findViewById(R.id.sent_messages_layout);
        receiverConstraint = itemView.findViewById(R.id.receiver_messages_relative_layout);
        senderFileIcon = itemView.findViewById(R.id.sender_file_icon);
        receiverFileIcon = itemView.findViewById(R.id.receiver_file_icon);
    }
}
