package com.example.sockettest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sockettest.R;
import com.example.sockettest.bean.ChatBean;
import com.example.sockettest.util.Configuration;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerChatAdapter extends RecyclerView.Adapter<RecyclerChatAdapter.ChatViewHolder> {

    private Context context;
    private List<ChatBean> list;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public RecyclerChatAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }

    public void addChatBean(ChatBean chatBean) {
        if (chatBean == null)
            return;
        list.add(chatBean);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerChatAdapter.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatViewHolder(LayoutInflater.from(context).inflate(R.layout.lv_item_chat, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerChatAdapter.ChatViewHolder holder, int position) {
        ChatBean bean = list.get(position);
        if (position == 0 || position > 0 && bean.getTime() - list.get(position - 1).getTime() > 300000) {
            holder.tvTime.setVisibility(View.VISIBLE);
            holder.tvTime.setText(format.format(bean.getTime()));
        } else {
            holder.tvTime.setVisibility(View.GONE);
        }
        if (bean.getType() == Configuration.MESSAGE_SYSTEM) {
            holder.tvTip.setVisibility(View.VISIBLE);
            holder.tvTip.setText(bean.getName() + bean.getContent());
            holder.tvTime.setVisibility(View.VISIBLE);
            holder.tvTime.setText(format.format(bean.getTime()));
            holder.layoutLeft.setVisibility(View.GONE);
            holder.layoutRight.setVisibility(View.GONE);
        } else if (bean.getType() == Configuration.MESSAGE_SEND) {
            holder.tvTip.setVisibility(View.GONE);
            holder.layoutRight.setVisibility(View.VISIBLE);
            holder.layoutLeft.setVisibility(View.GONE);
            holder.tvNameRight.setText(bean.getName());
            holder.tvMessageRight.setText(bean.getContent());
            holder.imageRight.setImageResource(R.drawable.image_head_boy);
        } else if (bean.getType() == Configuration.MESSAGE_RECEIVER) {
            holder.tvTip.setVisibility(View.GONE);
            holder.layoutRight.setVisibility(View.GONE);
            holder.layoutLeft.setVisibility(View.VISIBLE);
            holder.tvNameLeft.setText(bean.getName());
            holder.tvMessageLeft.setText(bean.getContent());
            holder.imageLeft.setImageResource(R.drawable.image_head_boy);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView tvTip;
        TextView tvTime;
        RelativeLayout layoutLeft;
        CircleImageView imageLeft;
        TextView tvNameLeft;
        TextView tvMessageLeft;
        RelativeLayout layoutRight;
        CircleImageView imageRight;
        TextView tvNameRight;
        TextView tvMessageRight;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTip = itemView.findViewById(R.id.tvTip);
            tvTime = itemView.findViewById(R.id.tvTime);

            layoutLeft = itemView.findViewById(R.id.layoutLeft);
            imageLeft = itemView.findViewById(R.id.imageLeft);
            tvNameLeft = itemView.findViewById(R.id.tvNameLeft);
            tvMessageLeft = itemView.findViewById(R.id.tvMessageLeft);

            layoutRight = itemView.findViewById(R.id.layoutRight);
            imageRight = itemView.findViewById(R.id.imageRight);
            tvNameRight = itemView.findViewById(R.id.tvNameRight);
            tvMessageRight = itemView.findViewById(R.id.tvMessageRight);
        }
    }
}
