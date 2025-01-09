package com.example.progetto_java.ui.vicinanze;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.progetto_java.R;
import com.example.progetto_java.databinding.FragmentVicinanzeBinding;
import com.example.progetto_java.model.Oggetto;
import com.example.progetto_java.model.OggettoModel;
import com.example.progetto_java.model.Utente;
import com.example.progetto_java.model.UtenteModel;
import com.example.progetto_java.ui.classifica.ClassificaAdapter;
import com.example.progetto_java.ui.classifica.ClassificaFragment;
import com.example.progetto_java.ui.classifica.ClassificaUtenteFragment;
import com.example.progetto_java.ui.classifica.ClassificaViewModel;
import com.example.progetto_java.ui.home.HomeViewModel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VicinanzeFragment extends Fragment {

    private FragmentVicinanzeBinding binding;
    private List<Oggetto> listaOggetti;  // Dichiarazione della variabile di istanza

    private VicinanzeAdapter adapter;

    SharedPreferences PreferenceSID;
    String defaultValue = "default";
    private HomeViewModel homeViewModel;
    private double utenteLat,utenteLon;
    private ProgressBar spinner;
    private int distanzaMax;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if(savedInstanceState==null){
            Log.d("prova","sono qui3");
        }


        VicinanzeViewModel vicinanzeViewModel = new ViewModelProvider(this).get(VicinanzeViewModel.class);
        vicinanzeViewModel.setContext(requireContext());
        OggettoModel oggettiDb = Room.databaseBuilder(requireContext(), OggettoModel.class, "oggetti")
                .build();
        binding = FragmentVicinanzeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        spinner = root.findViewById(R.id.spinnerID);
        SharedPreferences PreferenceSID = requireContext().getSharedPreferences("myPreferences", MODE_PRIVATE);
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.setContext(requireContext());

        homeViewModel.ritornaUtente().observe(getViewLifecycleOwner(), utente -> {
            homeViewModel.getUserAmuletLevel().observe(getViewLifecycleOwner(), amuletLevel -> {
                utenteLat=utente.getLat();
            utenteLon=utente.getLon();

            vicinanzeViewModel.prendiOggetti(PreferenceSID, defaultValue, oggettiDb);

            spinner.setVisibility(View.VISIBLE);
            vicinanzeViewModel.getListaOggettiLiveData().observe(getViewLifecycleOwner(), new Observer<List<Oggetto>>() {
            @Override
            public void onChanged(List<Oggetto> oggettiList) {
                vicinanzeViewModel.getListaOggettiLiveData().removeObservers(getViewLifecycleOwner());

                listaOggetti=oggettiList;
                distanzaMax = 100;
                if (amuletLevel != 1) {
                    // Usa il valore di amuletLevel come desideri
                    Log.d("TAG", "Livello amuleto: " + amuletLevel);
                    distanzaMax = 100 + amuletLevel;
                }
                Location userLocation = new Location("User");
                userLocation.setLatitude(utenteLat);
                userLocation.setLongitude(utenteLon);

                for (Oggetto oggetto : listaOggetti) {
                    Location objectLocation = new Location("Object");
                    objectLocation.setLatitude(oggetto.getLat());
                    objectLocation.setLongitude(oggetto.getLon());
                    Log.d("oggetto",oggetto.getName());
                    float distance = userLocation.distanceTo(objectLocation);
                    oggetto.setIsNear(distance < distanzaMax);
                    oggetto.setDistance(Math.round(distance));
                }


                Collections.sort(listaOggetti, new Comparator<Oggetto>() {
                    @Override
                    public int compare(Oggetto oggetto1, Oggetto oggetto2) {
                        // Ordina per distanza crescente
                        return Integer.compare(oggetto1.getDistance(), oggetto2.getDistance());
                    }
                });
                if (binding != null) {
                    spinner.setVisibility(View.GONE);

                    RecyclerView recyclerView = binding.userRecyclerView;
                    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    adapter = new VicinanzeAdapter(requireContext(), listaOggetti, VicinanzeFragment.this);
                    recyclerView.setAdapter(adapter);
                    Log.d("sono qui","sono qui");

                }
            }
            });
            });
        });
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                // Controlla se lo stack dei fragment è vuoto
                if (fragmentManager.getBackStackEntryCount() == 0) {
                    // Lo stack è vuoto, quindi sei tornato indietro
                    // Esegui il tuo codice qui
                    vicinanzeViewModel.getListaOggettiLiveData().removeObservers(getViewLifecycleOwner());

                    restartFragment();

                }
            }
        });
        return root;
    }

    private void restartFragment() {
        // Rimuovi il fragment corrente
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

            // Crea e aggiungi un nuovo fragment solo se non è già presente
            VicinanzeFragment newFragment = new VicinanzeFragment();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_vicinanze, newFragment, "vicinities");
            transaction.addToBackStack(null);
            transaction.commit();

    }

    public void onObjectClick(int posizione) {
        openVicinanzeOggettoFragment(posizione);
        // Disabilita il click su RecyclerView dopo il click
        if (adapter != null) {
            adapter.setClickable(false);
        }
    }

    private void openVicinanzeOggettoFragment(int posizione) {
        Oggetto o= listaOggetti.get(posizione);
        VicinanzeFragment vicinanzeFragment = (VicinanzeFragment) requireActivity().getSupportFragmentManager().findFragmentByTag("vicinities");

        VicinanzeOggettoFragment fragment = VicinanzeOggettoFragment.newInstance(o.getId(),o.getName(), o.getType(),Integer.toString(o.getLevel()), o.getImage(),adapter,o.getDistance(), o.getIsNear(),distanzaMax);
        // Crea una nuova istanza del Fragment
        //addbackstack per tornare indietro
        // Disabilita il click su layoutVicinanze
        Log.d("prova","sono qui");
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_vicinanze, fragment);
        fragmentTransaction.addToBackStack("vicinities");
        fragmentTransaction.commit();
        binding = null;


    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("prova","sono qui2");
        if (adapter != null) {
            adapter.setClickable(true);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        Log.d("vengo distrutto","vengo distrutto");
    }
}