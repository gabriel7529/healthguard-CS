package com.example.smartcheckup.activities;
import android.Manifest;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.smartcheckup.R;
import com.example.smartcheckup.fragments.TimePickerFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Reactivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener,

        com.google.android.gms.location.LocationListener {
    LinearLayout l1;
    LinearLayout l2;
    LinearLayout l3;
    Animation a1;
    Animation a2;
    Animation a3;
    String user;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private static final String NOTCHOONSEN = "No Watch is choosen";
    private static final String TAG = "useractivity";

    String uid;
    int s = 1;
    int check = 1;
    String watchid = "";
    int watchset;


    private LocationRequest mLocationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, UPDATE_INTERVAL)
            .setMinUpdateIntervalMillis(FASTEST_INTERVAL)
            .build();

    private FusedLocationProviderClient fusedLocationClient;
    private Location mLocation;


    private DatabaseReference locationinfo;

    protected static final long UPDATE_INTERVAL = 20000;  /* 20 secs */
    protected static final long FASTEST_INTERVAL = 4000; /* 2 sec */


    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_useractivity);

        // Inicializar FusedLocationProviderClient en lugar de GoogleApiClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initializeLocation();

        Intent i = getIntent();
        uid = i.getStringExtra("uid");
        user = i.getStringExtra("child");

        l1 = findViewById(R.id.l1);
        l2 = findViewById(R.id.l2);
        l3 = findViewById(R.id.l3);
        a1 = AnimationUtils.loadAnimation(Reactivity.this, R.anim.topdown);
        a3 = AnimationUtils.loadAnimation(Reactivity.this, R.anim.rightside);
        a2 = AnimationUtils.loadAnimation(Reactivity.this, R.anim.leftside);
        l1.setAnimation(a1);
        l2.setAnimation(a2);
        l3.setAnimation(a3);

        if (!isLocationEnabled(Reactivity.this)) buildDialog(Reactivity.this).show();

        DatabaseReference m = FirebaseDatabase.getInstance().getReference().child(getString(R.string.parents)).child(uid).child(getString(R.string.children)).child(user);
        m.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    watchid = Objects.requireNonNull(dataSnapshot.child("WatchId").getValue()).toString();
                    watchset = 1;
                } catch (NullPointerException e) {
                    watchid = "";
                    watchset = 1;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Registra el error para su análisis posterior
                Log.e("FirebaseError", "Error retrieving data: " + databaseError.getMessage());

                // Muestra un mensaje de error al usuario
                Toast.makeText(Reactivity.this, "Failed to retrieve data. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!watchid.isEmpty()) {
                Toast.makeText(Reactivity.this, watchid + " is choosen", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Reactivity.this, NOTCHOONSEN, Toast.LENGTH_SHORT).show();
                watchset = 1;
            }
        }, 1000);

        locationinfo = FirebaseDatabase.getInstance().getReference().child(getString(R.string.parents)).child(uid).child(getString(R.string.children)).child(user);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        createLocationRequest();
        createLocationCallback();
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, UPDATE_INTERVAL)
                .setMinUpdateIntervalMillis(FASTEST_INTERVAL)
                .build();
    }

    private void initializeLocation() {
        if (!hasLocationPermissions()) {
            requestLocationPermissions();
            return;
        }

        if (fusedLocationClient != null) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            mLocation = location;
                            Toast.makeText(Reactivity.this,
                                    "Location detected: " + mLocation.getLatitude() + ", " + mLocation.getLongitude(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            startLocationUpdates(this);
                        }
                    })
                    .addOnFailureListener(this, e -> {
                        Toast.makeText(Reactivity.this,
                                "Failed to get location: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();

                        // Intentar actualizaciones de ubicación si no se puede obtener la última ubicación
                        startLocationUpdates(this);
                    });
        } else {
            Toast.makeText(Reactivity.this,
                    "Location client not initialized.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult.getLocations().isEmpty()) return;

                Location location = locationResult.getLastLocation();
                if (location != null) {
                    onLocationChanged(location);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkLocationSettings();
    }

    private void checkLocationSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(this)
                .checkLocationSettings(builder.build());

        task.addOnCompleteListener(task1 -> {
            try {
                // Si la configuración es adecuada, comenzamos las actualizaciones de ubicación
                startLocationUpdates();
            } catch (Exception e) {
                Log.e(TAG, "Error checking location settings", e);
            }
        });
    }

    protected void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissions();
            return;
        }

        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Detener actualizaciones de ubicación
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    // Método para obtener la última ubicación conocida
    public void getLastKnownLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissions();
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        onLocationChanged(location);
                    }
                })
                .addOnFailureListener(this, e -> Toast.makeText(Reactivity.this,
                        "Error obteniendo ubicación: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
    }



    public android.app.AlertDialog.Builder buildDialog(Context c) {
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(c, R.style.TimePickerTheme);
        builder.setTitle("No GPS Connection");
        builder.setMessage("You need to have GPS turned ON to access this. Press OK if done");
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", (dialog, which) -> {
            if (!isLocationEnabled(Reactivity.this)) buildDialog(Reactivity.this).show();
        }).setNegativeButton("EXIT", (dialog, which) -> finish());

        return builder;
    }

    protected boolean isLocationEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }



    private boolean hasLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        return false;
    }

    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }






    protected void startLocationUpdates(Context context) {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // No hay permisos, salir
            return;
        }

        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        }

        // Preparar el LocationCallback

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;

                for (Location location : locationResult.getLocations()) {
                    Log.d("LocationUpdate",
                            "Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude());
                }
            }
        };

        // Solicitar actualizaciones
        fusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.getMainLooper());
    }

    @Override
    public void onLocationChanged(Location location) {
        locationinfo.child("userloc_LATITUDE").setValue(String.valueOf(location.getLatitude()));
        locationinfo.child("userloc_LONGITUDE").setValue(String.valueOf(location.getLongitude()));
    }

    //OPTIONS FOR WATCH

    public void watchface(View view)
    {

        if(!watchid.isEmpty()) {
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.parents)).child(uid).child(getString(R.string.children)).child(user);
            final DatabaseReference msg = myRef.child("Watchface");

            PopupMenu p = new PopupMenu(Reactivity.this, view);
            p.getMenuInflater().inflate(R.menu.popup, p.getMenu());
            p.setOnMenuItemClickListener(item -> {
                if (Objects.requireNonNull(item.getTitle()).toString().contains("12 Hour Format")) {
                    msg.setValue(12);
                    Toast.makeText(Reactivity.this, "Watchface Changed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Reactivity.this, "Watchface Changed", Toast.LENGTH_SHORT).show();
                    msg.setValue(24);
                }
                return true;
            });
            p.show();
        }
        else
        {
            Toast.makeText(Reactivity.this, NOTCHOONSEN, Toast.LENGTH_SHORT).show();
        }
    }

    public void heartbeat(View view) {
        Toast.makeText(Reactivity.this,"STILL UNDER DEVELOPMENT",Toast.LENGTH_SHORT).show();
    }

    public void alarm(View view) {
        if (watchid.isEmpty()) {
            Toast.makeText(Reactivity.this, NOTCHOONSEN, Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.parents)).child(uid)
                .child(getString(R.string.children)).child(user);
        final DatabaseReference alarmHourRef = myRef.child(getString(R.string.useralarmhour));
        final DatabaseReference alarmMinuteRef = myRef.child(getString(R.string.useralarmminute));

        PopupMenu popupMenu = new PopupMenu(Reactivity.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.timepickerpopup, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> handleMenuItemClick(item, alarmHourRef, alarmMinuteRef));
        popupMenu.show();
    }

    private boolean handleMenuItemClick(MenuItem item, DatabaseReference alarmHourRef, DatabaseReference alarmMinuteRef) {
        String title = Objects.requireNonNull(item.getTitle()).toString();

        if (title.contains("Set New Alarm")) {
            showTimePicker();
        } else if (title.contains("Show Alarm")) {
            fetchAndDisplayAlarm();
        } else {
            resetAlarm(alarmHourRef, alarmMinuteRef);
        }
        return true;
    }

    private void showTimePicker() {
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "Select Time");
    }

    private void fetchAndDisplayAlarm() {
        DatabaseReference childUserRef = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.parents)).child(uid)
                .child(getString(R.string.children)).child(user);

        childUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                displayAlarm(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log or handle error
            }
        });
    }

    private void displayAlarm(DataSnapshot dataSnapshot) {
        try {
            int hour = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child(getString(R.string.useralarmhour)).getValue()).toString());
            int minute = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child(getString(R.string.useralarmminute)).getValue()).toString());

            if (hour == 200 && minute == 200) {
                Toast.makeText(Reactivity.this, getString(R.string.no_alarm_preset), Toast.LENGTH_SHORT).show();
            } else {
                String formattedTime = formatTime(hour, minute);
                Toast.makeText(Reactivity.this, "Your Alarm Is Set At: " + formattedTime, Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException e) {
            Toast.makeText(Reactivity.this, getString(R.string.no_alarm_preset), Toast.LENGTH_SHORT).show();
        }
    }

    private String formatTime(int hour, int minute) {
        return String.format("%02d:%02d", hour, minute);
    }

    private void resetAlarm(DatabaseReference alarmHourRef, DatabaseReference alarmMinuteRef) {
        alarmHourRef.setValue(200);
        alarmMinuteRef.setValue(200);
        Toast.makeText(Reactivity.this, "Alarm reset successfully.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        s=1;
        DatabaseReference myRef=FirebaseDatabase.getInstance().getReference().child(getString(R.string.parents)).child(uid).child("children").child(user);
        final DatabaseReference msg = myRef.child("useralarmhour");
        final DatabaseReference msg2 = myRef.child(getString(R.string.useralarmminute));
        Toast.makeText(Reactivity.this,"Alarm Set",Toast.LENGTH_SHORT).show();
        msg.setValue(hourOfDay);
        msg2.setValue(minute);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

        }, 1000);
    }

    public void logout(View view) {
        user=uid="";
        check=0;
        finish();
    }

    public void pairwatch(View view) {

        if(watchid.isEmpty() ||watchset==1){
            LayoutInflater li = LayoutInflater.from(Reactivity.this);
            View pairwatch = li.inflate(R.layout.pairwatchdialouge, null);
            final AlertDialog.Builder alerBuilder = new AlertDialog.Builder(Reactivity.this, R.style.watchpair);
            alerBuilder.setView(pairwatch);
            final EditText input = (EditText) pairwatch.findViewById(R.id.watchID);
            alerBuilder.setCancelable(false)
                    .setPositiveButton("SET ID", (dialog, which) -> {
                        watchid = input.getText().toString();
                        Toast.makeText(Reactivity.this, watchid + " is choosen", Toast.LENGTH_SHORT).show();
                        DatabaseReference watchchoosen = FirebaseDatabase.getInstance().getReference().child("CHOOSE_WATCH").child(watchid);
                        DatabaseReference m = FirebaseDatabase.getInstance().getReference().child(getString(R.string.parents)).child(uid).child("children").child(user).child("WatchId");
                        watchchoosen.child("userID").setValue(user);
                        watchchoosen.child("parentUID").setValue(uid);
                        m.setValue(watchid);
                        dialog.dismiss();
                    }).setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss()).create().show();
        }
        else
        {
            Toast.makeText(Reactivity.this, watchid + " is choosen\nPRESS BUTTON AGAIN TO CHOOSE NEW WATCH", Toast.LENGTH_SHORT).show();
            watchset=1;

        }

    }  //OPTION FOR WATCHCHANGE
}
