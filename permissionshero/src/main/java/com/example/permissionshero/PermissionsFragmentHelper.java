package com.example.permissionshero;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import com.example.permissionshero.contracts.PermissionsHelperContract;

/**
 * Created by Nicholas Park on 11/10/17.
 *
 *  Permissions helper for fragments in requesting permissions
 */

public class PermissionsFragmentHelper implements PermissionsHelperContract {

    private static final int RC_PERMISSIONS = 1;

    @NonNull private final Fragment mFragment;
    @NonNull private final PermissionsHelperContract.Callback mCallback;
    private boolean mHasShownRationale = false;

    private PermissionsFragmentHelper(@NonNull Fragment fragment) {
        mFragment = fragment;
        if (mFragment instanceof Callback) {
            mCallback = (Callback) mFragment;
        } else {
            throw new IllegalArgumentException("Fragment must implement (PermissionsHelper.Callback)");
        }
    }

    public static PermissionsFragmentHelper newInstance(@NonNull Fragment fragment) {
        return new PermissionsFragmentHelper(fragment);
    }

    /**
     * Makes the original request for the permission
     *
     * @param permission
     */
    @Override
    public void makeRequest(@NonNull String permission) {
        if (isPermissionGranted(permission)) {
            mCallback.onPermissionGranted();
        } else if (isRationaleNeeded(permission) && !mHasShownRationale) {
            mCallback.onPermissionNeedsRationale(permission);
            mHasShownRationale = true;
        } else {
            requestPermission(permission);
        }
    }

    /**
     * Handles the results from the request permission
     *
     * @param requestCode
     * @param permissions
     * @param results
     */
    @Override
    public void onPermissionRequestResult(int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        if (requestCode == RC_PERMISSIONS) {
            final String permission = permissions[0];
            if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
                mCallback.onPermissionGranted();
            } else if (isRationaleNeeded(permission) && !mHasShownRationale) {
                mCallback.onPermissionNeedsRationale(permission);
                mHasShownRationale = true;
            } else if (isPermissionDeniedForever(permission)) {
                mCallback.onPermissionDeclinedForever(permission);
            } else {
                mCallback.onPermissionDeclined(permission);
            }
        }
    }

    /**
     * Internal
     * Calls Fragment.requestPermissions to actually request the permission
     *
     * @param permission
     */
    private void requestPermission(@NonNull String permission) {
        mFragment.requestPermissions(new String[]{permission}, RC_PERMISSIONS);
    }

    @Override
    public boolean isRationaleNeeded(@NonNull String permission) {
        return mFragment.shouldShowRequestPermissionRationale(permission);
    }

    @Override
    public boolean isPermissionDeniedForever(@NonNull String permission) {
        return !isPermissionGranted(permission) && !isRationaleNeeded(permission);
    }

    @Override
    public boolean isPermissionGranted(@NonNull String permission) {
        return ActivityCompat.checkSelfPermission(mFragment.getContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }
}
