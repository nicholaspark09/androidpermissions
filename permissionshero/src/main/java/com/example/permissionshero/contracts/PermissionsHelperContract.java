package com.example.permissionshero.contracts;

import android.support.annotation.NonNull;

/**
 * Created by Nicholas Park on 11/10/17.
 *
 *  Interface that defines the methods a PermissionsHelper
 *  must implement
 */

public interface PermissionsHelperContract {

    void makeRequest(@NonNull String permission);

    void onPermissionRequestResult(int requestCode, @NonNull String[] permissions, @NonNull int[] results);

    boolean isPermissionDeniedForever(@NonNull String permission);

    boolean isPermissionGranted(@NonNull String permission);

    boolean isRationaleNeeded(@NonNull String permission);

    interface Callback {

        void onPermissionGranted();

        void onPermissionDeclined(@NonNull String permission);

        void onPermissionNeedsRationale(@NonNull String permission);

        void onPermissionDeclinedForever(@NonNull String permission);
    }
}
