package com.example.eventhive.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.eventhive.data.local.entities.UserEntity;

import java.util.List;

/**
 * Data Access Object for User operations.
 * Provides methods to interact with the users table.
 */
@Dao
public interface UserDao {

    @Insert
    long insertUser(UserEntity user);

    @Update
    void updateUser(UserEntity user);

    @Delete
    void deleteUser(UserEntity user);

    @Query("DELETE FROM users WHERE id = :userId")
    void deleteUserById(int userId);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    UserEntity getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    UserEntity getUserById(int userId);

    @Query("SELECT * FROM users")
    LiveData<List<UserEntity>> getAllUsers();

    @Query("SELECT * FROM users")
    List<UserEntity> getAllUsersSync();

    @Query("SELECT COUNT(*) FROM users")
    LiveData<Integer> getUserCount();

    @Query("SELECT COUNT(*) FROM users WHERE role = :role")
    LiveData<Integer> getUserCountByRole(String role);
}
