package com.example.progetto_java.ui.classifica;


import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.progetto_java.R;
import com.example.progetto_java.model.Utente;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ClassificaViewHolder extends RecyclerView.ViewHolder {
    private TextView playerNameTextView;
    private TextView experiencePointTextView;
    private TextView posizioneTextView;
    private ImageView playerImageTextView;

    private ClassificaFragment classificaFragment;
    private boolean isClickable = true;

    public ClassificaViewHolder(@NonNull View itemView, ClassificaFragment classificaFragment) {
        super(itemView);
        this.classificaFragment = classificaFragment;
        //qui prendo i riferimenti ai vari elementi del layout
        //prendo la textview dal layout
        playerImageTextView= itemView.findViewById(R.id.playerImage);
        playerNameTextView = itemView.findViewById(R.id.playerName);
        experiencePointTextView = itemView.findViewById(R.id.experiencePoints);
        posizioneTextView= itemView.findViewById(R.id.posizioneClassifica);

        //setto un evento legato al tocco nella cella (lo gestisco dall'activity e lo richiamo semplicemente qui
        // poichè l'activity ha un accesso ad info che il viewHolder non ha)
        if (isClickable) {
            itemView.setOnClickListener(v -> {
                classificaFragment.onProfileClick(getAdapterPosition());
            });
        }
    }
    //passo un oggetto di quelli che voglio mostrare nella lista
    public void bind(Utente u, int position) {
        playerImageTextView.setImageBitmap(null);

        // Qui popolo i riferimenti con i dati dell'oggetto
        if (u.getPicture() != null && isBase64String(u.getPicture()) && !u.getPicture().equals("null")) {
            byte[] imageBytes = Base64.decode(u.getPicture(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            if (isBase64StringOver100KB(u.getPicture())) {
                playerImageTextView.setImageResource(R.drawable.profile_pic);
            } else {
                playerImageTextView.setImageBitmap(bitmap);
            }
        } else {
            playerImageTextView.setImageResource(R.drawable.profile_pic);
        }

        // Imposta il testo della textview
        playerNameTextView.setText(u.getName());
        experiencePointTextView.setText(String.valueOf(u.getExperience() + " XP"));

        // Imposta il numero della posizione
        posizioneTextView.setText(String.valueOf(position + 1)+"°");  // Aggiungi 1 per partire da 1 invece di 0
    }

    // Metodo per verificare se una stringa Base64 è più pesante di 100 KB
    private boolean isBase64StringOver100KB(String base64String) {
        String base64WithoutPadding = base64String.replaceAll("=*$", "");

        int decodedLength = base64WithoutPadding.length() * 6 / 8;

        int fileSizeKB = decodedLength / 1024;

        return fileSizeKB > 100;
    }
    private boolean isBase64String(String input) {
        try {
            // Decodifica la stringa Base64
            byte[] decodedBytes = Base64.decode(input, Base64.DEFAULT);

            // Controlla che la decodifica abbia successo
            return decodedBytes != null;
        } catch (IllegalArgumentException e) {
            // Se viene lanciata un'eccezione, la stringa non è Base64
            return false;
        }
    }
    public void setClickable(boolean clickable) {
        this.isClickable = clickable;
    }

}