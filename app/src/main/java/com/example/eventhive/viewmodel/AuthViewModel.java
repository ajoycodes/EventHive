package com.example.eventhive.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventhive.data.repository.UserRepository;
import com.example.eventhive.models.User;

/**
 * ViewModel for authentication operations (login and registration).
 * Handles input validation and communicates with UserRepository.
 */
public class AuthViewModel extends AndroidViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<AuthResult> loginResult = new MutableLiveData<>();
    private final MutableLiveData<AuthResult> registerResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public AuthViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    /**
     * Attempts to log in a user.
     */
    public void login(String email, String password) {
        // Validate inputs
        if (email == null || email.trim().isEmpty()) {
            loginResult.setValue(new AuthResult(false, "Email is required", null));
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            loginResult.setValue(new AuthResult(false, "Password is required", null));
            return;
        }

        if (!isValidEmail(email)) {
            loginResult.setValue(new AuthResult(false, "Invalid email format", null));
            return;
        }

        isLoading.setValue(true);

        userRepository.loginUser(email, password, new UserRepository.RepositoryCallback<User>() {
            @Override
            public void onSuccess(User user) {
                isLoading.postValue(false);
                loginResult.postValue(new AuthResult(true, "Login successful", user));
            }

            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                loginResult.postValue(new AuthResult(false, error, null));
            }
        });
    }

    /**
     * Attempts to register a new user.
     */
    public void register(String firstName, String lastName, String email, String password,
            String confirmPassword, String role, String phone) {
        // Validate inputs
        if (firstName == null || firstName.trim().isEmpty()) {
            registerResult.setValue(new AuthResult(false, "First name is required", null));
            return;
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            registerResult.setValue(new AuthResult(false, "Last name is required", null));
            return;
        }

        if (email == null || email.trim().isEmpty()) {
            registerResult.setValue(new AuthResult(false, "Email is required", null));
            return;
        }

        if (!isValidEmail(email)) {
            registerResult.setValue(new AuthResult(false, "Invalid email format", null));
            return;
        }

        if (password == null || password.length() < 6) {
            registerResult.setValue(new AuthResult(false, "Password must be at least 6 characters", null));
            return;
        }

        if (!password.equals(confirmPassword)) {
            registerResult.setValue(new AuthResult(false, "Passwords do not match", null));
            return;
        }

        if (phone == null || phone.trim().isEmpty()) {
            registerResult.setValue(new AuthResult(false, "Phone number is required", null));
            return;
        }

        isLoading.setValue(true);

        User user = new User(firstName, lastName, email, password, role, phone);

        userRepository.registerUser(user, new UserRepository.RepositoryCallback<Long>() {
            @Override
            public void onSuccess(Long userId) {
                isLoading.postValue(false);
                // Create new User object with the generated ID
                User registeredUser = new User(
                        userId.intValue(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getRole(),
                        user.getPhone());
                registerResult.postValue(new AuthResult(true, "Registration successful", registeredUser));
            }

            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                registerResult.postValue(new AuthResult(false, error, null));
            }
        });
    }

    /**
     * Validates email format.
     */
    private boolean isValidEmail(String email) {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // LiveData getters
    public LiveData<AuthResult> getLoginResult() {
        return loginResult;
    }

    public LiveData<AuthResult> getRegisterResult() {
        return registerResult;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    /**
     * Result class for authentication operations.
     */
    public static class AuthResult {
        public final boolean success;
        public final String message;
        public final User user;

        public AuthResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }
    }
}
