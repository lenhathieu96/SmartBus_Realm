package com.smartbus_realm.GPS;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.facebook.react.HeadlessJsTaskService;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.smartbus_realm.MainActivity;
import com.smartbus_realm.Model.Vehicle;
import com.smartbus_realm.R;


import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;


public class GPSTrackingService extends Service {

    private static final int SERVICE_NOTIFICATION_ID = 123456;
    private static  String CHANNEL_ID;

    private Vehicle vehicle;
    private double vehicleLatitude;
    private double vehicleLongitude;

    Gson gson = new Gson();
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("https://node.busmap.com.vn:2399");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback = new LocationCallback();
    private LocationRequest locationRequest = LocationRequest.create();
    private Location currentLocation;

    protected void createLocationRequest() {
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    //Set up foreground service for api 28 +
    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName){
        NotificationChannel chan =  new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);
        return channelId;
    }

    private Handler handler = new Handler();
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            if (currentLocation != null) {
                locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult != null) {
                            Context context = getApplicationContext();
                            Intent service = new Intent(getApplicationContext(), GPSTrackingEventService.class);
                            vehicleLatitude = locationResult.getLastLocation().getLatitude();
                            vehicleLongitude =  locationResult.getLastLocation().getLongitude();

                            vehicle.setCoordinates(vehicleLatitude + ", " + vehicleLongitude);
                            vehicle.setSpeed(locationResult.getLastLocation().getSpeed());
                            vehicle.setTimestamp(locationResult.getLastLocation().getTime());

                            Bundle bundle = new Bundle();
                            bundle.putDouble("latitude", vehicleLatitude );
                            bundle.putDouble("longitude",vehicleLongitude);
                            Log.d("devH", gson.toJson(vehicle));
                            Log.d("devH", String.valueOf(mSocket.connected()));
                            mSocket.emit("receiveDataPos", gson.toJson(vehicle));
                            service.putExtras(bundle);
                            context.startService(service);
                            HeadlessJsTaskService.acquireWakeLockNow(context);
                        }
                    }
                };
                startLocationUpdates();
            } else {
                handler.postDelayed(this, 2000);
            }
        }

    };

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if(location != null){
                mSocket.connect();
                createLocationRequest();
                currentLocation = location;
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();
        Gson gson = new Gson();
        vehicle = gson.fromJson(extras.getString("vehicle"), Vehicle.class);

        this.handler.post(this.runnableCode);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          CHANNEL_ID =   createNotificationChannel("my_service", "My Background Service");
        } else {
           CHANNEL_ID = "GPS_TRACKING";
        }

        // Turning into a foreground service
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("SmartBus")
                .setContentText("Đang hoạt động...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .build();
        startForeground(SERVICE_NOTIFICATION_ID, notification);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
