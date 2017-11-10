package com.example.permissionshero;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.example.permissionshero.contracts.PermissionsHelperContract;

/**
 * Created by Nicholas Park on 11/10/17.
 *
 * Permissions helper class for activities
 *  Handles logic for making permissions requests
 */

public class PermissionsActivityHelper implements PermissionsHelperContract {

    private static final int RC_PERMISSIONS = 1;

    @NonNull private final Activity mContext;
    @NonNull private final Callback mCallback;
    private boolean mHasShownRationale = false;

    private PermissionsActivityHelper(@NonNull Activity context) {
        mContext = context;
        if (mContext instanceof Callback) {
            mCallback = (Callback) context;
        } else {
            throw new IllegalArgumentException("Fragment must implement (PermissionsHelper.Callback)");
        }
    }

    public static PermissionsActivityHelper newInstance(@NonNull Activity context) {
        return new PermissionsActivityHelper(context);
    }

    @Override
    public void makeRequest(@NonNull String permission) {
        if (isPermissionGranted(permission)) {
            mCallback.onPermissionGranted();
        } else if (isRationaleNeeded(permission) && !mHasShownRationale) {
            mCallback.onPermissionNeedsRationale(permission);
        } else if (isPermissionDeniedForever(permission)) {
            mCallback.onPermissionDeclinedForever(permission);
        } else {
            requestPermission(permission);
        }
    }

    private void requestPermission(@NonNull String permission) {
        ActivityCompat.requestPermissions(mContext, new String[]{permission}, RC_PERMISSIONS);
    }

    @Override
    public void onPermissionRequestResult(int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        if (requestCode == RC_PERMISSIONS) {
            final String permission = permissions[0];
            if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
                mCallback.onPermissionGranted();
            } else if (isRationaleNeeded(permission) && !mHasShownRationale) {
                mCallback.onPermissionNeedsRationale(permission);
            } else if (isPermissionDeniedForever(permission)) {
                mCallback.onPermissionDeclinedForever(permission);
            } else {
                mCallback.onPermissionDeclined(permission);
            }
        }
    }

    @Override
    public boolean isPermissionDeniedForever(@NonNull String permission) {
        return isPermissionGranted(permission) && !isRationaleNeeded(permission);
    }

    @Override
    public boolean isPermissionGranted(@NonNull String permission) {
        return ActivityCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean isRationaleNeeded(@NonNull String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(mContext, permission);
    }
}
