//package com.smartbus_realm.gps;
//
//
//import android.app.ActivityManager;
//
//
//import android.app.Service;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//
//import android.location.Location;
//import android.os.Binder;
//
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.os.IBinder;
//import android.os.Looper;
//
//
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//
//import androidx.localbroadcastmanager.content.LocalBroadcastManager;
//
//import com.facebook.react.ReactInstanceManager;
//import com.facebook.react.ReactNativeHost;
//import com.facebook.react.bridge.ReactContext;
//import com.facebook.react.modules.core.DeviceEventManagerModule;
//
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.smartbus_realm.MainApplication;
//
//import org.json.JSONObject;
//
//
////import static com.smartbus_realm.gps.GPSModule.VEHICLE_ID;
////import static com.smartbus_realm.gps.GPSModule.sqLiteHelper;
//
//
//public class LocationUpdatesService extends Service {
//
//    private static final String PACKAGE_NAME = "com.google.android.gms.location.sample.locationupdatesforegroundservice";
//    private static final String TAG = "DEVK";
//    public static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";
//    public static String EXTRA_LOCATION = PACKAGE_NAME + ".location";
//    private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME + ".started_from_notification";
//
//    private final IBinder mBinder = new LocalBinder();
//    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 2000;
//    private LocationRequest mLocationRequest;
//    private FusedLocationProviderClient mFusedLocationClient;
//    private LocationCallback mLocationCallback;
//    private Handler mServiceHandler;
//    private Location mLocation;
//    private static JSONObject json_bus_map;
//
//    public LocationUpdatesService() {
//    }
//    @Override
//    public void onCreate() {
//        IntentFilter intentFilterVehicle = new IntentFilter(VEHICLE_ID);
//        registerReceiver(mRecevicervehicle, intentFilterVehicle);
//
//
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        mLocationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//                onNewLocation(locationResult.getLastLocation());
//            }
//        };
//        createLocationRequest();
//        getLastLocation();
//        HandlerThread handlerThread = new HandlerThread(TAG);
//        handlerThread.start();
//        mServiceHandler = new Handler(handlerThread.getLooper());
//    }
//
//    BroadcastReceiver mRecevicervehicle = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if(intent.getAction().equals(VEHICLE_ID)){
//                String json_object = intent.getStringExtra("JSON_OBJECT");
//                try {
//                    JSONObject jsonObject = new JSONObject(json_object);
//                    json_bus_map = jsonObject;
//                    Log.e("DEVK: ", jsonObject+"");
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        }
//    };
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION, false);
//        if (startedFromNotification) {
//            removeLocationUpdates();
//            stopSelf();
//        }
//        return START_NOT_STICKY;
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        // Log.i(TAG, "in onBind() 1");
//        stopForeground(true);
//        return mBinder;
//    }
//    @Override
//    public void onDestroy() {
//        mServiceHandler.removeCallbacksAndMessages(null);
//        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
//    }
//
//    public void requestLocationUpdates() {
//        // Log.i(TAG, "Requesting location updates 2");
//        Utils.setRequestingLocationUpdates(this, true);
//        startService(new Intent(getApplicationContext(), LocationUpdatesService.class));
//        try {
//            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
//        } catch (SecurityException unlikely) {
//            Utils.setRequestingLocationUpdates(this, false);
//            // Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
//        }
//    }
//    public void removeLocationUpdates() {
//        // Log.i(TAG, "Removing location updates");
//        try {
//            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
//            Utils.setRequestingLocationUpdates(this, false);
//            stopSelf();
//        } catch (SecurityException unlikely) {
//            Utils.setRequestingLocationUpdates(this, true);
//            // Log.e(TAG, "Lost location permission. Could not remove updates. " + unlikely);
//        }
//    }
//
//    private void getLastLocation() {
//        try {
//            mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
//                @Override
//                public void onComplete(@NonNull Task<Location> task) {
//                    if (task.isSuccessful() && task.getResult() != null) {
//                        mLocation = task.getResult();
//                    } else {
//                        // Log.w(TAG, "Failed to get location.");
//                    }
//                }
//            });
//        } catch (SecurityException unlikely) {
//            // Log.e(TAG, "Lost location permission." + unlikely);
//        }
//    }
//    private void onNewLocation(Location location) {
//        mLocation = location;
//        Intent intent = new Intent(ACTION_BROADCAST);
//        intent.putExtra(EXTRA_LOCATION, location);
//        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
//        String params = "{\"lat\":" + location.getLatitude() +
//                        ",\"lng\":" + location.getLongitude() +
//                        ",\"speed\":" + location.getSpeed() +
//                        ",\"accuracy\":" + location.getAccuracy() + "}";
//
//        String position = "{\"lat\":" + location.getLatitude() + ",\"lng\":" + location.getLongitude()+ "}";
//        sendEvent("EMIT_LOCATION", params);
//        sqLiteHelper.updateDataLocalStorage("@COORDINATES", params);
//        sqLiteHelper.updateDataLocalStorage("@position", position);
//    }
//
//    public void sendEvent(String eventName, String params) {
//        MainApplication application = (MainApplication) this.getApplication();
//        ReactNativeHost reactNativeHost = application.getReactNativeHost();
//        ReactInstanceManager reactInstanceManager = reactNativeHost.getReactInstanceManager();
//        ReactContext reactContext = reactInstanceManager.getCurrentReactContext();
//        if (reactContext != null) {
//            reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
//        }
//    }
//    private void createLocationRequest() {
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//    }
//    public class LocalBinder extends Binder {
//        LocationUpdatesService getService() {
//            return LocationUpdatesService.this;
//        }
//    }
//    public boolean serviceIsRunningInForeground(Context context) {
//        ActivityManager manager = (ActivityManager) context.getSystemService(
//                Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
//                Integer.MAX_VALUE)) {
//            if (getClass().getName().equals(service.service.getClassName())) {
//                if (service.foreground) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//}
