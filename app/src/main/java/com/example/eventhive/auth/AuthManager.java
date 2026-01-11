package com.example.eventhive.auth;

import android.util.Log;
import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * AuthManager handles all Firebase Authentication and Firestore operations
 * for user authentication and profile management
 */
public class AuthManager {
    private static final String TAG = "AuthManager";
    private static final String USERS_COLLECTION = "users";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public AuthManager() {
        try {
            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
            Log.d(TAG, "Firebase initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Firebase not available - google-services.json may be missing", e);
            mAuth = null;
            db = null;
        }
    }

    /**
     * Registers a new user with email and password
     * Creates user document in Firestore with profile information
     * 
     * @param email     User's email address
     * @param password  User's password
     * @param firstName User's first name
     * @param lastName  User's last name
     * @param role      User's role (user, organizer, or admin)
     * @param phone     User's phone number
     * @param callback  Callback for success/failure
     */
    public void register(String email, String password, String firstName, String lastName,
            String role, String phone, AuthCallback callback) {
        if (mAuth == null || db == null) {
            callback.onFailure("Firebase is not configured. Please add google-services.json.");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            createUserDocument(uid, email, firstName, lastName, role, phone, callback);
                        } else {
                            callback.onFailure("User creation failed");
                        }
                    } else {
                        String errorMessage = getErrorMessage(task.getException());
                        Log.e(TAG, "Registration failed", task.getException());
                        callback.onFailure(errorMessage);
                    }
                });
    }

    /**
     * Creates a user document in Firestore
     */
    private void createUserDocument(String uid, String email, String firstName, String lastName,
            String role, String phone, AuthCallback callback) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("firstName", firstName);
        userData.put("lastName", lastName);
        userData.put("role", role.toLowerCase()); // Store role in lowercase
        userData.put("phone", phone);
        userData.put("createdAt", System.currentTimeMillis());

        db.collection(USERS_COLLECTION).document(uid)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User document created successfully");
                    callback.onSuccess(uid);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error creating user document", e);
                    // Delete the auth user if Firestore creation fails
                    if (mAuth.getCurrentUser() != null) {
                        mAuth.getCurrentUser().delete();
                    }
                    callback.onFailure("Failed to create user profile: " + e.getMessage());
                });
    }

    /**
     * Logs in a user with email and password
     * 
     * @param email    User's email address
     * @param password User's password
     * @param callback Callback for success/failure
     */
    public void login(String email, String password, AuthCallback callback) {
        if (mAuth == null) {
            callback.onFailure("Firebase is not configured. Please add google-services.json.");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Log.d(TAG, "Login successful for: " + email);
                            callback.onSuccess(user.getUid());
                        } else {
                            callback.onFailure("Login failed");
                        }
                    } else {
                        String errorMessage = getErrorMessage(task.getException());
                        Log.e(TAG, "Login failed", task.getException());
                        callback.onFailure(errorMessage);
                    }
                });
    }

    /**
     * Fetches user role and profile information from Firestore
     * 
     * @param uid      Firebase user ID
     * @param callback Callback for role fetch
     */
    public void getUserRole(String uid, RoleCallback callback) {
        db.collection(USERS_COLLECTION).document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        String firstName = documentSnapshot.getString("firstName");
                        String phone = documentSnapshot.getString("phone");

                        if (role != null && firstName != null) {
                            Log.d(TAG, "User role fetched: " + role);
                            callback.onRoleFetched(role, firstName, phone != null ? phone : "");
                        } else {
                            callback.onFailure("User profile incomplete");
                        }
                    } else {
                        callback.onFailure("User profile not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching user role", e);
                    callback.onFailure("Failed to fetch user profile: " + e.getMessage());
                });
    }

    /**
     * Logs out the current user
     */
    public void logout() {
        if (mAuth != null) {
            mAuth.signOut();
            Log.d(TAG, "User logged out");
        }
    }

    /**
     * Gets the currently authenticated Firebase user
     * 
     * @return FirebaseUser or null if not authenticated
     */
    public FirebaseUser getCurrentUser() {
        return mAuth != null ? mAuth.getCurrentUser() : null;
    }

    /**
     * Converts Firebase exception to user-friendly error message
     */
    private String getErrorMessage(Exception exception) {
        if (exception == null) {
            return "An unknown error occurred";
        }

        String message = exception.getMessage();
        if (message == null) {
            return "An unknown error occurred";
        }

        // Parse common Firebase error messages
        if (message.contains("email address is already in use")) {
            return "Email already in use";
        } else if (message.contains("password is invalid")) {
            return "Invalid password";
        } else if (message.contains("no user record")) {
            return "No account found with this email";
        } else if (message.contains("badly formatted")) {
            return "Invalid email format";
        } else if (message.contains("weak password")) {
            return "Password is too weak";
        } else if (message.contains("network error")) {
            return "Network error. Please check your connection";
        } else if (message.contains("too many requests")) {
            return "Too many attempts. Please try again later";
        } else {
            return "Authentication failed: " + message;
        }
    }
}
