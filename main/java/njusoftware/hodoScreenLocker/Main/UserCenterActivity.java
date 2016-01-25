package njusoftware.hodoScreenLocker.Main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import njusoftware.hodoScreenLocker.R;

public class UserCenterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);
        Intent intent = new Intent(UserCenterActivity.this, BackgroundService.class);
        startService(intent);
        Log.i("ECHO", "Main Task id:" + getPackageName());
    }
}
