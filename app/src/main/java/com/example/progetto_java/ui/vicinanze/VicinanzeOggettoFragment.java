package com.example.progetto_java.ui.vicinanze;

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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.progetto_java.R;
import com.example.progetto_java.model.Utente;
import com.example.progetto_java.ui.classifica.ClassificaAdapter;
import com.example.progetto_java.ui.classifica.ClassificaUtenteFragment;
import com.example.progetto_java.ui.home.HomeFragment;
import com.example.progetto_java.ui.home.HomeViewModel;

public class VicinanzeOggettoFragment extends Fragment {
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
    private VicinanzeAdapter adapter;
    private HomeViewModel homeViewModel;
    private int idObject;
    private int objectDistance;
    private boolean isNear;
    private int distanzaMax;
    private ProgressBar spinner;
    private View overlay;

    public VicinanzeOggettoFragment() {
        // Required empty public constructor
    }
    public static VicinanzeOggettoFragment newInstance(int id,String Name, String Type, String Level, String Image, VicinanzeAdapter vicinanzeAdapter,int objectDistance,boolean isNear,int distanzaMax){
        VicinanzeOggettoFragment fragment = new VicinanzeOggettoFragment();
        Bundle args = new Bundle();
        args.putInt("objectDistance", objectDistance);
        args.putInt("distanzaMax", distanzaMax);
        args.putBoolean("isNear", isNear);
        args.putInt("id", id);
        args.putString(ARG_NAME, Name);
        args.putString(ARG_TYPE, Type);
        args.putString(ARG_LEVEL, Level);
        args.putString(ARG_IMAGE, Image);
        fragment.setArguments(args);
        fragment.adapter=vicinanzeAdapter;
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
            objectDistance= getArguments().getInt("objectDistance");
            isNear= getArguments().getBoolean("isNear");
            distanzaMax= getArguments().getInt("distanzaMax");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_oggetto_vicinanza, container, false);
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.setContext(requireContext());
        ViewGroup parentViewGroup = (ViewGroup) container.getParent().getParent();
        spinner = parentViewGroup.findViewById(R.id.spinnerID);
        overlay=parentViewGroup.findViewById(R.id.overlay);

                // Inizializza i TextView
                NameTextView = view.findViewById(R.id.Name);
                ImageView = view.findViewById(R.id.oggettoImage);
                TypeTextView = view.findViewById(R.id.Type);
                LevelTextView = view.findViewById(R.id.Level);


                // Imposta i valori nei TextView
                NameTextView.setText(Name);

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
                LevelTextView.setText(Level);
                if (Image != null) {
                    byte[] imageBytes = Base64.decode(Image, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    ImageView.setImageBitmap(bitmap);
                }else{
                    if (Type.equals("amulet")) {
                        ImageView.setImageResource(R.drawable.icona_amuleto);

                    }else if(Type.equals("monster")){
                        ImageView.setImageResource(R.drawable.icona_mostro);
                    }else if(Type.equals("candy")){
                        ImageView.setImageResource(R.drawable.icona_caramella);
                    }else if(Type.equals("armor")){
                        ImageView.setImageResource(R.drawable.icona_armatura);
                    }else {
                        ImageView.setImageResource(R.drawable.icona_spada);
                    }
                }

                View layoutVicinanze = requireActivity().findViewById(R.id.fragment_vicinanze);

                View attivaButton = view.findViewById(R.id.attivaButton);
                View indietroVicinanze = view.findViewById(R.id.indietroVicinanze);
                View indietroVicinanzeCentro = view.findViewById(R.id.indietroVicinanzeCentro);


                if (objectDistance < distanzaMax) {
                    // La distanza è maggiore di 100 metri, quindi mostra il bottone
                    attivaButton.setVisibility(View.VISIBLE);
                    indietroVicinanzeCentro.setVisibility(View.GONE);

                } else {
                    // La distanza è minore o uguale a 100 metri, quindi nascondi il bottone
                    attivaButton.setVisibility(View.GONE);
                    indietroVicinanzeCentro.setVisibility(View.VISIBLE);
                    indietroVicinanze.setVisibility(View.GONE);
                }

                attivaButton.setOnClickListener(v -> {
                    if (Type.equals("amulet") || Type.equals("weapon") || Type.equals("armor") || Type.equals("candy")) {
                        showActivationDialog(Name);
                    } else if (Type.equals("monster")) {
                        showCombattimentoDialog(Name);
                    }


                });

       
    View indietroButton = view.findViewById(R.id.indietroVicinanze);
    View indietroButton2 = view.findViewById(R.id.indietroVicinanzeCentro);

    // Imposta il click listener per il pulsante "indietro"
        indietroButton.setOnClickListener(v -> {
        FragmentManager fm = requireActivity().getSupportFragmentManager();
        fm.popBackStack("vicinities", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            if (adapter != null  ) {
                adapter.setClickable(true);
            }

    });
        indietroButton2.setOnClickListener(v -> {
        FragmentManager fm = requireActivity().getSupportFragmentManager();
        fm.popBackStack("vicinities", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            if (adapter != null  ) {
                adapter.setClickable(true);
            }
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
                                VicinanzeFragment fragment = new VicinanzeFragment();
                                if (adapter != null  ) {
                                    adapter.setClickable(true);
                                }
                                /*
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.fragment_oggetto_vicinanza,fragment );
                                fragmentTransaction.commit();*/
                                FragmentManager fm = requireActivity().getSupportFragmentManager();
                                fm.popBackStack("vicinities", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            }
                        });
                AlertDialog dialog = builder.create();

                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        // Azioni da eseguire quando l'utente preme "OK"
                        dialog.dismiss(); // Chiudi il dialog
                        if (adapter != null  ) {
                            adapter.setClickable(true);
                        }

                        FragmentManager fm = requireActivity().getSupportFragmentManager();
                        fm.popBackStack("vicinities", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                });
                // Crea e mostra il dialog
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
                spinner.setVisibility(View.GONE);
                overlay.setVisibility(View.GONE);
                // Fai qualcosa con l'utente appena ricevuto
                if (utente != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("Attivazione")
                            .setMessage("Ora hai " + utente.getLife() + " punti vita")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // Azioni da eseguire quando l'utente preme "OK"
                                    dialog.dismiss();
                                    FragmentManager fm = requireActivity().getSupportFragmentManager();
                                    fm.popBackStack("vicinities", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                    if (adapter != null  ) {
                                        adapter.setClickable(true);
                                    }
                                }
                            });

                    AlertDialog dialog = builder.create();

                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                                // Azioni da eseguire quando l'utente preme "OK"
                                dialog.dismiss(); // Chiudi il dialog
                                FragmentManager fm = requireActivity().getSupportFragmentManager();
                                fm.popBackStack("vicinities", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                if (adapter != null  ) {
                                    adapter.setClickable(true);
                                }
                        }
                    });

                    // Crea e mostra il dialog
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
                        if(TypeTextView.getText().equals("amuleto")){
                            // Azioni da eseguire quando l'utente preme "OK"
                            dialog.dismiss(); // Chiudi il dialog
                            VicinanzeFragment fragment = new VicinanzeFragment();
                            FragmentManager fm = requireActivity().getSupportFragmentManager();
                            fm.popBackStack("vicinities", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            if (adapter != null ) {
                                adapter.setClickable(true);
                            }
                            /*FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                            fragmentManager1.popBackStack("vicinities", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_oggetto_vicinanza,fragment );
                            fragmentTransaction.commit();*/

                        }else{
                            // Azioni da eseguire quando l'utente preme "OK"
                            dialog.dismiss(); // Chiudi il dialog
                            FragmentManager fm = requireActivity().getSupportFragmentManager();
                            fm.popBackStack("vicinities", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            if (adapter != null  ) {
                                adapter.setClickable(true);
                            }
                        }

                    }
                });
        AlertDialog dialog = builder.create();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(TypeTextView.getText().equals("amuleto")){
                    // Azioni da eseguire quando l'utente preme "OK"
                    dialog.dismiss(); // Chiudi il dialog

                    if (adapter != null  ) {
                        adapter.setClickable(true);
                    }

                    FragmentManager fm = requireActivity().getSupportFragmentManager();
                    fm.popBackStack("vicinities", FragmentManager.POP_BACK_STACK_INCLUSIVE);

                }else{
                    // Azioni da eseguire quando l'utente preme "OK"
                    dialog.dismiss(); // Chiudi il dialog
                    FragmentManager fm = requireActivity().getSupportFragmentManager();
                    fm.popBackStack("vicinities", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    if (adapter != null  ) {
                        adapter.setClickable(true);
                    }
                }
            }
        });
        // Crea e mostra il dialog

        dialog.show();
    }
}
