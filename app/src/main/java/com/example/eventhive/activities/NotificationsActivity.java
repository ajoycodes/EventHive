package com.example.eventhive.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventhive.R;
import com.example.eventhive.databases.DatabaseHelper;
import com.example.eventhive.models.Notification;
import com.example.eventhive.utils.SessionManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Activity to display user notifications.
 * Shows all notifications for the logged-in user, sorted newest first.
 * Supports marking individual notifications as read and marking all as read.
 */
public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewNotifications;
    private View emptyStateNotifications;
    private ProgressBar progressBar;
    private TextView tvMarkAllRead;
    private DatabaseHelper dbHelper;
    private SessionManager session;
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Initialize views
        recyclerViewNotifications = findViewById(R.id.recyclerViewNotifications);
        emptyStateNotifications = findViewById(R.id.emptyStateNotifications);
        progressBar = findViewById(R.id.progressBar);
        tvMarkAllRead = findViewById(R.id.tvMarkAllRead);
        ImageView btnBack = findViewById(R.id.btnBack);

        dbHelper = new DatabaseHelper(this);
        session = SessionManager.getInstance(this);

        // Setup RecyclerView
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(new ArrayList<>());
        recyclerViewNotifications.setAdapter(adapter);

        // Setup listeners
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (tvMarkAllRead != null) {
            tvMarkAllRead.setOnClickListener(v -> markAllAsRead());
        }

        // Load notifications
        loadNotifications();
    }

    private void loadNotifications() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewNotifications.setVisibility(View.GONE);
        emptyStateNotifications.setVisibility(View.GONE);

        int userId = session.getUserId();
        List<Notification> notifications = dbHelper.getNotificationsForUser(userId);

        progressBar.setVisibility(View.GONE);

        if (notifications == null || notifications.isEmpty()) {
            emptyStateNotifications.setVisibility(View.VISIBLE);
            recyclerViewNotifications.setVisibility(View.GONE);
        } else {
            emptyStateNotifications.setVisibility(View.GONE);
            recyclerViewNotifications.setVisibility(View.VISIBLE);
            adapter.updateNotifications(notifications);
        }
    }

    private void markAllAsRead() {
        int userId = session.getUserId();
        dbHelper.markAllNotificationsAsRead(userId);
        loadNotifications(); // Reload to update UI
    }

    /**
     * RecyclerView Adapter for displaying notifications.
     */
    private class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
        private List<Notification> notifications;

        public NotificationAdapter(List<Notification> notifications) {
            this.notifications = notifications != null ? notifications : new ArrayList<>();
        }

        public void updateNotifications(List<Notification> newNotifications) {
            this.notifications.clear();
            this.notifications.addAll(newNotifications);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_notification, parent, false);
            return new NotificationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
            Notification notification = notifications.get(position);

            // Set title
            holder.tvTitle.setText(notification.getTitle());

            // Set message
            holder.tvMessage.setText(notification.getMessage());

            // Set relative time
            holder.tvTime.setText(getRelativeTime(notification.getTimestamp()));

            // Show/hide unread indicator
            if (notification.isRead()) {
                holder.unreadIndicator.setVisibility(View.INVISIBLE);
                // Optionally reduce text opacity for read notifications
                holder.tvTitle.setAlpha(0.7f);
                holder.tvMessage.setAlpha(0.7f);
            } else {
                holder.unreadIndicator.setVisibility(View.VISIBLE);
                holder.tvTitle.setAlpha(1.0f);
                holder.tvMessage.setAlpha(1.0f);
            }

            // Mark as read on tap
            holder.itemView.setOnClickListener(v -> {
                if (!notification.isRead()) {
                    dbHelper.markNotificationAsRead(notification.getId());
                    loadNotifications(); // Reload to update UI
                }
            });
        }

        @Override
        public int getItemCount() {
            return notifications.size();
        }

        class NotificationViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvMessage, tvTime;
            View unreadIndicator;

            public NotificationViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tvNotificationTitle);
                tvMessage = itemView.findViewById(R.id.tvNotificationMessage);
                tvTime = itemView.findViewById(R.id.tvNotificationTime);
                unreadIndicator = itemView.findViewById(R.id.unreadIndicator);
            }
        }
    }

    /**
     * Converts timestamp to relative time (e.g., "2 hours ago").
     */
    private String getRelativeTime(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;

        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (seconds < 60) {
            return "Just now";
        } else if (minutes < 60) {
            return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
        } else if (hours < 24) {
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        } else if (days < 7) {
            return days + (days == 1 ? " day ago" : " days ago");
        } else {
            // Format as date for older notifications
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            return sdf.format(new Date(timestamp));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload notifications when returning to this activity
        loadNotifications();
    }
}
