package com.example.groupvideochatdemo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupvideochatdemo.CallActivity;
import com.example.groupvideochatdemo.R;
import com.example.groupvideochatdemo.utils.Utils;
import com.opentok.android.Subscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * SubscribersAdapter : to show and render list of subscribers added in a session on top of screen
 */
public class SubscribersAdapter extends RecyclerView.Adapter<SubscribersAdapter.ViewHolder> {

    private CallActivity activity;
    private List<Subscriber> list = new ArrayList<>();
    private RecyclerItemClickListener callback;

    /**
     * Constructor
     *
     * @param activity type of BaseActivity
     */
    public SubscribersAdapter(CallActivity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public SubscribersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_subscriber, parent, false);
        return new SubscribersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubscribersAdapter.ViewHolder holder, int position) {
        if (list.get(position) != null) {

            Subscriber subscriber = list.get(position);
            if (subscriber.getView().getParent() != null) {
                ((ViewGroup) subscriber.getView().getParent()).removeView(subscriber.getView()); // <- fix for crash occur while .addView(framelayout)
            }
            holder.subscriberContainer.addView(subscriber.getView());

            //to show mute and un-mute icon on subscriber-view
            if (subscriber.getSubscribeToAudio())
                holder.ivMuteSubscriber.setImageResource(R.drawable.ic_mike_on_subscriber);
            else
                holder.ivMuteSubscriber.setImageResource(R.drawable.ic_mike_off_subscriber);

            //AppUtil.setMapFilterDrawable(binding.subscriberContainer, "#ffffff", "#ffffff", 15f, 2);

            String[] splitArray;
            //to show mute and un-mute icon on subscriber-view
            if (subscriber.getStream() != null && subscriber.getStream().getName() != null && !subscriber.getStream().getName().trim().isEmpty()) {
                splitArray = Utils.splitName(subscriber.getStream().getName());
                if (splitArray != null) {
                    holder.subscriberName.setText(splitArray[0]);
                } else
                    holder.subscriberName.setText(subscriber.getStream().getName());
                holder.subscriberName.setVisibility(View.VISIBLE);
            } else
                holder.subscriberName.setVisibility(View.GONE);

            holder.ivMuteSubscriber.setElevation(10);
            holder.ivMuteSubscriber.setOnClickListener(v -> callback.onMuteSubscriberIconClick(subscriber, position, holder.ivMuteSubscriber));
            holder.subscriberContainer.setOnClickListener(v -> callback.onRecyclerItemClick(subscriber, position, holder.ivMuteSubscriber));
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private FrameLayout subscriberContainer;
        private AppCompatTextView subscriberName;
        private AppCompatImageView ivMuteSubscriber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subscriberContainer = itemView.findViewById(R.id.subscriberContainer);
            subscriberName = itemView.findViewById(R.id.subscriberName);
            ivMuteSubscriber = itemView.findViewById(R.id.ivMuteSubscriber);
        }
    }

    /**
     * to set list data
     *
     * @param list Subscriber
     */
    public void setData(List<Subscriber> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    /**
     * to set list data
     *
     * @param list Subscriber
     */
    public void updateData(List<Subscriber> list, int position) {
        this.list = list;
        notifyItemRemoved(position);
    }

    /**
     * to set list data
     *
     * @param list Subscriber
     */
    public void updateDataChange(List<Subscriber> list, int position) {
        this.list = list;
        notifyItemChanged(position);
    }

    /**
     * to set callback of the activity that handles click events
     *
     * @param callback RecyclerItemClickListener
     */
    public void setCallback(RecyclerItemClickListener callback) {
        this.callback = callback;
    }

    /**
     * IMakeSelection: listener to notify activity on click events
     */
    public interface RecyclerItemClickListener {
        void onMuteSubscriberIconClick(Subscriber subscriber, int position, View view);

        void onRecyclerItemClick(Subscriber subscriber, int position, View view);
    }
}
