package com.ethras.simplepermissions;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;


import androidx.annotation.NonNull;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;

/**
 * SimplePermissionsPlugin
 */
public class SimplePermissionsPlugin implements MethodCallHandler, PluginRegistry.RequestPermissionsResultListener, FlutterPlugin, ActivityAware {
    private Registrar registrar;
    private Result result;
    private static Context mContext;
    private static MethodChannel channel;
    private Activity activity;
    private ActivityPluginBinding activityPluginBinding;
    private static final String CHANNEL_ID = "simple_permissions";

    private static String MOTION_SENSOR = "MOTION_SENSOR";

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), CHANNEL_ID);
        SimplePermissionsPlugin simplePermissionsPluginInstance = new SimplePermissionsPlugin();
        simplePermissionsPluginInstance.initInstance(registrar.messenger(), mContext);
    }


    private void initInstance(BinaryMessenger binaryMessenger, Context context) {
        SimplePermissionsPlugin.channel  = new MethodChannel(binaryMessenger, CHANNEL_ID);
        SimplePermissionsPlugin.channel.setMethodCallHandler(this);
        mContext= context;
    }


    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        initInstance(binding.getBinaryMessenger(), binding.getApplicationContext());
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        SimplePermissionsPlugin.channel.setMethodCallHandler(null);
        SimplePermissionsPlugin.channel = null;
    }
    

    public SimplePermissionsPlugin() {
    }

    @Override
    public void onAttachedToActivity(ActivityPluginBinding activityPluginBinding) {
        // TODO: your plugin is now attached to an Activity
        this.activity=activityPluginBinding.getActivity();
        this.activityPluginBinding=activityPluginBinding;
        activityPluginBinding.addRequestPermissionsResultListener(this);
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        // TODO: the Activity your plugin was attached to was destroyed to change configuration.
        // This call will be followed by onReattachedToActivityForConfigChanges().
        this.activity=null;
    }

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding activityPluginBinding) {
        // TODO: your plugin is now attached to a new Activity after a configuration change.

        this.activity=activityPluginBinding.getActivity();
        this.activityPluginBinding=activityPluginBinding;
        activityPluginBinding.addRequestPermissionsResultListener(this);
    }

    @Override
    public void onDetachedFromActivity() {
        // TODO: your plugin is no longer associated with an Activity. Clean up references.
        this.activity=null;
    }
    

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        String method = call.method;
        String permission;
        switch (method) {
            case "getPlatformVersion":
                result.success("Android " + android.os.Build.VERSION.RELEASE);
                break;
            case "getPermissionStatus":
                permission = call.argument("permission");
                if (MOTION_SENSOR.equalsIgnoreCase(permission)) {
                    result.success(3);
                    break;
                }
                int value = checkPermission(permission) ? 3 : 2;
                result.success(value);
                break;
            case "checkPermission":
                permission = call.argument("permission");
                if (MOTION_SENSOR.equalsIgnoreCase(permission)) {
                    result.success(true);
                    break;
                }
                result.success(checkPermission(permission));
                break;
            case "requestPermission":
                permission = call.argument("permission");
                if (MOTION_SENSOR.equalsIgnoreCase(permission)) {
                    result.success(3);
                    break;
                }
                this.result = result;
                requestPermission(permission);
                break;
            case "openSettings":
                openSettings();
                result.success(true);
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    private void openSettings() {
        
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + this.activity.getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.activity.startActivity(intent);
    }

    private String getManifestPermission(String permission) {
        String res;
        switch (permission) {
            case "RECORD_AUDIO":
                res = Manifest.permission.RECORD_AUDIO;
                break;
            case "CALL_PHONE":
                res = Manifest.permission.CALL_PHONE;
                break;
            case "CAMERA":
                res = Manifest.permission.CAMERA;
                break;
            case "WRITE_EXTERNAL_STORAGE":
                res = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                break;
            case "READ_EXTERNAL_STORAGE":
                res = Manifest.permission.READ_EXTERNAL_STORAGE;
                break;
            case "READ_PHONE_STATE":
                res = Manifest.permission.READ_PHONE_STATE;
                break;
            case "ACCESS_FINE_LOCATION":
                res = Manifest.permission.ACCESS_FINE_LOCATION;
                break;
            case "ACCESS_COARSE_LOCATION":
                res = Manifest.permission.ACCESS_COARSE_LOCATION;
                break;
            case "WHEN_IN_USE_LOCATION":
                res = Manifest.permission.ACCESS_FINE_LOCATION;
                break;
            case "ALWAYS_LOCATION":
                res = Manifest.permission.ACCESS_FINE_LOCATION;
                break;
            case "READ_CONTACTS":
                res = Manifest.permission.READ_CONTACTS;
                break;
            case "SEND_SMS":
                res = Manifest.permission.SEND_SMS;
                break;
            case "READ_SMS":
                res = Manifest.permission.READ_SMS;
                break;
            case "VIBRATE":
                res = Manifest.permission.VIBRATE;
                break;
            case "WRITE_CONTACTS":
                res = Manifest.permission.WRITE_CONTACTS;
                break;
            default:
                res = "ERROR";
                break;
        }
        return res;
    }

    private void requestPermission(String permission) {
        permission = getManifestPermission(permission);
        String[] perm = {permission};
        ActivityCompat.requestPermissions(this.activity, perm, 0);
    }

    private boolean checkPermission(String permission) {
        
        permission = getManifestPermission(permission);
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(activity, permission);
    }

    @Override
    public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        int status = 0;
        String permission = null;
        if (permissions != null && permissions.length > 0) {
            
            permission = permissions[0];
        
            if (requestCode == 0 && grantResults.length > 0) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this.activity, permission)) {
                    //denied
                    status = 2;
                } else {
                    if (ActivityCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED) {
                        //allowed
                        status = 3;
                    } else {
                        //set to never ask again
                        Log.e("SimplePermission", "set to never ask again" + permission);
                        status = 4;
                    }
                }
            }
        }
        Log.i("SimplePermission", "Requesting permission status : " + status);
        Result result = this.result;
        this.result = null;
        if(result != null) {
            result.success(status);
        }
        return status == 3;
    }
}
