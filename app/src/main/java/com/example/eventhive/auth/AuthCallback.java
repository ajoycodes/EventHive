package com.example.eventhive.auth;

/**
 * Callback interface for authentication operations
 */
public interface AuthCallback {
    /**
     * Called when authentication operation succeeds
     * 
     * @param uid Firebase user ID
     */
    void onSuccess(String uid);

    /**
     * Called when authentication operation fails
     * 
     * @param errorMessage Error message describing the failure
     */
    void onFailure(String errorMessage);
}
