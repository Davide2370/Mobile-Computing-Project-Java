package com.example.progetto_java.ui.classifica;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.progetto_java.R;
import com.example.progetto_java.model.Utente;

import java.util.List;

//l'adapter passa i dati al viewHolder che li mostra
public class ClassificaAdapter extends RecyclerView.Adapter<ClassificaViewHolder> {
    private LayoutInflater mInflater;

    //il viewmodel ha un riferimento al model e l'adapter al viewmodel
    private List<Utente> utentiList;

    private ClassificaFragment classificaFragment;
    private boolean isClickable = true;

    public ClassificaAdapter(Context context, List<Utente> utentiList, ClassificaFragment classificaFragment) {
        //l'inflater prende in input l'XML della riga della classifica e lo trasforma in java
        this.mInflater = LayoutInflater.from(context);
        this.utentiList = utentiList;
        this.classificaFragment = classificaFragment;
    }

    //chiamato quando c'è bisogno di creare un viewholder
    public ClassificaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //L'inflater a partire dalla rappresentazione statica del layout XML mi crea l'oggetto dinamico, poi lo popolo con il binding
        //qui creo una rappresentazione dinamica della riga della classifica e lo metto dentro una view
        View view = mInflater.inflate(R.layout.riga_classifica, parent, false);
        return new ClassificaViewHolder(view,classificaFragment);
    }

    //questo metodo viene chiamato ogni volta che devo mostrare una riga della classifica e devo assegnargli un nuovo valore
    //gli passo qual è il viewholder da aggiornare e con cosa va aggiornato (posizione del model)
    @Override
    public void onBindViewHolder(@NonNull ClassificaViewHolder holder, int index) {
        Log.d("ClassificaFragment", "Lista Utenti size: " + utentiList.size());

        holder.bind(utentiList.get(index),index);
        // Imposta l'OnClickListener solo se isClickable è true
        holder.itemView.setOnClickListener(view -> {
            if (isClickable) {
                classificaFragment.onProfileClick(index);
            }
        });
    }

    @Override
    public int getItemCount() {
        return utentiList.size();
    }

    public void setClickable(boolean clickable) {
        this.isClickable = clickable;
        notifyDataSetChanged(); // Notifica al RecyclerView che i dati sono cambiati
    }
}