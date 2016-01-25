package njusoftware.hodoScreenLocker.Main;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class BackgroundService extends Service {
    LockerScreenLauncher lsl;
    private static boolean isRegistered = false;

    public BackgroundService() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("ECHO", "Service Stop");
        lsl.unregister(BackgroundService.this);
        startService(new Intent(BackgroundService.this, BackgroundService.class));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("ECHO", "Service Start!!");
        lsl = new LockerScreenLauncher();
        lsl.register(BackgroundService.this);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void launchLockedScreen() {
        Intent intent = new Intent(BackgroundService.this, LockedScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Log.i("ECHO", "Start Activity");
    }

    private class LockerScreenLauncher extends BroadcastReceiver {
        String action;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_OFF) && !LockedScreenActivity.isRun) {
                Log.i("ECHO", "Broadcast Received");
                launchLockedScreen();
            }
        }

        private void register(Context context) {
            if (!isRegistered) {
                isRegistered = true;
                IntentFilter filter = new IntentFilter();
                filter.addAction(Intent.ACTION_SCREEN_OFF);
                context.registerReceiver(LockerScreenLauncher.this, filter);
            }
        }

        private void unregister(Context context) {
            if (isRegistered) {
                isRegistered = false;
                context.unregisterReceiver(LockerScreenLauncher.this);
            }
        }
    }
}
