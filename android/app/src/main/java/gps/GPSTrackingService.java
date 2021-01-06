package gps;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.facebook.react.HeadlessJsTaskService;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.smartbus_realm.MainActivity;
import com.smartbus_realm.R;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;


public class GPSTrackingService extends Service {

    private static final int SERVICE_NOTIFICATION_ID = 123456;
    private static final String CHANNEL_ID = "GPS_TRACKING";

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback = new LocationCallback();
    private LocationRequest locationRequest = LocationRequest.create();
    private Location currentLocation;

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
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("location", locationResult);
                            Log.e("devH", String.valueOf(currentLocation));
                            mSocket.emit("test", locationResult);
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

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("https://ssrestaurant.herokuapp.com/");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    protected void createLocationRequest() {
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

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
        this.handler.post(this.runnableCode);
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
