package com.example.eventhive.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility class for managing event image storage in internal storage.
 * Handles saving images from URIs and loading them back as Bitmaps.
 */
public class ImageStorageHelper {

    private static final String TAG = "ImageStorageHelper";
    private static final String IMAGE_DIRECTORY = "event_images";

    /**
     * Saves an image from URI to internal storage.
     * 
     * @param context  Application context
     * @param imageUri URI of the image to save
     * @return File path of the saved image, or null if failed
     */
    public static String saveImageToInternalStorage(Context context, Uri imageUri) {
        if (context == null || imageUri == null) {
            Log.e(TAG, "Context or URI is null");
            return null;
        }

        try {
            // Create image directory if it doesn't exist
            File directory = new File(context.getFilesDir(), IMAGE_DIRECTORY);
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    Log.e(TAG, "Failed to create directory: " + directory.getAbsolutePath());
                    return null;
                }
            }

            // Generate unique filename using timestamp
            String filename = "img_" + System.currentTimeMillis() + ".jpg";
            File outputFile = new File(directory, filename);

            // Copy image from URI to internal storage
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            if (inputStream == null) {
                Log.e(TAG, "Failed to open input stream for URI: " + imageUri);
                return null;
            }

            OutputStream outputStream = new FileOutputStream(outputFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            Log.d(TAG, "Image saved successfully: " + outputFile.getAbsolutePath());
            return outputFile.getAbsolutePath();

        } catch (Exception e) {
            Log.e(TAG, "Error saving image: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Loads an image from internal storage as a Bitmap.
     * 
     * @param context  Application context
     * @param filePath Absolute file path of the image
     * @return Bitmap of the image, or null if failed
     */
    public static Bitmap loadImageFromInternalStorage(Context context, String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            Log.w(TAG, "File path is null or empty");
            return null;
        }

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                Log.w(TAG, "File does not exist: " + filePath);
                return null;
            }

            FileInputStream inputStream = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            return bitmap;

        } catch (Exception e) {
            Log.e(TAG, "Error loading image: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Deletes an image file from internal storage.
     * 
     * @param context  Application context
     * @param filePath Absolute file path of the image to delete
     * @return true if deletion was successful, false otherwise
     */
    public static boolean deleteImage(Context context, String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            Log.w(TAG, "File path is null or empty");
            return false;
        }

        try {
            File file = new File(filePath);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (deleted) {
                    Log.d(TAG, "Image deleted successfully: " + filePath);
                } else {
                    Log.w(TAG, "Failed to delete image: " + filePath);
                }
                return deleted;
            } else {
                Log.w(TAG, "File does not exist: " + filePath);
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error deleting image: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Checks if an image file exists at the given path.
     * 
     * @param filePath Absolute file path to check
     * @return true if file exists, false otherwise
     */
    public static boolean imageExists(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    /**
     * Gets the total size of all images in the event images directory.
     * 
     * @param context Application context
     * @return Total size in bytes
     */
    public static long getTotalImageStorageSize(Context context) {
        try {
            File directory = new File(context.getFilesDir(), IMAGE_DIRECTORY);
            if (!directory.exists()) {
                return 0;
            }

            long totalSize = 0;
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        totalSize += file.length();
                    }
                }
            }
            return totalSize;

        } catch (Exception e) {
            Log.e(TAG, "Error calculating storage size: " + e.getMessage(), e);
            return 0;
        }
    }
}
