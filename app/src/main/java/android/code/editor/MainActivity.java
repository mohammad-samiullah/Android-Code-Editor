package android.code.editor;

import android.Manifest;
import android.app.Activity;
import android.code.editor.tsd.StoragePermission;
import android.code.editor.ui.MaterialColorHelper;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity extends AppCompatActivity implements StoragePermission {

    private boolean isRequested;
    private MaterialAlertDialogBuilder MaterialDialog;
    private TextView info;
    private LinearLayout main;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        // Enable logging in Sketchware pro
        // SketchLogger.startLogging();

        super.onCreate(savedInstanceState);
        MaterialColorHelper.setUpTheme(this);
        setContentView(R.layout.activity_main);
        // Navigation
        // modified by ashishtechnozone
        if (Build.VERSION.SDK_INT >= 21) {
            Window w = this.getWindow();
            w.setNavigationBarColor(Color.parseColor("#000000"));
        }
        // StatusBar
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            Window w = this.getWindow();
            w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.setStatusBarColor(Color.parseColor("#000000"));
        }

        if (isStoagePermissionGranted(this)) {
            startActivtyLogic();
        } else {
            _requestStoragePermission(this, 10);
        }
    }

    @Override
    public void startActivtyLogic() {
        info = findViewById(R.id.info);
        main = findViewById(R.id.main);
        info.setVisibility(View.VISIBLE);
        main.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        // TODO: Implement this method
                        Intent intent = new Intent();
                        intent.putExtra(
                                "path",
                                Environment.getExternalStorageDirectory().getAbsolutePath());
                        intent.setClass(MainActivity.this, FileManagerActivity.class);
                        startActivity(intent);
                    }
                });
    }

    public static boolean isStoagePermissionGranted(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(
                                context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {
            return false;
        } else {
            return true;
        }
    }

    public static void _requestStoragePermission(Activity activity, int reqCode) {
        ActivityCompat.requestPermissions(
                activity,
                new String[] {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                reqCode);
    }

    @Override
    public void onRequestPermissionsResult(int arg0, String[] arg1, int[] arg2) {
        super.onRequestPermissionsResult(arg0, arg1, arg2);
        // TODO: Implement this method
        switch (arg0) {
            case 1:
            case -1:
            case 10:
                int Denied = 0;
                for (int position = 0; position < arg2.length; position++) {
                    if (arg2[position] == PackageManager.PERMISSION_DENIED) {
                        Denied++;
                        if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(arg1[position])) {
                            if (Build.VERSION.SDK_INT >= 23) {
                                if (shouldShowRequestPermissionRationale(arg1[position])) {
                                    showRationaleOfStoragePermissionDialog(this);
                                } else {
                                    showStoragePermissionDialogForGoToSettings(this);
                                }
                            }
                        }
                    }
                }
                if (Denied == 0) {
                    startActivtyLogic();
                }
                break;
        }
    }

    /* Show Material Dialog for Storage Permission */

    public static void showStoragePermissionDialog(Activity activity) {
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(activity);
        dialog.setTitle("Storage permission required");
        dialog.setMessage(
                "Storage permission is required please allow app to use storage in next page.");
        dialog.setPositiveButton(
                "Continue",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface _dialog, int _which) {
                        _requestStoragePermission(activity, 1);
                    }
                });
        dialog.setNegativeButton(
                "No thanks",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface _dialog, int _which) {
                        activity.finishAffinity();
                    }
                });
        dialog.create().show();
    }

    public static void showRationaleOfStoragePermissionDialog(Activity activity) {
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(activity);
        dialog.setTitle("Storage permission required");
        dialog.setMessage(
                "Storage permissions is highly recommend for storing and reading files in device.Without this permission you can't use this app.");
        dialog.setPositiveButton(
                "Continue",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface _dialog, int _which) {
                        _requestStoragePermission(activity, 1);
                    }
                });
        dialog.setNegativeButton(
                "No thanks",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface _dialog, int _which) {
                        activity.finishAffinity();
                    }
                });
        dialog.create().show();
    }

    public static void showStoragePermissionDialogForGoToSettings(Activity context) {
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(context);
        dialog.setTitle("Storage permission required");
        dialog.setMessage(
                "Storage permissions is highly recommend for storing and reading files in device.Without this permission you can't use this app.");
        dialog.setPositiveButton(
                "Setting",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface _dialog, int _which) {
                        Intent intent = new Intent();
                        intent.setAction(
                                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        intent.setData(uri);
                        context.startActivity(intent);
                    }
                });
        dialog.setNegativeButton(
                "No thanks",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface _dialog, int _which) {
                        context.finishAffinity();
                    }
                });
        dialog.create().show();
    }
}
