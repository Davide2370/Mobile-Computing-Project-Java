package com.example.progetto_java.ui.vicinanze;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.progetto_java.R;
import com.example.progetto_java.model.Oggetto;
import com.example.progetto_java.model.Utente;
import com.example.progetto_java.ui.vicinanze.VicinanzeFragment;

public class VicinanzeViewHolder extends RecyclerView.ViewHolder {
    private TextView objectNameTextView;
    private TextView objectLevelTextView;

    private ImageView objectNearTextView;
    private TextView objectTypeTextView;


    private ImageView objectImageView;

    private VicinanzeFragment vicinanzeFragment;
    private boolean isClickable = true;

    public VicinanzeViewHolder(@NonNull View itemView, VicinanzeFragment vicinanzeFragment) {
        super(itemView);
        this.vicinanzeFragment = vicinanzeFragment;
        //qui prendo i riferimenti ai vari elementi del layout
        //prendo la textview dal layout
        objectImageView= itemView.findViewById(R.id.objectImage);
        objectNameTextView = itemView.findViewById(R.id.objectName);
        objectLevelTextView = itemView.findViewById(R.id.objectLevel);
        objectNearTextView = itemView.findViewById(R.id.objectIsNear);
        //setto un evento legato al tocco nella cella (lo gestisco dall'activity e lo richiamo semplicemente qui
        // poichè l'activity ha un accesso ad info che il viewHolder non ha)
        if (isClickable) {
            itemView.setOnClickListener(v -> {
                vicinanzeFragment.onObjectClick(getAdapterPosition());
            });
        }
    }
    //passo un oggetto di quelli che voglio mostrare nella lista
    public void bind(Oggetto o) {
        objectImageView.setImageBitmap(null);

        //qui popolo i riferimenti con i dati dell'oggetto
        if (o.getImage() != null) {

            byte[] imageBytes = Base64.decode(o.getImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            objectImageView.setImageBitmap(bitmap);
        }else{
            if (o.getType().equals("amulet")) {
                objectImageView.setImageResource(R.drawable.icona_amuleto);
            }else if(o.getType().equals("monster")){
                objectImageView.setImageResource(R.drawable.icona_mostro);
            }else if(o.getType().equals("candy")){
                objectImageView.setImageResource(R.drawable.icona_caramella);
            }else if(o.getType().equals("armor")){
                objectImageView.setImageResource(R.drawable.icona_armatura);
            }else {
                objectImageView.setImageResource(R.drawable.icona_spada);
            }
        }
        //setto il testo della textview
        objectNameTextView.setText(o.getName());
        objectLevelTextView.setText("Lv: "+String.valueOf(o.getLevel()));
        if(o.getIsNear()== true){

            objectNearTextView.setImageResource(R.drawable.checkmark_circle);
        }else{
            objectNearTextView.setImageResource(R.drawable.close_circle);
        }


    }
    public void setClickable(boolean clickable) {
        this.isClickable = clickable;
    }

}
