package gps;

import android.app.Activity;
//import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
//import android.util.Log;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.facebook.react.HeadlessJsTaskService;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

//import static android.content.Context.ACTIVITY_SERVICE;

public class GPSModule extends ReactContextBaseJavaModule {
    private ReactApplicationContext reactContext;
    public GPSModule(@NonNull ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @NonNull
    @Override
    public String getName() {
        return "GPSModule";
    }

    @ReactMethod
    public void createCalendarEvent(String name, String location) {
        Log.d("CalendarModule", "Create event called with name: " + name
                + " and location: " + location);
    }

    @ReactMethod
    public void startTracking(){
        this.reactContext.startService(new Intent(this.reactContext,GPSTrackingService.class));
    }
//    public static final String TAG = "DEVK MAIN";
//    private LocationUpdatesService mService = null;
//    public static final String VEHICLE_ID = "com.smartbus.gps.VEHICLEID";
//    Context context;
//    private Boolean startService = false;
//    public static SQLiteHelper sqLiteHelper;
//
//    public GPSModule(ReactApplicationContext reactContext) {
//        super(reactContext);
//        context = reactContext;
//    }
//    @Nonnull
//    @Override
//    public String getName() {
//        return "LocationGPSModule";
//    }
//    @ReactMethod
//    public void init() {
//        // Log.i("DEVK", "Init service"); //ds
//        insertLocal();
//        activate();
//        context.bindService(new Intent(getReactApplicationContext(), LocationUpdatesService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
//    }
//    public void insertLocal(){
//
//        sqLiteHelper = new SQLiteHelper(getReactApplicationContext(), "RKStorage", null, 1);
//
//        String COORDINATES = sqLiteHelper.selectDataLocalStorage("@COORDINATES");
//        if(COORDINATES == "") sqLiteHelper.insertDataLocalStorage("@COORDINATES", "");
//
//        String position = sqLiteHelper.selectDataLocalStorage("@position");
//        if(position == "") sqLiteHelper.insertDataLocalStorage("@position", "");
//    }
//    public void activate() {
//        final Activity activity = getCurrentActivity();
//        if (activity != null) {
//            // Log.i("DEVK", "activate1");
//            activity.runOnUiThread( new Runnable() {
//                @Override
//                public void run() {
//                    // Log.i("DEVK", "activate");
//                    activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//                }
//            });
//        }
//    }
//
//    @ReactMethod
//    public void updateQuery() {
//        sendEvent("EMMIT_UPDATE_QUERY", "");
//    }
//    public void sendEvent(String eventName, @Nullable String params) {
//      getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
//    }
//    private final ServiceConnection mServiceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
//            mService = binder.getService();
//            mService.requestLocationUpdates();
//            startService = true;
//        }
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            mService = null;
//            startService = false;
//        }
//    };
//    @ReactMethod
//    public void start(){
//        if(mService != null && !startService){
//            activate();
//            //  Log.i("DEVK", "Start service");
//             mService.requestLocationUpdates();
//        }
//    }
//    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
//    @ReactMethod
//    public void disconnectService(){
//        stop();
//        mService.stopForeground(true);
//        // Log.i("DEVK", "disconnectService()");
//    }
//    @ReactMethod
//    public void stop(){
//        startService = false;
//        if(mService != null){
//            deactivate();
//            mService.removeLocationUpdates();
//            // Log.i("DEVK", "Stop service");
//        }
//    }
//    public void deactivate() {
//        final Activity activity = getCurrentActivity();
//        if (activity != null) {
//            activity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    // Log.i("DEVK", "deactivate");
//                    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//                }
//            });
//        }
//    }

}
