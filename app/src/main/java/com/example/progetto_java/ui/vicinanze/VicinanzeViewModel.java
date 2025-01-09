package com.example.progetto_java.ui.vicinanze;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.Room;

import com.example.progetto_java.api.ApiInterface;
import com.example.progetto_java.api.ResponseObjects;
import com.example.progetto_java.api.ResponseOggettoData;
import com.example.progetto_java.api.ResponseRanking;
import com.example.progetto_java.api.ResponseUtenteData;
import com.example.progetto_java.api.RetrofitProvider;
import com.example.progetto_java.model.Oggetto;
import com.example.progetto_java.model.OggettoModel;
import com.example.progetto_java.model.Utente;
import com.example.progetto_java.model.UtenteModel;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;

public class VicinanzeViewModel extends ViewModel {

    ApiInterface apiInterface= RetrofitProvider.getApinterface();
    String defaultValue = "default";
    private Context context;

    private MutableLiveData<List<Oggetto>> oggettiLiveData = new MutableLiveData<>();

    private int numChiamateCompletate = 0;
    private int numChiamateTotali = 0;

    private UtenteModel utenteDb;

    private double lon,lat;

    public VicinanzeViewModel() {
    }

    public void setContext(Context context) {
        this.context = context;
        utenteDb = Room.databaseBuilder(context, UtenteModel.class, "utenti")
                .build();
    }

    public LiveData<List<Oggetto>> getListaOggettiLiveData() {
        return oggettiLiveData;
    }


    public void prendiOggetti(SharedPreferences preferenceSID, String defaultValue, OggettoModel oggettiDb) {
        ApiInterface apiInterface = RetrofitProvider.getApinterface();
        SharedPreferences preferences = context.getSharedPreferences("myPreferences", MODE_PRIVATE);;
        String uidString = preferences.getString("uid", "default"); // "default" è il valore di default se la chiave non esiste
        int uidUtente = 0;

        if(!uidString.equals("default")){
            uidUtente = Integer.parseInt(uidString);
        }

        //prendo prima le coordinate del mio utente e poi faccio le chiamate
        ListenableFuture<Utente> lf = utenteDb.utenteDao().getUserById(uidUtente);
        //aggiungo un listener che viene eseguito quando la chiamata asincrona è completata
        lf.addListener(() -> {


            try {
                Utente utente = lf.get();

                if (utente != null) {
                    Log.d("TAG", "Utente:"+utente.getName()+" "+utente.getExperience()+" "+utente.getAmulet()+" "+utente.getArmor()+" "+utente.getWeapon());

                    Log.d("TAG", utente.toString());

                    lon = utente.getLon();
                    lat = utente.getLat();

                    Call<List<ResponseObjects>> objectsCall = apiInterface.prendiOggetti(preferenceSID.getString("SID", defaultValue), lat, lon);

                    objectsCall.enqueue(new Callback<List<ResponseObjects>>() {
                        @Override
                        public void onResponse(Call<List<ResponseObjects>> call, retrofit2.Response<List<ResponseObjects>> response) {
                            if (response.isSuccessful()) {
                                List<ResponseObjects> resultList = response.body();
                                List<Oggetto> oggettiList = new ArrayList<>();

                                numChiamateCompletate = 0;
                                numChiamateTotali = resultList.size();

                                for (ResponseObjects result : resultList) {
                                    Log.d("Oggetti", "preso da API - ID: " + result.getid() + ", tipo: " + result.getType() + ", lat: " + result.getLat() + ", lon: " + result.getLon());
                                    // Aggiungi le informazioni di longitudine e latitudine all'Utente

                                    prendiOggettodaDbLocale(preferenceSID, defaultValue, result.getid(), oggettiDb, oggettiList,result.getLat(), result.getLon());
                                }
                            } else {
                                try {
                                    String errorBody = response.errorBody().string();
                                    Log.d("Davide", "Error: " + response.code() + ", Error Body: " + errorBody);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }

                        @Override
                        public void onFailure(Call<List<ResponseObjects>> call, Throwable t) {
                            Log.d("Oggetti", "Error: " + t.getMessage());
                        }
                    });

                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },  ContextCompat.getMainExecutor(context));

    }

    public void prendiOggettodaDbLocale(SharedPreferences preferenceSID, String defaultValue,int idOggetto,OggettoModel db,List<Oggetto> oggettiList,double latOggetto,double lonOggetto){
        //faccio la chiamata asincrona
        ListenableFuture<Oggetto> lf = db.oggettoDao().getObjectById(idOggetto);
        //aggiungo un listener che viene eseguito quando la chiamata asincrona è completata
        lf.addListener(() -> {
            Log.d("TAG", "Chiamata al db locale completata");
            try {
                Oggetto oggetto = lf.get();

                //Log.d("TAG", ""+utente.getProfileVersion()+" -"+profileVersion);

                if (oggetto != null) {
                    oggetto.setLat(latOggetto);
                    oggetto.setLon(lonOggetto);
                    Log.d("TAG", oggetto.toString());
                    // Aggiungi utente alla lista
                    oggettiList.add(oggetto);

                    numChiamateCompletate++;
                    if (numChiamateCompletate == numChiamateTotali) {
                        // Tutte le chiamate sono complete, aggiorna il LiveData

                        oggettiLiveData.setValue(oggettiList);
                    }

                    Log.d("Davide", oggettiList.toString());
                }else{
                    // Se il mostro non è presente nel database locale, lo recupero dall'API
                    prendiOggettoDaApi(idOggetto, preferenceSID,  db,defaultValue,oggettiList,latOggetto,lonOggetto);
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },  ContextCompat.getMainExecutor(context));
    }

    public void prendiOggettoDaApi(int idOggetto,SharedPreferences PreferenceSID,OggettoModel db,String defaultValue,List<Oggetto> oggettiList,double latOggetto,double lonOggetto){
            //chiedo all'API il mostro con l'ID specificato

            Call<ResponseOggettoData> prendiOggettoCall = apiInterface.prendiDatiOggetto(idOggetto,PreferenceSID.getString("SID", defaultValue));
            prendiOggettoCall.enqueue(new Callback<ResponseOggettoData>() {
                @Override
                public void onResponse(Call<ResponseOggettoData> call, retrofit2.Response<ResponseOggettoData> response) {
                    if (!response.isSuccessful()) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.d("Oggetti", "Error: " + response.code() + ", Error Body: " + errorBody);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                    Log.d("Oggetti", "Oggetto preso da API");
                    ResponseOggettoData result = response.body();

                        Oggetto oggettoCreato = new Oggetto(result.getId(),result.getType(),result.getLevel(),result.getImage(),result.getName());


                    ListenableFuture<Void> lf = db.oggettoDao().inserisciOggetto(oggettoCreato);
                    //aggiungo un listener che viene eseguito quando la chiamata asincrona è completata
                    lf.addListener(() -> {
                        Log.d("TAG", "Chiamata al db locale completata,oggetto aggiunto");
                    }, ContextCompat.getMainExecutor(context));
                    oggettoCreato.setLat(latOggetto);
                    oggettoCreato.setLon(lonOggetto);
                    oggettiList.add(oggettoCreato);
                    Log.d("Davide", oggettiList.toString());

                        oggettiList.add(oggettoCreato);
                        Log.d("Davide", oggettiList.toString());

                        numChiamateCompletate++;
                        if (numChiamateCompletate == numChiamateTotali) {
                            // Tutte le chiamate sono complete, aggiorna il LiveData
                            oggettiLiveData.setValue(oggettiList);
                        }

                }
                @Override
                public void onFailure(Call<ResponseOggettoData> call, Throwable t) {
                    Log.d("Davide", "Error: " + t.getMessage());
                }
            });
    }

    public void chiudiDb(){
        utenteDb.close();
    }
}