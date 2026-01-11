package com.example.eventhive.auth;

/**
 * Callback interface for fetching user role from Firestore
 */
public interface RoleCallback {
    /**
     * Called when role is successfully fetched
     * 
     * @param role      User role (user, organizer, or admin)
     * @param firstName User's first name
     * @param phone     User's phone number
     */
    void onRoleFetched(String role, String firstName, String email, String phone);

    /**
     * Called when role fetch fails
     * 
     * @param errorMessage Error message describing the failure
     */
    void onFailure(String errorMessage);
}
