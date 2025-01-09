package com.example.progetto_java.ui.home;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.progetto_java.R;
import com.example.progetto_java.model.Oggetto;
import com.example.progetto_java.model.OggettoModel;
import com.example.progetto_java.model.Utente;
import com.example.progetto_java.model.UtenteModel;
import com.example.progetto_java.ui.vicinanze.VicinanzeAdapter;
import com.example.progetto_java.ui.vicinanze.VicinanzeFragment;
import com.example.progetto_java.ui.vicinanze.VicinanzeOggettoFragment;
import com.example.progetto_java.ui.vicinanze.VicinanzeViewModel;
import com.google.android.gms.location.*;
import com.example.progetto_java.databinding.FragmentHomeBinding;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private FragmentHomeBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap googleMap;
    private HomeViewModel homeViewModel;
    private VicinanzeViewModel vicinanzeViewModel;
    Location nuovaPosizione;

    private ProgressBar spinner;
    private View overlay;

    private boolean isFirstZoom = true;

    private LatLng newLatLng;

    private Handler handler = new Handler();
    private Runnable updateRunnable;

    private UtenteModel utentiDb;
    private OggettoModel oggettiDb;



    //ho una callback perchè la richiesta DI AGGIORNAMENTO DI POSIZIONE
    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
            Log.d(TAG, "onLocationAvailability " + locationAvailability.isLocationAvailable());
        }

        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            nuovaPosizione = locationResult.getLastLocation();
            Log.d(TAG, "onLocationResult : " + nuovaPosizione.toString());

            if (googleMap != null) {
                // Aggiorna la posizione della mappa con la nuova posizione solo se la mappa è pronta
                updateMapLocation(nuovaPosizione);
            }
        }
    };
    SharedPreferences PreferenceSID;
    String defaultValue = "default";
    private List<Oggetto> listaOggetti;  // Dichiarazione della variabile di istanza

    private List<Utente> listaUtenti;  // Dichiarazione della variabile di istanza

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        PreferenceSID = requireContext().getSharedPreferences("myPreferences", MODE_PRIVATE);
        utentiDb = Room.databaseBuilder(requireContext(), UtenteModel.class, "utenti")
                .build();
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.setContext(requireContext());
        homeViewModel.salvaSID(PreferenceSID, defaultValue);


        vicinanzeViewModel = new ViewModelProvider(this).get(VicinanzeViewModel.class);
        vicinanzeViewModel.setContext(requireContext());
        oggettiDb = Room.databaseBuilder(requireContext(), OggettoModel.class, "oggetti")
                .build();

        Log.d("infoUser", "SID: " + PreferenceSID.getString("SID", defaultValue));


        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        //verifico se l'utente ha già concesso i permessi
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //richiedo accesso ai permessi

            ActivityResultLauncher<String[]> locationPermissionRequest =
                    registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                            (result) -> onPermissionRequestResult(result));

            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });

        } else {
            //richiedo la posizione
            requestPosition();
        }

        // Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e(TAG, "SupportMapFragment is null");
        }


        homeViewModel.getListaUtentiLiveData().observeForever(new Observer<List<Utente>>() {
            @Override
            public void onChanged(List<Utente> utentilist) {

                listaUtenti=utentilist;
                for (Utente utente : listaUtenti) {
                    LatLng UtenteVicinoLatLng = new LatLng(utente.getLat(), utente.getLon());
                    int iconResource;
                    iconResource = R.drawable.icona_mappa_utente; // Usa il nuovo drawable personalizzato

                    Bitmap drawableBitmap = getBitmap(iconResource);


                    BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromBitmap(drawableBitmap);


                    MarkerOptions markerOptions2 = new MarkerOptions()
                            .position(UtenteVicinoLatLng)
                            .title(utente.getName())
                            .snippet(String.valueOf(utente.getUid()))
                            .icon(markerIcon);
                    googleMap.addMarker(markerOptions2);


                }


            }

        });
        vicinanzeViewModel.getListaOggettiLiveData().observeForever(new Observer<List<Oggetto>>() {
            @Override
            public void onChanged(List<Oggetto> oggettiList) {

                listaOggetti=oggettiList;
                for (Oggetto oggetto : listaOggetti) {
                    LatLng oggettoLatLng = new LatLng(oggetto.getLat(), oggetto.getLon());
                    int iconResource;
                    if ("amulet".equals(oggetto.getType())) {
                        iconResource = R.drawable.icona_mappa_amuleto; // Usa il nuovo drawable personalizzato
                    } else if ("weapon".equals(oggetto.getType())) {
                        iconResource = R.drawable.icona_mappa_arma;
                    } else if ("armor".equals(oggetto.getType())) {
                        iconResource = R.drawable.icona_mappa_armatura;
                    } else if ("candy".equals(oggetto.getType())) {
                        iconResource = R.drawable.icona_mappa_caramella;
                    } else if ("monster".equals(oggetto.getType())) {
                        iconResource = R.drawable.icona_mappa_mostro;
                    } else {
                        iconResource = 0;
                    }
                    Bitmap drawableBitmap = getBitmap(iconResource);


                    BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromBitmap(drawableBitmap);


                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(oggettoLatLng)
                            .title(oggetto.getName())
                            .snippet(String.valueOf(oggetto.getId()))
                            .icon(markerIcon);
                    googleMap.addMarker(markerOptions);

                }
            }
        });
        homeViewModel.prendiVicini(PreferenceSID, defaultValue,utentiDb);
        vicinanzeViewModel.prendiOggetti(PreferenceSID, defaultValue, oggettiDb);




        return root;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
    @Override
    public void onStart() {
        super.onStart();


        // Access the parent view here
        ViewGroup parentViewGroup = (ViewGroup) getView().getParent().getParent();
        overlay = parentViewGroup.findViewById(R.id.overlay);
        spinner = parentViewGroup.findViewById(R.id.spinnerID);
        if (isFirstZoom) {

            if (overlay != null && spinner != null) {
                overlay.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.VISIBLE);
            }
        }

        updateRunnable = new Runnable() {
            @Override
            public void run() {
                // Chiamare qui i metodi per aggiornare gli utenti e gli oggetti
                aggiornaUtentiEOggetti();
                // Pianificare la chiamata successiva dopo 10 secondi
                handler.postDelayed(this, 10000); // 10000 millisecondi = 10 secondi
            }
        };
        handler.post(updateRunnable);

    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("Resume", "onResume");

        aggiornaUtentiEOggetti();
        // Aggiorna i marker quando il fragment viene riaperto

    }
    private void aggiornaUtentiEOggetti() {

        if(googleMap!=null) {

            // Rimuovi i marker che non sono stati aggiornati
            googleMap.clear();


            // Chiamare qui i metodi per ottenere e aggiornare gli utenti e gli oggetti
            homeViewModel.prendiVicini(PreferenceSID, defaultValue, utentiDb);

            vicinanzeViewModel.prendiOggetti(PreferenceSID, defaultValue, oggettiDb);
        }
    }

    private void onPermissionRequestResult(Map<String, Boolean> result) {
        Log.d(TAG, "onPermissionRequestResult");

        Boolean fineLocationGranted = result.getOrDefault(
                Manifest.permission.ACCESS_FINE_LOCATION,
                false
        );

        Boolean coarseLocationGranted = result.getOrDefault(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                false
        );

        Log.d(TAG, "Fine Location Granted: " + fineLocationGranted);
        Log.d(TAG, "Coarse Location Granted: " + coarseLocationGranted);

        if (fineLocationGranted != null && fineLocationGranted) {
            // Precise location access granted
            Log.d(TAG, "onPermissionRequestResult: fineLocationGranted");
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                googleMap.setMyLocationEnabled(true);
                // Ottieni l'oggetto UiSettings per personalizzare i controlli dell'interfaccia utente
                UiSettings uiSettings = googleMap.getUiSettings();

                // Abilita il pulsante "Torna alla posizione dell'utente"
                uiSettings.setMyLocationButtonEnabled(true);
            }
            requestPosition();
        } else if (coarseLocationGranted != null && coarseLocationGranted) {
            // Only approximate location access granted.
            Log.d(TAG, "onPermissionRequestResult: coarseLocationGranted");
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                googleMap.setMyLocationEnabled(true);
                // Ottieni l'oggetto UiSettings per personalizzare i controlli dell'interfaccia utente
                UiSettings uiSettings = googleMap.getUiSettings();

                // Abilita il pulsante "Torna alla posizione dell'utente"
                uiSettings.setMyLocationButtonEnabled(true);
            }
            requestPosition();
        } else {
            // No location access granted.
            Log.d(TAG, "Permessi necessari per accedere alla posizione");
            showLocationPermissionDeniedDialog();

        }
    }
    private void showLocationPermissionDeniedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Permessi di posizione negati")
                .setMessage("Per utilizzare quest'app è necessario concedere i permessi di posizione.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requireActivity().finish();
                    }
                })
                .setCancelable(false) // L'utente non può annullare il dialog
                .show();
    }

    //richiesta di posizione
    public void getCurrentPosition() {
        CurrentLocationRequest clr = new
                CurrentLocationRequest.Builder().setPriority(Priority.PRIORITY_HIGH_ACCURACY).build();
        //vedo se ho i permessi
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = fusedLocationClient.getCurrentLocation(clr, null);
        task.addOnSuccessListener(
                location -> {
                    if (location != null) {
                        Log.d(TAG, "Location: " + location.toString());
                    }
                }
        );
    }

    //gestione richiesta di AGGIORNAMENTO DI POSIZIONE ogni secondo
    private void requestPosition() {

        LocationRequest locationRequest =
                new LocationRequest.Builder(1000)
                        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                        .build();
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
            );
        }

    }
    private void requestPositionfirst() {

        LocationRequest locationRequest =
                new LocationRequest.Builder(1000)
                        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                        .build();
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
            );

        }

    }
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            requireContext(),
                            R.raw.map_style  // R.raw.map_style è l'ID del tuo file JSON
                    )
            );

            if (!success) {
                Log.e(TAG, "Stile della mappa non applicato correttamente");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "File di stile della mappa non trovato. Assicurati che il file JSON sia correttamente posizionato nella cartella res/raw.");
        }
        LatLng posizioneIniziale = new LatLng(0, 0); // Cambia con le tue coordinate iniziali
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            // Ottieni l'oggetto UiSettings per personalizzare i controlli dell'interfaccia utente
            UiSettings uiSettings = googleMap.getUiSettings();

            // Abilita il pulsante "Torna alla posizione dell'utente"
            uiSettings.setMyLocationButtonEnabled(true);

        } else {

            requestPositionfirst();
        }




    }


    // Aggiorna la posizione della mappa in base alla nuova posizione
    private void updateMapLocation(Location nuovaPosizione) {
        if (googleMap != null) {
            LatLng newLatLng = new LatLng(nuovaPosizione.getLatitude(), nuovaPosizione.getLongitude());


            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Oggetto oggettoDesiderato = null;
                    Utente utenteDesiderato = null;
                    // Cerca l'oggetto con l'ID desiderato nella lista
                    for (Oggetto oggetto : listaOggetti) {
                        if (oggetto.getId() == Integer.parseInt(marker.getSnippet())) {
                            oggettoDesiderato = oggetto;
                            break;  // Esci dal ciclo una volta trovato l'oggetto
                        }
                    }
                    for (Utente utente : listaUtenti) {
                        if (utente.getUid() == Integer.parseInt(marker.getSnippet())) {
                            utenteDesiderato = utente;
                            break;  // Esci dal ciclo una volta trovato l'oggetto
                        }
                    }


                    // Apri il nuovo fragment passando le informazioni del marker
                    if(oggettoDesiderato!=null){
                        openVicinanzeOggettoFragment(oggettoDesiderato);

                    }else{
                        openVicinanzeUtenteFragment(utenteDesiderato);

                    }
                    // Restituisci true per indicare che hai gestito il clic sul marcatore
                    return true;
                }
            });

            if (isFirstZoom) {
                // Zoom solo la prima volta
                if (overlay != null && spinner != null) {
                    spinner.setVisibility(View.GONE);
                    overlay.setVisibility(View.GONE);
                }
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLatLng, 16.0f));
                isFirstZoom = false; // Imposta la variabile di stato a false dopo lo zoom iniziale
            }
            homeViewModel.setCoordinatesDB(nuovaPosizione.getLatitude(), nuovaPosizione.getLongitude());


        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(updateRunnable);
        binding = null;
    }

    private void openVicinanzeOggettoFragment(Oggetto o) {

        VicinanzeOggettoMarkerFragment fragment = VicinanzeOggettoMarkerFragment.newInstance(o.getId(),o.getName(), o.getType(),Integer.toString(o.getLevel()), o.getImage(), o.getLat(), o.getLon(),nuovaPosizione);


        // Crea una nuova istanza del Fragment
        //addbackstack per tornare indietro
        // Disabilita il click su layoutClassifica

        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.anim.transation1, R.anim.transation1
        );

        fragmentTransaction.replace(R.id.fragment_home, fragment, null)
                .addToBackStack("home")
                .setReorderingAllowed(true)
                .commit();


    }

    private void openVicinanzeUtenteFragment(Utente u) {

        VicinanzeUtenteMarkerFragment fragment = VicinanzeUtenteMarkerFragment.newInstance(u.getUid(),u.getName(), u.getLife(), u.getExperience(), u.getPicture());

        // Crea una nuova istanza del Fragment
        //addbackstack per tornare indietro
        // Disabilita il click su layoutClassifica

        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.anim.transation1, R.anim.transation1
        );

        fragmentTransaction.replace(R.id.fragment_home, fragment, null)
                .addToBackStack("home")
                .setReorderingAllowed(true)
                .commit();


    }

    private Bitmap getBitmap(int drawableRes) {
        Drawable drawable = getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}