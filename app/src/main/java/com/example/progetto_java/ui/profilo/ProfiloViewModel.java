package com.example.progetto_java.ui.profilo;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.Room;

import com.example.progetto_java.api.ApiInterface;
import com.example.progetto_java.api.ResponseAttivaOggetto;
import com.example.progetto_java.api.RetrofitProvider;
import com.example.progetto_java.model.Oggetto;
import com.example.progetto_java.model.OggettoModel;
import com.example.progetto_java.model.Utente;
import com.example.progetto_java.model.UtenteModel;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;

public class ProfiloViewModel extends ViewModel {

    private Context context;

    private OggettoModel oggettiDb;
    private UtenteModel utenteDb;
    public ProfiloViewModel() {

    }

    public void setContext(Context context) {
        this.context = context;
        oggettiDb = Room.databaseBuilder(context, OggettoModel.class, "oggetti")
                .build();
        utenteDb = Room.databaseBuilder(context, UtenteModel.class, "utenti")
                .build();
    }

    public LiveData<Oggetto> getObjectDetails(int idObject){
        MutableLiveData<Oggetto> OggettoLiveData = new MutableLiveData<>();

        ListenableFuture<Oggetto> ogg = oggettiDb.oggettoDao().getObjectById(idObject);

        ogg.addListener(() -> {
            Log.d("TAG", "Oggetto Utente preso dal DB");
            try {
                Oggetto oggetto = ogg.get();
                if (oggetto != null) {

                    OggettoLiveData.setValue(oggetto);
                }else{
                    Log.d("Prova", "oggetto utente nullo");
                    OggettoLiveData.setValue(null);
                }

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(context));

        return OggettoLiveData;
    }

    public void settaImmagine(String base64Image, View overlay, ProgressBar spinner){
        ApiInterface apiInterface= RetrofitProvider.getApinterface();
        SharedPreferences preferences = context.getSharedPreferences("myPreferences", MODE_PRIVATE);
        String uidString = preferences.getString("uid", "default");
        int uidUtente = 0;

        if (!uidString.equals("default")) {
            uidUtente = Integer.parseInt(uidString);
        }
        ResponseAggiornaUtente body = new ResponseAggiornaUtente();
        body.picture = base64Image;
        body.sid = preferences.getString("SID", "default");

        Call<Void> aggiornaUtenteCall = apiInterface.aggiornaUtente(uidUtente,body);
        aggiornaUtenteCall.enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (!response.isSuccessful()) {
                    Log.d("Davide", "Error: " + response.code());
                    return;
                }
                overlay.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                SharedPreferences preferences = context.getSharedPreferences("myPreferences", MODE_PRIVATE);
                String uidString = preferences.getString("uid", "default");
                int uidUtente = 0;

                if (!uidString.equals("default")) {
                    uidUtente = Integer.parseInt(uidString);
                }
                ListenableFuture<Void> lf = utenteDb.utenteDao().AggiornaImmagineUtente(uidUtente, base64Image);

                //aggiungo un listener che viene eseguito quando la chiamata asincrona è completata
                lf.addListener(() -> {
                    Log.d("Davide", "Immagine inserita: ");


                 }, ContextCompat.getMainExecutor(context));

            };

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("Davide", "Error: " + t.getMessage());
            }
        });
    }

    public void settaNome(String nome,View overlay, ProgressBar spinner){
        overlay.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.VISIBLE);
        ApiInterface apiInterface= RetrofitProvider.getApinterface();
        SharedPreferences preferences = context.getSharedPreferences("myPreferences", MODE_PRIVATE);
        String uidString = preferences.getString("uid", "default");
        int uidUtente = 0;

        if (!uidString.equals("default")) {
            uidUtente = Integer.parseInt(uidString);
        }
        ResponseAggiornaUtente body = new ResponseAggiornaUtente();
        body.name = nome;
        body.sid = preferences.getString("SID", "default");

        Call<Void> aggiornaUtenteCall = apiInterface.aggiornaUtente(uidUtente,body);
        aggiornaUtenteCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (!response.isSuccessful()) {
                    Log.d("Davide", "Error: " + response.code());
                    return;
                }
                overlay.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                SharedPreferences preferences = context.getSharedPreferences("myPreferences", MODE_PRIVATE);
                String uidString = preferences.getString("uid", "default");
                int uidUtente = 0;

                if (!uidString.equals("default")) {
                    uidUtente = Integer.parseInt(uidString);
                }
                ListenableFuture<Void> lf = utenteDb.utenteDao().AggiornaNomeUtente(uidUtente, nome);

                //aggiungo un listener che viene eseguito quando la chiamata asincrona è completata
                lf.addListener(() -> {
                    Log.d("Davide", "Nome inserito nel db: ");


                }, ContextCompat.getMainExecutor(context));

            };

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("Davide", "Error: " + t.getMessage());
            }
        });
    }

    public void settaSharePosition(boolean SharePosition,View overlay, ProgressBar spinner){
        overlay.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.VISIBLE);
        ApiInterface apiInterface= RetrofitProvider.getApinterface();
        SharedPreferences preferences = context.getSharedPreferences("myPreferences", MODE_PRIVATE);
        String uidString = preferences.getString("uid", "default");
        int uidUtente = 0;

        if (!uidString.equals("default")) {
            uidUtente = Integer.parseInt(uidString);
        }
        ResponseAggiornaUtente body = new ResponseAggiornaUtente();
        body.positionshare = SharePosition;
        body.sid = preferences.getString("SID", "default");

        Call<Void> aggiornaUtenteCall = apiInterface.aggiornaUtente(uidUtente,body);
        aggiornaUtenteCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (!response.isSuccessful()) {
                    Log.d("Davide", "Error: " + response.code());
                    return;
                }

                SharedPreferences preferences = context.getSharedPreferences("myPreferences", MODE_PRIVATE);
                String uidString = preferences.getString("uid", "default");
                int uidUtente = 0;

                if (!uidString.equals("default")) {
                    uidUtente = Integer.parseInt(uidString);
                }
                ListenableFuture<Void> lf = utenteDb.utenteDao().AggiornaCondivisionePosizioneUtente(uidUtente, SharePosition);

                //aggiungo un listener che viene eseguito quando la chiamata asincrona è completata
                lf.addListener(() -> {
                    Log.d("Davide", "Condivisione inserita nel db: ");
                    overlay.setVisibility(View.GONE);
                    spinner.setVisibility(View.GONE);

                }, ContextCompat.getMainExecutor(context));

            };

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("Davide", "Error: " + t.getMessage());
            }
        });
    }
    public class ResponseAggiornaUtente
    {
        public String sid;
        public String name;
        public String picture;
        public boolean positionshare;

    }
}