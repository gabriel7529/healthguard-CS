package com.example.smartcheckup.activities;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(AndroidJUnit4.class)
public class ReactivityTest {

    @Mock
    private Context mockContext;

    @Mock
    private LocationManager mockLocationManager;

    @Mock
    private FirebaseDatabase mockFirebaseDatabase;

    @Mock
    private DatabaseReference mockDatabaseReference;

    private Reactivity reactivityActivity;

    @Before
    public void setUp() {
        // Inicializa los mocks
        MockitoAnnotations.initMocks(this);

        // Crea una instancia de la actividad, reemplazando las dependencias
        reactivityActivity = new Reactivity();

    }



    @Test
    public void testLogout() {
        // Configurar estado inicial
        reactivityActivity.user = "testUser";
        reactivityActivity.uid = "testUID";
        reactivityActivity.check = 1;

        // Ejecutar logout
        reactivityActivity.logout(null);

        // Verificaciones
        assertEquals("User debe estar vacío después de logout", "", reactivityActivity.user);
        assertEquals("UID debe estar vacío después de logout", "", reactivityActivity.uid);
        assertEquals("Check debe ser 0 después de logout", 0, reactivityActivity.check);
    }
}
