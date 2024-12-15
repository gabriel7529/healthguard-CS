package com.example.smartcheckup.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Bundle;
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
import androidx.fragment.app.DialogFragment;

import com.example.smartcheckup.R;
import com.example.smartcheckup.fragments.TimePickerFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class useractivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    LinearLayout l1, l2, l3;
    Animation a1, a2, a3;
    static String user;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "useractivity";

    static String uid;
    static int s = 1;
    static int check = 1;
    static String watchid = "";
    int watchset;

    private LocationManager manager;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient fusedLocationClient;
    private FusedLocationProviderClient client;
    private Location mLocation;
    private GoogleApiClient mGoogleApiClient;

    private DatabaseReference locationinfo;

    private long UPDATE_INTERVAL = 2 * 10000;  /* 20 secs */
    private long FASTEST_INTERVAL = 4000; /* 2 sec */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_useractivity);

        // Initialize GoogleApiClient
        initGoogleApiClient();

        Intent i = getIntent();
        uid = i.getStringExtra("uid");
        user = i.getStringExtra("child");

        l1 = findViewById(R.id.l1);
        l2 = findViewById(R.id.l2);
        l3 = findViewById(R.id.l3);
        a1 = AnimationUtils.loadAnimation(useractivity.this, R.anim.topdown);
        a3 = AnimationUtils.loadAnimation(useractivity.this, R.anim.rightside);
        a2 = AnimationUtils.loadAnimation(useractivity.this, R.anim.leftside);
        l1.setAnimation(a1);
        l2.setAnimation(a2);
        l3.setAnimation(a3);

        if (!isLocationEnabled(useractivity.this)) buildDialog(useractivity.this).show();

        DatabaseReference m = FirebaseDatabase.getInstance().getReference().child("Parents").child(uid).child("children").child(user);
        m.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    watchid = dataSnapshot.child("WatchId").getValue().toString();
                    watchset = 1;
                } catch (NullPointerException e) {
                    watchid = "";
                    watchset = 1;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!watchid.equals("")) {
                    Toast.makeText(useractivity.this, watchid + " is choosen", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(useractivity.this, "No Watch is choosen", Toast.LENGTH_SHORT).show();
                    watchset = 1;
                }
            }
        }, 1000);

        locationinfo = FirebaseDatabase.getInstance().getReference().child("Parents").child(uid).child("children").child(user);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    // Initialize GoogleApiClient method
    private void initGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    // Remaining methods from the original code...

    public android.app.AlertDialog.Builder buildDialog(Context c) {
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(c, R.style.TimePickerTheme);
        builder.setTitle("No GPS Connection");
        builder.setMessage("You need to have GPS turned ON to access this. Press OK if done");
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!isLocationEnabled(useractivity.this)) buildDialog(useractivity.this).show();
            }
        }).setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        return builder;
    }

    private boolean isLocationEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onBackPressed() {
        super.onPause();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(useractivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(useractivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(useractivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        mLocation = location;
                        Toast.makeText(this, "Location detected: " + location.getLatitude() + ", " + location.getLongitude(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        startLocationUpdates();
                    }
                })
                .addOnFailureListener(this, e -> {
                    Toast.makeText(this, "Failed to get location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void startLocationUpdates() {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(useractivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(useractivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        locationinfo.child("userloc_LATITUDE").setValue(String.valueOf(location.getLatitude()));
        locationinfo.child("userloc_LONGITUDE").setValue(String.valueOf(location.getLongitude()));
    }

    //OPTIONS FOR WATCH

    public void watchface(View view)
    {

        if(!watchid.equals("")) {
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Parents").child(uid).child("children").child(user);
            final DatabaseReference msg = myRef.child("Watchface");

            PopupMenu p = new PopupMenu(useractivity.this, view);
            p.getMenuInflater().inflate(R.menu.popup, p.getMenu());
            p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getTitle().toString().contains("12 Hour Format")) {
                        msg.setValue(12);
                        Toast.makeText(useractivity.this, "Watchface Changed", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(useractivity.this, "Watchface Changed", Toast.LENGTH_SHORT).show();
                        msg.setValue(24);
                    }
                    return true;
                }
            });
            p.show();
        }
        else
        {
            Toast.makeText(useractivity.this, "No Watch is choosen", Toast.LENGTH_SHORT).show();
        }
    }

    public void heartbeat(View view) {
        Toast.makeText(useractivity.this,"STILL UNDER DEVELOPMENT",Toast.LENGTH_SHORT).show();
    }

    public void Alarm(View view)  {
        if(!watchid.equals("")) {
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Parents").child(uid).child("children").child(user);
            final DatabaseReference msg = myRef.child("useralarmhour");
            final DatabaseReference msg2 = myRef.child("useralarmminute");

            PopupMenu p = new PopupMenu(useractivity.this, view);
            p.getMenuInflater().inflate(R.menu.timepickerpopup, p.getMenu());
            p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getTitle().toString().contains("Set New Alarm")) {
                        s = 1;
                        DialogFragment timepicker = new TimePickerFragment();
                        timepicker.show(getSupportFragmentManager(), "Select Time");

                    } else if (item.getTitle().toString().contains("Show Alarm")) {
                        DatabaseReference childuser = FirebaseDatabase.getInstance().getReference().child("Parents").child(uid).child("children").child(user);
                        childuser.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                s++;
                                if (s == 0) {
                                    Toast.makeText(useractivity.this, "No Alarm Preset", Toast.LENGTH_SHORT).show();
                                } else {
                                    try {
                                        s = 1;
                                        int a = Integer.parseInt(dataSnapshot.child("useralarmhour").getValue().toString());
                                        int b = Integer.parseInt(dataSnapshot.child("useralarmminute").getValue().toString());
                                        if (a == 200 && b == 200) {
                                            Toast.makeText(useractivity.this, "No Alarm Preset", Toast.LENGTH_SHORT).show();
                                        } else {
                                            if (a < 10 && b > 9)
                                                Toast.makeText(useractivity.this, "Your Alarm Is Set At: 0" + dataSnapshot.child("useralarmhour").getValue().toString() + ":" + dataSnapshot.child("useralarmminute").getValue().toString(), Toast.LENGTH_SHORT).show();
                                            else if (b < 10 && a > 9)
                                                Toast.makeText(useractivity.this, "Your Alarm Is Set At: " + dataSnapshot.child("useralarmhour").getValue().toString() + ":0" + dataSnapshot.child("useralarmminute").getValue().toString(), Toast.LENGTH_SHORT).show();
                                            else if (a < 10 && b < 10)
                                                Toast.makeText(useractivity.this, "Your Alarm Is Set At: 0" + dataSnapshot.child("useralarmhour").getValue().toString() + ":0" + dataSnapshot.child("useralarmminute").getValue().toString(), Toast.LENGTH_SHORT).show();
                                            else
                                                Toast.makeText(useractivity.this, "Your Alarm Is Set At: " + dataSnapshot.child("useralarmhour").getValue().toString() + ":" + dataSnapshot.child("useralarmminute").getValue().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (NullPointerException e) {

                                        Toast.makeText(useractivity.this, "No Alarm Preset", Toast.LENGTH_SHORT).show();
                                        s = 0;

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } else {
                        s = 0;
                        msg.setValue(200);
                        msg2.setValue(200);


                    }
                    return true;
                }
            });
            p.show();

        }
        else
            Toast.makeText(useractivity.this, "No Watch is choosen", Toast.LENGTH_SHORT).show();




    }



    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        s=1;
        DatabaseReference myRef=FirebaseDatabase.getInstance().getReference().child("Parents").child(uid).child("children").child(user);
        final DatabaseReference msg = myRef.child("useralarmhour");
        final DatabaseReference msg2 = myRef.child("useralarmminute");
        Toast.makeText(useractivity.this,"Alarm Set",Toast.LENGTH_SHORT).show();
        msg.setValue(hourOfDay);
        msg2.setValue(minute);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        },1000);

    }

    public void logout(View view) {

        user=uid="";
        check=0;
        finish();
    }

    public void pairwatch(View view) {

        if(watchid.equals("")||watchset==1){
            LayoutInflater li = LayoutInflater.from(useractivity.this);
            View pairwatch = li.inflate(R.layout.pairwatchdialouge, null);
            final AlertDialog.Builder alerBuilder = new AlertDialog.Builder(useractivity.this, R.style.watchpair);
            alerBuilder.setView(pairwatch);
            final EditText input = (EditText) pairwatch.findViewById(R.id.watchID);
            alerBuilder.setCancelable(false)
                    .setPositiveButton("SET ID", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            watchid = input.getText().toString();
                            Toast.makeText(useractivity.this, watchid + " is choosen", Toast.LENGTH_SHORT).show();
                            DatabaseReference watchchoosen = FirebaseDatabase.getInstance().getReference().child("CHOOSE_WATCH").child(watchid);
                            DatabaseReference m = FirebaseDatabase.getInstance().getReference().child("Parents").child(uid).child("children").child(user).child("WatchId");
                            watchchoosen.child("userID").setValue(user);
                            watchchoosen.child("parentUID").setValue(uid);
                            m.setValue(watchid);
                            dialog.dismiss();
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    //Toast.makeText(useractivity.this, "No Watch is choosen", Toast.LENGTH_SHORT).show();
                }
            }).create().show();
        }
        else
        {
            Toast.makeText(useractivity.this, watchid + " is choosen\nPRESS BUTTON AGAIN TO CHOOSE NEW WATCH", Toast.LENGTH_SHORT).show();
            watchset=1;
            //watchid="";
        }

    }  //OPTION FOR WATCHCHANGE
}
