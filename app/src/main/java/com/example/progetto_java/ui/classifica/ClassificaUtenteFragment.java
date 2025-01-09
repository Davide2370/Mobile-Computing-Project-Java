package com.example.progetto_java.ui.classifica;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.progetto_java.R;
import com.example.progetto_java.ui.profilo.ProfiloFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ClassificaUtenteFragment extends Fragment implements OnMapReadyCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_NAME = "nome";
    private static final String ARG_HP = "vita";
    private static final String ARG_XP = "esperienza";
    private static final String ARG_IMAGE = "immagine";

    private TextView pNameTextView;
    private TextView pHPTextView;
    private TextView pXPTextView;
    private ImageView pImageView;
    private TextView condividiPosizioneTextView;


    // TODO: Rename and change types of parameters
    private String pName;
    private String pXP;
    private String pHP;
    private String pImage;

    private ClassificaAdapter adapter;

    private boolean positionShare;
    private double lat;
    private double lon;

    private MapView mapView;
    private GoogleMap googleMap;

    public ClassificaUtenteFragment() {
        // Required empty public constructor
    }
    public static ClassificaUtenteFragment newInstance(String pName, String pHP, String pXP,String pImage,ClassificaAdapter classificaFragment,boolean positionShare,double lat,double lon){
        ClassificaUtenteFragment fragment = new ClassificaUtenteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, pName);
        args.putString(ARG_HP, pHP);
        args.putString(ARG_XP, pXP);
        args.putString(ARG_IMAGE, pImage);
        args.putDouble("lat",lat);
        args.putDouble("lon",lon);
        args.putBoolean("positionShare",positionShare);
        fragment.setArguments(args);
        fragment.adapter=classificaFragment;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pName = getArguments().getString(ARG_NAME);
            pHP = getArguments().getString(ARG_HP);
            pXP = getArguments().getString(ARG_XP);
            pImage= getArguments().getString(ARG_IMAGE);
            positionShare=getArguments().getBoolean("positionShare");
            lat = getArguments().getDouble("lat");
            lon= getArguments().getDouble("lon");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classifica_utente, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e(TAG, "SupportMapFragment is null");
        }


        // Inizializza i TextView
        pNameTextView = view.findViewById(R.id.pName);
        pImageView=view.findViewById(R.id.utenteImage);
        pImageView=view.findViewById(R.id.utenteImage);
        pHPTextView = view.findViewById(R.id.pHP);
        pXPTextView = view.findViewById(R.id.pXP);
        condividiPosizioneTextView= view.findViewById(R.id.condivisione_label);
        String condiv="Off";
        if(positionShare){
            condiv="On";
        }
        // Imposta i valori nei TextView
        pNameTextView.setText(pName);
        pHPTextView.setText(pHP);
        pXPTextView.setText(pXP);
        condividiPosizioneTextView.setText(condiv);
        if (pImage != null && !pImage.equals("null")) {
            byte[] imageBytes = Base64.decode(pImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            pImageView.setImageBitmap(bitmap);
        }else{
            pImageView.setImageResource(R.drawable.profile_pic);
        }

        View layoutClassifica = requireActivity().findViewById(R.id.layoutClassifica);

        View indietroButton = view.findViewById(R.id.indietroClassifica);
        // Imposta il click listener per il pulsante "indietro"
        indietroButton.setOnClickListener(v -> {
            FragmentManager fm = requireActivity().getSupportFragmentManager();
            fm.popBackStack("classifica", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            if (adapter != null  ) {
                adapter.setClickable(true);
            }
        });

        return view;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        if (googleMap != null) {
            googleMap.getUiSettings().setAllGesturesEnabled(false);

            // Imposta un marker alle coordinate desiderate
            LatLng coordinateMarker = new LatLng(lat, lon);

            googleMap.addMarker(new MarkerOptions().position(coordinateMarker).title("Marker Title"));

            // Imposta la camera sulla posizione del marker senza animazione
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinateMarker, 12.0f)); // Zoom desiderato
        } else {
            Log.e(TAG, "GoogleMap is null");
        }
    }

}