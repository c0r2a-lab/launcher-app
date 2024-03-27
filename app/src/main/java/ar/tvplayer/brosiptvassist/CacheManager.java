package ar.tvplayer.brosiptvassist;

import android.content.Context;
import android.content.pm.PackageManager;

import java.io.File;
import java.util.Objects;

public class CacheManager {

    // Clear the cache for a specific app
    public static void clearCacheForApp(Context context, String packageName) {
        try {
            // Get the package manager
            PackageManager pm = context.getPackageManager();

            // Get the data directory for the app
            String dataDir = null;
            try {
                dataDir = pm.getApplicationInfo(packageName, 0).dataDir;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if (dataDir != null) {
                // Clear the cache directory
                File cacheDir = new File(dataDir, "cache");
                if (cacheDir.exists()) {
                    deleteDir(cacheDir);
                }

                // Clear the external cache directory
                File externalCacheDir = context.getExternalCacheDir();
                if (externalCacheDir != null && externalCacheDir.exists()) {
                    deleteDir(externalCacheDir);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Recursively delete a directory and its contents
    private static void deleteDir(File dir) {
        if(dir.exists()) {
            if (dir.isDirectory()) {
                String[] children = dir.list();
                for (String child : Objects.requireNonNull(children)) {
                    File childFile = new File(dir, child);
                    if (childFile.exists()) {
                        if (childFile.isDirectory()) {
                            deleteDir(childFile);
                        } else {
                            childFile.delete();
                        }
                    }
                }
            }
            dir.delete();
        }
    }
}
