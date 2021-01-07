package com.smartbus_realm.GPS;

//import android.app.ActivityManager;
import android.content.Intent;
//import android.util.Log;
import android.os.Bundle;
import android.util.Log;

        import androidx.annotation.NonNull;

        import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.google.gson.Gson;
import com.smartbus_realm.Model.Vehicle;

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
    public void startTracking(String vehicleData){
        Bundle bundle = new Bundle();
        bundle.putString("vehicle", vehicleData);
        Intent intent = new Intent(this.reactContext,GPSTrackingService.class);
        intent.putExtras(bundle);
        this.reactContext.startService(intent);
    }
}
