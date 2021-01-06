package gps;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.facebook.react.HeadlessJsTaskService;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.jstasks.HeadlessJsTaskConfig;

import java.util.HashMap;
import java.util.Map;

public class GPSTrackingEventService extends HeadlessJsTaskService {
    @Nullable
    @Override
    protected HeadlessJsTaskConfig getTaskConfig(Intent intent) {
        Bundle extras = intent.getExtras();
            return new HeadlessJsTaskConfig(
                    "GPSModule", extras != null ? Arguments.fromBundle(extras) : null, 5000, true
            );
        }


}
