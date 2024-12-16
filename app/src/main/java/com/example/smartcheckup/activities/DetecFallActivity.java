package com.example.smartcheckup.activities;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.smartcheckup.R;
import com.example.smartcheckup.databinding.ActivityDetectFallBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


public class DetecFallActivity extends AppCompatActivity {

    private ActivityDetectFallBinding binding;
    private static final int SEND_SMS_CODE = 23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_fall);

        // Buscar el NavController desde el NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = navHostFragment.getNavController();

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(permsRequestCode, permissions, grantResults);
        SharedPreferences settings = this.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = settings.edit();

        switch (permsRequestCode) {
            case SEND_SMS_CODE:
                boolean SMSAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                editor.putBoolean("send_emergency_contact", SMSAccepted);
                editor.commit();

        }
    }

}
