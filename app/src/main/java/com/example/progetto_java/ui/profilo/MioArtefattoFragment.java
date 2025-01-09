package com.example.progetto_java.ui.profilo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.progetto_java.R;
import com.example.progetto_java.model.Utente;
import com.example.progetto_java.ui.home.HomeViewModel;
import com.example.progetto_java.ui.vicinanze.VicinanzeAdapter;

public class MioArtefattoFragment extends Fragment {
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
    private TextView DescrizioneOggettoTextView;

    // TODO: Rename and change types of parameters
    private String Name;
    private String Type;
    private int Level;
    private String Image;


    private VicinanzeAdapter adapter;

    private HomeViewModel homeViewModel;

    private int idObject;
    private int objectDistance;
    private boolean isNear;
    private int distanzaMax;

    public MioArtefattoFragment() {
        // Required empty public constructor
    }
    public static MioArtefattoFragment newInstance( String Name, String Type, int Level, String Image){
        MioArtefattoFragment fragment = new MioArtefattoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, Name);
        args.putString(ARG_TYPE, Type);
        args.putInt(ARG_LEVEL, Level);
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
            Level = getArguments().getInt(ARG_LEVEL);
            Image= getArguments().getString(ARG_IMAGE);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mio_artefatto, container, false);
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.setContext(requireContext());


                // Inizializza i TextView
                NameTextView = view.findViewById(R.id.Name);
                ImageView = view.findViewById(R.id.oggettoImage);
                TypeTextView = view.findViewById(R.id.Type);
                LevelTextView = view.findViewById(R.id.Level);
                DescrizioneOggettoTextView=view.findViewById(R.id.DescrizioneTextView);


                // Imposta i valori nei TextView
                NameTextView.setText(Name);
                TypeTextView.setText(Type);
                LevelTextView.setText(String.valueOf(Level));
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

                if(Type.equals("amulet")){
                    DescrizioneOggettoTextView.setText("Ti permette di attivare oggetti fino a "+(100+Level)+"metri");
                }else if(Type.equals("weapon")){
                    DescrizioneOggettoTextView.setText("Ti permette di subire il "+Level+" % di danni in meno in combattimento");
                }else if(Type.equals("armor")){
                    DescrizioneOggettoTextView.setText("Ti permette di arrivare fino a "+(100+Level)+" punti vita");
                }
                View indietroProfilo = view.findViewById(R.id.indietroProfilo);



    // Imposta il click listener per il pulsante "indietro"
        indietroProfilo.setOnClickListener(v -> {
        FragmentManager fm = requireActivity().getSupportFragmentManager();
        fm.popBackStack("profilo", FragmentManager.POP_BACK_STACK_INCLUSIVE);

        });
        return view;
    }

}
