package com.example.eventhive.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.eventhive.data.local.AppDatabase;
import com.example.eventhive.data.local.dao.UserDao;
import com.example.eventhive.data.local.entities.UserEntity;
import com.example.eventhive.models.User;
import com.example.eventhive.utils.PasswordHasher;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Repository for User data operations.
 * Mediates between ViewModel and UserDao, handles password hashing.
 */
public class UserRepository {

    private final UserDao userDao;
    private final ExecutorService executorService;
    private final LiveData<List<User>> allUsers;

    public UserRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        userDao = database.userDao();
        executorService = Executors.newSingleThreadExecutor();

        // Transform LiveData<List<UserEntity>> to LiveData<List<User>>
        allUsers = Transformations.map(userDao.getAllUsers(),
                entities -> entities.stream().map(UserEntity::toUser).collect(Collectors.toList()));
    }

    /**
     * Registers a new user with hashed password.
     * 
     * @param user     User to register (password will be hashed)
     * @param callback Callback with result (user ID if success, -1 if failed)
     */
    public void registerUser(User user, RepositoryCallback<Long> callback) {
        executorService.execute(() -> {
            try {
                // Hash the password before storing
                String hashedPassword = PasswordHasher.hashPassword(user.getPassword());

                UserEntity entity = UserEntity.fromUser(user);
                entity.setPassword(hashedPassword);

                long userId = userDao.insertUser(entity);
                callback.onSuccess(userId);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Authenticates a user with email and password.
     * 
     * @param email    User email
     * @param password Plain text password
     * @param callback Callback with User if success, null if failed
     */
    public void loginUser(String email, String password, RepositoryCallback<User> callback) {
        executorService.execute(() -> {
            try {
                UserEntity entity = userDao.getUserByEmail(email);

                if (entity == null) {
                    callback.onError("User not found");
                    return;
                }

                // Verify password
                if (PasswordHasher.verifyPassword(password, entity.getPassword())) {
                    callback.onSuccess(entity.toUser());
                } else {
                    callback.onError("Invalid password");
                }
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Gets all users as LiveData.
     */
    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    /**
     * Gets user by ID.
     */
    public void getUserById(int userId, RepositoryCallback<User> callback) {
        executorService.execute(() -> {
            try {
                UserEntity entity = userDao.getUserById(userId);
                if (entity != null) {
                    callback.onSuccess(entity.toUser());
                } else {
                    callback.onError("User not found");
                }
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Updates user information (name, email, phone).
     */
    public void updateUser(User user, RepositoryCallback<Void> callback) {
        executorService.execute(() -> {
            try {
                UserEntity entity = UserEntity.fromUser(user);
                userDao.updateUser(entity);
                callback.onSuccess(null);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Deletes a user by ID.
     */
    public void deleteUser(int userId, RepositoryCallback<Void> callback) {
        executorService.execute(() -> {
            try {
                userDao.deleteUserById(userId);
                callback.onSuccess(null);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Gets user count by role.
     */
    public LiveData<Integer> getUserCountByRole(String role) {
        return userDao.getUserCountByRole(role);
    }

    /**
     * Callback interface for repository operations.
     */
    public interface RepositoryCallback<T> {
        void onSuccess(T result);

        void onError(String error);
    }
}
