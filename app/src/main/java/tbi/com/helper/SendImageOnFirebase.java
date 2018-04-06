package tbi.com.helper;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static tbi.com.helper.ImagePicker.decodeBitmap;

public class SendImageOnFirebase {

    public static final String TEMP_IMAGE_NAME = "tempImage.jpg";
    private static final int DEFAULT_MIN_WIDTH_QUALITY = 400;        // min pixels
    private static final int DEFAULT_MIN_HEIGHT_QUALITY = 400;
    private static int minWidthQuality = DEFAULT_MIN_WIDTH_QUALITY;
    private static int minHeightQuality = DEFAULT_MIN_HEIGHT_QUALITY;

    public static boolean hasCameraAccess(Context context) {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean appManifestContainsPermission(Context context, String permission) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] requestedPermissions = null;
            if (packageInfo != null) {
                requestedPermissions = packageInfo.requestedPermissions;
            }
            if (requestedPermissions == null) {
                return false;
            }

            if (requestedPermissions.length > 0) {
                List<String> requestedPermissionsList = Arrays.asList(requestedPermissions);
                return requestedPermissionsList.contains(permission);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static File getTemporalFile(Context context) {
        return new File(context.getExternalCacheDir(), TEMP_IMAGE_NAME);
    }

    /**
     * Resize to avoid using too much memory loading big images (e.g.: 2560*1920)
     **/
    public static Bitmap getImageResized(Context context, Uri selectedImage) {
        Bitmap bm;
        int[] sampleSizes = new int[]{5, 3, 2, 1};
        int i = 0;
        do {
            bm = decodeBitmap(context, selectedImage, sampleSizes[i]);
            i++;
        } while (bm != null
                && (bm.getWidth() < minWidthQuality || bm.getHeight() < minHeightQuality)
                && i < sampleSizes.length);
        return bm;
    }
}
