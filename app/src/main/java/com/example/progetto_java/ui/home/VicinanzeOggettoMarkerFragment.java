package com.example.progetto_java.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.Guideline;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.progetto_java.R;
import com.example.progetto_java.databinding.FragmentProfiloBinding;
import com.example.progetto_java.model.Oggetto;
import com.example.progetto_java.model.Utente;
import com.example.progetto_java.ui.vicinanze.VicinanzeAdapter;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class VicinanzeOggettoMarkerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_NAME = "nome";
    private static final String ARG_TYPE = "tipo";
    private static final String ARG_LEVEL = "livello";

    private static final String ARG_IMAGE = "immagine";

    private TextView NameTextView;
    private TextView TypeTextView;
    private TextView LevelTextView;
    private ImageView ImageView;

    // TODO: Rename and change types of parameters
    private String Name;
    private String Type;
    private String Level;
    private String Image;

    private int idObject;
    private double objectLat;
    private double objectLon;
    private Location userLatLng;

    private HomeViewModel homeViewModel;
    private ProgressBar spinner;
    private View overlay;
    public VicinanzeOggettoMarkerFragment() {
        // Required empty public constructor
    }
    public static VicinanzeOggettoMarkerFragment newInstance(int id,String Name, String Type, String Level, String Image, double objectLat, double objectLon, Location userLatLng) {
        VicinanzeOggettoMarkerFragment fragment = new VicinanzeOggettoMarkerFragment();
        Bundle args = new Bundle();
        args.putDouble("objectLat", objectLat);
        args.putDouble("objectLon", objectLon);
        args.putParcelable("userLatLon", userLatLng);
        args.putInt("id", id);
        args.putString(ARG_NAME, Name);
        args.putString(ARG_TYPE, Type);
        args.putString(ARG_LEVEL, Level);
        args.putString(ARG_IMAGE, Image);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Name = getArguments().getString(ARG_NAME);
            Type = getArguments().getString(ARG_TYPE);
            Level = getArguments().getString(ARG_LEVEL);
            Image= getArguments().getString(ARG_IMAGE);
            idObject = getArguments().getInt("id");
            objectLat = getArguments().getDouble("objectLat");
            objectLon = getArguments().getDouble("objectLon");
            userLatLng = getArguments().getParcelable("userLatLon");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_oggetto_vicinanze_marker, container, false);


         homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.setContext(requireContext());

        homeViewModel.getUserAmuletLevel().observe(getViewLifecycleOwner(), amuletLevel -> {
            // Questo blocco di codice viene eseguito ogni volta che il livello dell'amuleto cambia
            int distanzaMax = 100;
            if (amuletLevel != 1) {
                // Usa il valore di amuletLevel come desideri
                Log.d("TAG", "Livello amuleto: " + amuletLevel);
                distanzaMax = 100 + amuletLevel;
            }
            ViewGroup parentViewGroup = (ViewGroup) container.getParent().getParent();
            spinner = parentViewGroup.findViewById(R.id.spinnerID);
            overlay=parentViewGroup.findViewById(R.id.overlay);
            Location locationA = new Location("Point A");
            locationA.setLatitude(userLatLng.getLatitude());
            locationA.setLongitude(userLatLng.getLongitude());

            Location locationB = new Location("Point B");
            locationB.setLatitude(objectLat);
            locationB.setLongitude(objectLon);

            // Inizializza i TextView
            NameTextView = view.findViewById(R.id.Name);
            ImageView=view.findViewById(R.id.oggettoImage);
            TypeTextView = view.findViewById(R.id.Type);

            if (Type.equals("amulet")) {
                TypeTextView.setText("amuleto");
            }else if(Type.equals("monster")){
                TypeTextView.setText("mostro");

            }else if(Type.equals("candy")){
                TypeTextView.setText("caramella");
            }else if(Type.equals("armor")){
                TypeTextView.setText("armatura");
            }else {
                TypeTextView.setText("arma");
            }
            LevelTextView = view.findViewById(R.id.Level);
            Log.d("TAG", "Livello amuleto: " + idObject);
            // Imposta i valori nei TextView
            NameTextView.setText(Name);
            LevelTextView.setText(Level);
            if (Image != null) {
                byte[] imageBytes = Base64.decode(Image, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                ImageView.setImageBitmap(bitmap);
            }

            View layoutHome = requireActivity().findViewById(R.id.fragment_home);


            View attivaButton = view.findViewById(R.id.attivaButton);
            View indietroHome = view.findViewById(R.id.indietroHome);
            View indietroHomeCentro = view.findViewById(R.id.indietroHomeCentro);


            float distance = locationA.distanceTo(locationB);
            Log.d("idobj", "id: "+idObject);
            if (distance < distanzaMax) {
                // La distanza è maggiore di 100 metri, quindi mostra il bottone
                attivaButton.setVisibility(View.VISIBLE);
                indietroHomeCentro.setVisibility(View.GONE);


            } else {
                // La distanza è minore o uguale a 100 metri, quindi nascondi il bottone
                attivaButton.setVisibility(View.GONE);
                indietroHomeCentro.setVisibility(View.VISIBLE);
                indietroHome.setVisibility(View.GONE);
            }

            attivaButton.setOnClickListener(v -> {
                if(Type.equals("amulet") || Type.equals("weapon") || Type.equals("armor") || Type.equals("candy")){
                    showActivationDialog(Name);
                }else if(Type.equals("monster")){
                    showCombattimentoDialog(Name);
                }


            });
        });

        View indietroButton = view.findViewById(R.id.indietroHome);
        View indietroButton2 = view.findViewById(R.id.indietroHomeCentro);

        // Imposta il click listener per il pulsante "indietro"
        indietroButton.setOnClickListener(v -> {
            FragmentManager fm = requireActivity().getSupportFragmentManager();
            fm.popBackStack("home", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        });
        indietroButton2.setOnClickListener(v -> {
            FragmentManager fm = requireActivity().getSupportFragmentManager();
            fm.popBackStack("home", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        });
        return view;
    }


    private void showActivationDialog(String nomeOggetto){
        String messaggio;
        if(Type.equals("amulet")){
            int distMax= 100+Integer.parseInt(Level);
            messaggio="L'oggetto ti permetterà di attivare oggetti fino a "+distMax+" metri. ";
        }else if(Type.equals("weapon")){
            messaggio="L'oggetto ti permetterà di ricevere il "+Level+"% di danni in meno in combattimento. ";
        }else if(Type.equals("armor")) {
            int LifeMax= 100+Integer.parseInt(Level);
            messaggio = "L'oggetto ti permetterà di poter arrivare fino " + LifeMax + " punti vita. ";
        }else{
            int LifeMax= 2*Integer.parseInt(Level);
            messaggio="L'oggetto ti permetterà di ricevere tra i "+Level+" e i "+LifeMax+" di punti vita";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Attivazione")
                .setMessage(messaggio)
                .setPositiveButton("Attiva", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Azioni da eseguire quando l'utente preme "OK"
                        spinner.setVisibility(View.VISIBLE);
                        overlay.setVisibility(View.VISIBLE);

                        homeViewModel.attivaOggetto(idObject);
                        homeViewModel.getLifeLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                            @Override
                            public void onChanged(Integer life) {
                                spinner.setVisibility(View.GONE);
                                overlay.setVisibility(View.GONE);

                                // Do something with the updated Life value
                                dialog.dismiss();
                                if(Type.equals("candy")) {
                                    showObjectActivatedDialogCandy();
                                }else{
                                    showObjectActivatedDialog();
                                }
                            }
                        });


                    }
                })
                .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Azioni da eseguire quando l'utente preme "Annulla"
                        dialog.dismiss(); // Chiudi il dialog

                    }
                });

        // Crea e mostra il dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showCombattimentoDialog(String nomeOggetto){
        spinner.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.VISIBLE);


        homeViewModel.getUserWeaponLevel().observe(getViewLifecycleOwner(), weaponLevel -> {
            spinner.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);
            int dannoMax= Integer.parseInt(Level)*2;
            int dannoMin= Integer.parseInt(Level);
            int minVitaDaSottrarre,maxVitaDaSottrarre;
            if(weaponLevel!=1){
                minVitaDaSottrarre = dannoMin - (dannoMin * weaponLevel / 100);
                maxVitaDaSottrarre = dannoMax - (dannoMax * weaponLevel / 100);
            }else{
                minVitaDaSottrarre = dannoMin;
                maxVitaDaSottrarre = dannoMax;
            }

            String message="Perderai tra "+minVitaDaSottrarre+" e "+maxVitaDaSottrarre+" punti vita.";

                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("Attivazione")
                            .setMessage(message)
                            .setPositiveButton("Combatti", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    spinner.setVisibility(View.VISIBLE);
                                    overlay.setVisibility(View.VISIBLE);


                                    // Azioni da eseguire quando l'utente preme "OK"
                                    homeViewModel.attivaOggetto(idObject);
                                    homeViewModel.getLifeLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                                        @Override
                                        public void onChanged(Integer life) {
                                            spinner.setVisibility(View.GONE);
                                            overlay.setVisibility(View.GONE);
                                            // Do something with the updated Life value
                                            dialog.dismiss();
                                            showObjectActivatedDialogCombattimento();
                                        }
                                    });


                                }
                            })
                            .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // Azioni da eseguire quando l'utente preme "Annulla"
                                    dialog.dismiss(); // Chiudi il dialog

                                }
                            });

                    // Crea e mostra il dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
        });
    }

    private void showObjectActivatedDialogCombattimento() {
        spinner.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.VISIBLE);

        homeViewModel.ritornaUtente().observe(getViewLifecycleOwner(), utente -> {
            spinner.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);
            String message;
            if (utente != null) {
                if(homeViewModel.getDied()== true){
                    message="Sei morto, hai perso i tuoi artefatti e rinascerai con 100 di vita";
                }else{
                    message="Combattimento finito, ti sono rimasti " + utente.getLife() + " punti vita e hai guadagnato "+Level+" punti esperienza";
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Risultato combattimento")
                        .setMessage(message)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {


                                // Azioni da eseguire quando l'utente preme "OK"
                                dialog.dismiss(); // Chiudi il dialog
                                FragmentManager fm = requireActivity().getSupportFragmentManager();
                                fm.popBackStack("home", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            }
                        });

                // Crea e mostra il dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }


    private void showObjectActivatedDialogCandy() {
        spinner.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.VISIBLE);

        homeViewModel.ritornaUtente().observe(getViewLifecycleOwner(), new Observer<Utente>() {
            @Override
            public void onChanged(Utente utente) {
                // Fai qualcosa con l'utente appena ricevuto
                if (utente != null) {
                    spinner.setVisibility(View.GONE);
                    overlay.setVisibility(View.GONE);

                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("Attivazione")
                            .setMessage("Ora hai " + utente.getLife() + " punti vita")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // Azioni da eseguire quando l'utente preme "OK"
                                    dialog.dismiss();
                                    FragmentManager fm = requireActivity().getSupportFragmentManager();
                                    fm.popBackStack("home", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                }
                            });

                    // Crea e mostra il dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }



    private void showObjectActivatedDialog() {


        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Oggetto Attivato")
                .setMessage("L'oggetto è stato attivato con successo!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // Azioni da eseguire quando l'utente preme "OK"
                        dialog.dismiss(); // Chiudi il dialog
                        FragmentManager fm = requireActivity().getSupportFragmentManager();
                        fm.popBackStack("home", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                });

        // Crea e mostra il dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
