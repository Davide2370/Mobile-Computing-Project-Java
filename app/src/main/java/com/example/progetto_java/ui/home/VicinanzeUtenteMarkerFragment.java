package com.example.progetto_java.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;

import com.example.progetto_java.R;
import com.example.progetto_java.model.Utente;

public class VicinanzeUtenteMarkerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_NAME = "nome";
    private static final String ARG_LIFE = "vita";
    private static final String ARG_EXPERIENCE = "esperienza";

    private static final String ARG_IMAGE = "immagine";

    private TextView NameTextView;
    private TextView LifeTextView;
    private TextView ExperienceTextView;
    private ImageView ImageView;

    // TODO: Rename and change types of parameters
    private String Name;
    private int Life;
    private int Experience;
    private String Image;

    private int uidUtente;

    private HomeViewModel homeViewModel;

    public VicinanzeUtenteMarkerFragment() {
        // Required empty public constructor
    }
    public static VicinanzeUtenteMarkerFragment newInstance(int uid, String Name, int Life, int experience, String Image) {
        VicinanzeUtenteMarkerFragment fragment = new VicinanzeUtenteMarkerFragment();
        Bundle args = new Bundle();
        args.putInt("uid", uid);
        args.putString(ARG_NAME, Name);
        args.putInt(ARG_EXPERIENCE, experience);
        args.putInt(ARG_LIFE, Life);
        args.putString(ARG_IMAGE, Image);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Name = getArguments().getString(ARG_NAME);
            Life = getArguments().getInt(ARG_LIFE);
            Experience = getArguments().getInt(ARG_EXPERIENCE);
            Image= getArguments().getString(ARG_IMAGE);
            uidUtente = getArguments().getInt("uid");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_utente_vicinanze_marker, container, false);

            NameTextView = view.findViewById(R.id.pName);
            LifeTextView = view.findViewById(R.id.pHP);
            ExperienceTextView = view.findViewById(R.id.pXP);
            ImageView = view.findViewById(R.id.utenteImage);

            // Imposta i valori nei TextView
            NameTextView.setText(Name);
            LifeTextView.setText(String.valueOf(Life));
            ExperienceTextView.setText(String.valueOf(Experience));

        if (Image != null) {
                byte[] imageBytes = Base64.decode(Image, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                ImageView.setImageBitmap(bitmap);
            }

            View layoutHome = requireActivity().findViewById(R.id.fragment_home);

            View indietroHome = view.findViewById(R.id.indietroHome);


        // Imposta il click listener per il pulsante "indietro"
        indietroHome.setOnClickListener(v -> {
            FragmentManager fm = requireActivity().getSupportFragmentManager();
            fm.popBackStack("home", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        });

        return view;
    }

}
