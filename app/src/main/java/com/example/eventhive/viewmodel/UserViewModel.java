package com.example.eventhive.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.eventhive.data.repository.UserRepository;
import com.example.eventhive.models.User;

import java.util.List;

/**
 * ViewModel for User/Admin operations.
 * Manages user list and admin-specific functionality.
 */
public class UserViewModel extends AndroidViewModel {

    private final UserRepository userRepository;
    private final LiveData<List<User>> allUsers;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        allUsers = userRepository.getAllUsers();
    }

    /**
     * Gets all users (for admin panel).
     */
    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    /**
     * Deletes a user by ID.
     */
    public void deleteUser(int userId, UserRepository.RepositoryCallback<Void> callback) {
        userRepository.deleteUser(userId, callback);
    }

    /**
     * Gets user count by role.
     */
    public LiveData<Integer> getUserCountByRole(String role) {
        return userRepository.getUserCountByRole(role);
    }
}
