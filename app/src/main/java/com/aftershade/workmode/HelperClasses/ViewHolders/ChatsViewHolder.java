package com.aftershade.workmode.HelperClasses.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView profileImage;
    public TextView fullName, chatMessage, timeTxtV, messageNumber;
    public ImageView mutedIcon;

    public ChatsViewHolder(@NonNull View itemView) {
        super(itemView);
        profileImage = itemView.findViewById(R.id.chats_profile_picture);
        fullName = itemView.findViewById(R.id.chats_fullname_txt);
        chatMessage = itemView.findViewById(R.id.chats_message);
        timeTxtV = itemView.findViewById(R.id.chats_time);
        messageNumber = itemView.findViewById(R.id.new_message_number_chats_card);
        mutedIcon = itemView.findViewById(R.id.muted_icon_chats_card);
    }
}
