package com.example.progetto_java.ui.classifica;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.progetto_java.R;
import com.example.progetto_java.databinding.FragmentClassificaBinding;
import com.example.progetto_java.model.Utente;
import com.example.progetto_java.model.UtenteModel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ClassificaFragment extends Fragment {

    private FragmentClassificaBinding binding;
    SharedPreferences PreferenceSID;
    String defaultValue = "default";
    private List<Utente> listaUtenti;  // Dichiarazione della variabile di istanza

    private ClassificaAdapter adapter;
    private ClassificaViewModel classificaViewModel;
    private UtenteModel utentiDb;
    private ProgressBar spinner;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentClassificaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        spinner = root.findViewById(R.id.spinnerID);

        classificaViewModel = new ViewModelProvider(this).get(ClassificaViewModel.class);
        classificaViewModel.setContext(requireContext());
        utentiDb = Room.databaseBuilder(requireContext(), UtenteModel.class, "utenti")
                .build();
        PreferenceSID = requireContext().getSharedPreferences("myPreferences", MODE_PRIVATE);

        spinner.setVisibility(View.VISIBLE);

        classificaViewModel.getListaUtentiLiveData().observe(getViewLifecycleOwner(), new Observer<List<Utente>>() {
            @Override
            public void onChanged(List<Utente> utentiList) {

                // Aggiorna la tua UI con la lista degli utenti qui
                Collections.sort(utentiList, new Comparator<Utente>() {
                    @Override
                    public int compare(Utente utente1, Utente utente2) {
                        // Ordina per esperienza decrescente
                        return Integer.compare(utente2.getExperience(), utente1.getExperience());
                    }
                });
                listaUtenti = utentiList;
                if (binding != null) {
                    spinner.setVisibility(View.GONE);

                    RecyclerView recyclerView = binding.userRecyclerView;
                    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    adapter = new ClassificaAdapter(requireContext(), utentiList, ClassificaFragment.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        if (PreferenceSID != null) {
            classificaViewModel.prendiRanking(PreferenceSID, defaultValue, utentiDb);
        } else {
            // Logga un messaggio di errore o gestisci la situazione in modo appropriato
            Log.e("ClassificaFragment", "L'oggetto PreferenceSID è null in onResume");
        }




        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

    }

    public void onProfileClick(int posizione) {
        openClassificaUtenteFragment(posizione);

        // Disabilita il click su RecyclerView dopo il click
        if (adapter != null) {
            adapter.setClickable(false);
        }
    }

    private void openClassificaUtenteFragment(int posizione) {
        Utente u= listaUtenti.get(posizione);
        ClassificaUtenteFragment fragment = ClassificaUtenteFragment.newInstance(u.getName(), Integer.toString(u.getLife()), Integer.toString(u.getExperience()),u.getPicture(),adapter,u.isShareLocation(),u.getLat(),u.getLon());

        // Crea una nuova istanza del Fragment
        //addbackstack per tornare indietro
        // Disabilita il click su layoutClassifica

        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.anim.transation1, R.anim.transation1
        );

        fragmentTransaction.replace(R.id.layoutClassifica, fragment, null)
                .addToBackStack("classifica")
                .setReorderingAllowed(true)
                .commit();


    }


}