package com.example.progetto_java.ui.vicinanze;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.progetto_java.R;
import com.example.progetto_java.model.Oggetto;
import com.example.progetto_java.model.Utente;
import com.example.progetto_java.ui.classifica.ClassificaFragment;
import com.example.progetto_java.ui.classifica.ClassificaViewHolder;

import java.util.List;

//l'adapter passa i dati al viewHolder che li mostra
public class VicinanzeAdapter extends RecyclerView.Adapter<VicinanzeViewHolder> {
    private LayoutInflater mInflater;

    //il viewmodel ha un riferimento al model e l'adapter al viewmodel
    private List<Oggetto> oggettiList;
    private VicinanzeFragment vicinanzeFragment;
    private boolean isClickable = true;

    public VicinanzeAdapter(Context context, List<Oggetto> oggettiList, VicinanzeFragment vicinanzeFragment) {
        //l'inflater prende in input l'XML della riga della classifica e lo trasforma in java
        this.mInflater = LayoutInflater.from(context);
        this.oggettiList = oggettiList;
        this.vicinanzeFragment = vicinanzeFragment;
    }

    //chiamato quando c'è bisogno di creare un viewholder
    public VicinanzeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //L'inflater a partire dalla rappresentazione statica del layout XML mi crea l'oggetto dinamico, poi lo popolo con il binding
        //qui creo una rappresentazione dinamica della riga della classifica e lo metto dentro una view
        View view = mInflater.inflate(R.layout.riga_vicinanze, parent, false);
        return new VicinanzeViewHolder(view,vicinanzeFragment);
    }

    //questo metodo viene chiamato ogni volta che devo mostrare una riga della classifica e devo assegnargli un nuovo valore
    //gli passo qual è il viewholder da aggiornare e con cosa va aggiornato (posizione del model)
    @Override
    public void onBindViewHolder(@NonNull VicinanzeViewHolder holder, int index) {
        holder.bind(oggettiList.get(index));

        // Imposta l'OnClickListener solo se isClickable è true
        holder.itemView.setOnClickListener(view -> {
            if (isClickable) {
                vicinanzeFragment.onObjectClick(index);
            }
        });
    }

    @Override
    public int getItemCount() {
        return oggettiList.size();
    }

    public void setClickable(boolean clickable) {
        this.isClickable = clickable;
        notifyDataSetChanged(); // Notifica al RecyclerView che i dati sono cambiati
    }
}
