package com.example.eventhive.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventhive.R;
import com.example.eventhive.databases.DatabaseHelper;
import com.example.eventhive.models.User;
import java.util.ArrayList;
import java.util.List;

public class AdminUserListActivity extends AppCompatActivity {

    private RecyclerView userRecyclerView;
    private DatabaseHelper dbHelper;
    private List<User> userList;
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_list);

        dbHelper = new DatabaseHelper(this);
        userRecyclerView = findViewById(R.id.userRecyclerView);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        loadUsers();
    }

    private void loadUsers() {
        userList = dbHelper.getAllUsers();

        if (userList == null || userList.isEmpty()) {
            Toast.makeText(this, "No users found", Toast.LENGTH_SHORT).show();
            userList = new ArrayList<>();
        } else {
            Toast.makeText(this, "Found " + userList.size() + " users", Toast.LENGTH_SHORT).show();
        }

        adapter = new UserAdapter(userList);
        userRecyclerView.setAdapter(adapter);
    }

    // RecyclerView Adapter
    private class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
        private List<User> users;

        public UserAdapter(List<User> users) {
            this.users = users;
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user, parent, false);
            return new UserViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            User user = users.get(position);

            String fullName = user.getFirstName() + " " + user.getLastName();
            holder.tvUserName.setText(fullName);
            holder.tvUserEmail.setText(user.getEmail());
            holder.tvUserRole.setText(user.getRole());

            // Set initial
            String initial = "";
            if (!user.getFirstName().isEmpty()) {
                initial = String.valueOf(user.getFirstName().charAt(0)).toUpperCase();
            }
            holder.tvUserInitial.setText(initial);
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        class UserViewHolder extends RecyclerView.ViewHolder {
            TextView tvUserName, tvUserEmail, tvUserRole, tvUserInitial;

            public UserViewHolder(@NonNull View itemView) {
                super(itemView);
                tvUserName = itemView.findViewById(R.id.tvUserName);
                tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
                tvUserRole = itemView.findViewById(R.id.tvUserRole);
                tvUserInitial = itemView.findViewById(R.id.tvUserInitial);
            }
        }
    }
}
