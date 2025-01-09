package com.example.progetto_java;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.progetto_java.model.UtenteModel;
import com.example.progetto_java.ui.home.HomeViewModel;
import com.example.progetto_java.ui.profilo.ProfiloFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import com.example.progetto_java.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private static final long CHECK_CONNECTION_INTERVAL = 5000; // Controlla la connessione ogni 5 secondi
    private Handler handler;
    private Runnable checkConnectionRunnable;
    private AlertDialog noConnectionDialog;
    private HomeViewModel homeViewModel;

    private boolean presenzadialog=false;
    SharedPreferences PreferenceSID;
    String defaultValue = "default";
    private UtenteModel utentiDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceSID = this.getSharedPreferences("myPreferences", MODE_PRIVATE);
        utentiDb = Room.databaseBuilder(this, UtenteModel.class, "utenti")
                .build();
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        homeViewModel.setContext(this);
        homeViewModel.salvaSID(PreferenceSID, defaultValue);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        View root = binding.getRoot();
        ProgressBar spinner= root.findViewById(R.id.spinnerID);
        spinner.bringToFront();
        View overlay= root.findViewById(R.id.overlay);
        overlay.bringToFront();
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_vicinanze, R.id.navigation_classifica, R.id.navigation_profilo)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


        handler = new Handler();
        checkConnectionRunnable = new Runnable() {
            @Override
            public void run() {
                if (isNetworkAvailable()) {
                    dismissNoConnectionDialog();

                    // La connessione di rete è disponibile
                    // Puoi eseguire le operazioni che richiedono la connessione di rete qui
                } else if(!isNetworkAvailable() && !presenzadialog){
                    // Nessuna connessione di rete disponibile

                    showNoConnectionDialog();
                    // Ripeti il controllo dopo un intervallo di tempo

                }
                handler.postDelayed(this, CHECK_CONNECTION_INTERVAL);

            }
        };

        handler.post(checkConnectionRunnable);


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

        return false;
    }

    private void dismissNoConnectionDialog() {
        if (noConnectionDialog != null && noConnectionDialog.isShowing()) {
            noConnectionDialog.dismiss();
            noConnectionDialog = null;
        }
    }

    private void showNoConnectionDialog() {
        if (noConnectionDialog == null || !noConnectionDialog.isShowing()) {
            presenzadialog=true;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Errore di connessione")
                    .setMessage("La connessione di rete non è disponibile. Controlla la tua connessione e riprova.")
                    .setPositiveButton("Riprova", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (isNetworkAvailable()) {
                                dismissNoConnectionDialog();

                                dialog.dismiss();
                                // La connessione di rete è disponibile
                                // Puoi eseguire le operazioni che richiedono la connessione di rete qui
                            } else {
                                // Nessuna connessione di rete disponibile
                                showNoConnectionDialog();
                                // Ripeti il controllo dopo un intervallo di tempo

                            }
                            // Chiudi l'app o esegui altre azioni in base alle tue esigenze
                        }
                    })
                    .setCancelable(false) // L'utente non può annullare il dialog
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            // Dialog nascosto, puoi eseguire operazioni aggiuntive qui se necessario
                        }
                    })
                    .show();
        }
    }


}