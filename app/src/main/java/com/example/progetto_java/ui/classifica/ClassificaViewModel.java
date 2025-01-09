package com.example.progetto_java.ui.classifica;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.progetto_java.api.ApiInterface;
import com.example.progetto_java.api.ResponseRanking;
import com.example.progetto_java.api.ResponseUtenteData;
import com.example.progetto_java.api.RetrofitProvider;
import com.example.progetto_java.model.Utente;
import com.example.progetto_java.model.UtenteModel;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;

public class ClassificaViewModel extends ViewModel {

    ApiInterface apiInterface= RetrofitProvider.getApinterface();
    private final MutableLiveData<String> mText;
    String defaultValue = "default";
    private Context context;

    private MutableLiveData<List<Utente>> utentiLiveData = new MutableLiveData<>();

    private int numChiamateCompletate = 0;
    private int numChiamateTotali = 0;

    private boolean PositionShare;

    public ClassificaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public LiveData<List<Utente>> getListaUtentiLiveData() {
        return utentiLiveData;
    }
    public void prendiRanking( SharedPreferences preferenceSID, String defaultValue, UtenteModel utentiDb) {
        ApiInterface apiInterface = RetrofitProvider.getApinterface();
        Call<List<ResponseRanking>> rankingCall = apiInterface.prendiRanking(preferenceSID.getString("SID", defaultValue));
        rankingCall.enqueue(new Callback<List<ResponseRanking>>() {
            @Override
            public void onResponse(Call<List<ResponseRanking>> call, retrofit2.Response<List<ResponseRanking>> response) {
                if (response.isSuccessful()) {
                    List<ResponseRanking> resultList = response.body();
                    List<Utente> utentiList = new ArrayList<>();

                    numChiamateCompletate = 0;
                    numChiamateTotali = resultList.size();
                    for (ResponseRanking result : resultList) {
                        Log.d("Davide", "Ranking preso da API - UID: " + result.getUid() + ", Life: " + result.getLife() + ", Experience: " + result.getExperience()+ ", ProfileVersion: " + result.getProfileVersion() + ", PositionShare: " + result.isPositionShare());

                        // Aggiungi le informazioni di longitudine e latitudine all'Utente
                        double lon = result.getLon();
                        double lat = result.getLat();
                        int life = result.getLife();
                        int experience = result.getExperience();
                        boolean PositionShare = result.isPositionShare();

                        prendiUtentedaDbLocale(preferenceSID, defaultValue, result.getUid(), utentiDb, lon, lat,life,experience,result.getProfileVersion(),result.isPositionShare(),utentiList);
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
            public void onFailure(Call<List<ResponseRanking>> call, Throwable t) {
                Log.d("Davide", "Error: " + t.getMessage());
            }
        });

    }

    public void prendiUtentedaDbLocale(SharedPreferences preferenceSID, String defaultValue,int uidUtente,UtenteModel db,double lon,double lat,int life,int experience,int profileVersion,boolean positionShare,List<Utente> utentiList){
        //faccio la chiamata asincrona
        ListenableFuture<Utente> lf = db.utenteDao().getUserById(uidUtente);
        //aggiungo un listener che viene eseguito quando la chiamata asincrona è completata
        lf.addListener(() -> {
            try {
                Utente utente = lf.get();
     

                if (utente != null && utente.getProfileVersion() == profileVersion) {
                    Log.d("TAG", utente.toString());
                    // Aggiungi utente alla lista
                    utente.setLon(lon);
                    utente.setLat(lat);
                    utente.setLife(life);
                    utente.setExperience(experience);
                    utente.setShareLocation(positionShare);
                    utentiList.add(utente);

                    numChiamateCompletate++;

                    if (numChiamateCompletate == numChiamateTotali) {
                        Utente utenteVuoto = new Utente(0,"","",0,0,0,false); // Crea un utente vuoto
                        utentiList.add(utenteVuoto);
                        // Tutte le chiamate sono complete, aggiorna il LiveData


                        utentiLiveData.setValue(utentiList);
                    }

                    Log.d("Davide", utentiList.toString());
                } else if(utente != null && utente.getProfileVersion() != profileVersion){
                    prendiUtenteDaApi("aggiorna",uidUtente,preferenceSID,db,defaultValue,lat,lon,utentiList,positionShare);
                }else{
                    // Se il mostro non è presente nel database locale, lo recupero dall'API
                    prendiUtenteDaApi("inserisci",uidUtente,preferenceSID,db,defaultValue,lat,lon,utentiList,positionShare);
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },  ContextCompat.getMainExecutor(context));
    }

    public void prendiUtenteDaApi(String operazione,int uidUtente,SharedPreferences PreferenceSID,UtenteModel db,String defaultValue,double lat,double lon,List<Utente> utentiList,boolean positionShare){
        //chiedo all'API il mostro con l'ID specificato
        Call<ResponseUtenteData> prendiUtenteCall = apiInterface.prendiUtente(uidUtente,PreferenceSID.getString("SID", defaultValue));
        prendiUtenteCall.enqueue(new Callback<ResponseUtenteData>() {
            @Override
            public void onResponse(Call<ResponseUtenteData> call, retrofit2.Response<ResponseUtenteData> response) {
                if (!response.isSuccessful()) {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.d("Davide", "Error: " + response.code() + ", Error Body: " + errorBody);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
                Log.d("APIRanking", "Utente preso da API");
                ResponseUtenteData result = response.body();

                if(operazione == "inserisci"){
                    Utente utenteCreato = new Utente(result.getUid(),result.getName(),result.getPicture(),result.getLife(),result.getExperience(),result.getProfileVersion(),result.isPositionShare());
                    //inserisce l'utente nel database
                    //faccio la chiamata asincrona
                    ListenableFuture<Void> lf = db.utenteDao().InserisciUtente(utenteCreato);
                    //aggiungo un listener che viene eseguito quando la chiamata asincrona è completata
                    lf.addListener(() -> {
                        Log.d("TAG", "Chiamata al db locale completata");
                    }, ContextCompat.getMainExecutor(context));
                    utenteCreato.setLon(lon);
                    utenteCreato.setLat(lat);
                    utenteCreato.setShareLocation(PositionShare);
                    utentiList.add(utenteCreato);
                    Log.d("Davide", utentiList.toString());

                    numChiamateCompletate++;
                    if (numChiamateCompletate == numChiamateTotali) {
                        Utente utenteVuoto = new Utente(0,"","",0,0,0,false); // Crea un utente vuoto

                        utentiList.add(utenteVuoto);
                        // Tutte le chiamate sono complete, aggiorna il LiveData
                        utentiLiveData.setValue(utentiList);
                    }
                }else if(operazione == "aggiorna"){
                    ListenableFuture<Utente> lf = db.utenteDao().getUserById(uidUtente);
                    lf.addListener(() -> {
                        try {
                            Utente utente = lf.get();
                            if (utente != null) {
                                // Modifica i dettagli dell'Utente
                                utente.setLat(lat);
                                utente.setLon(lon);
                                utente.setPicture(result.getPicture());
                                utente.setLife(result.getLife());
                                utente.setExperience(result.getExperience());
                                utente.setShareLocation(result.isPositionShare());
                                utente.setProfileVersion(result.getProfileVersion());
                                // Esegui l'operazione di aggiornamento
                                ListenableFuture<Void> updateFuture = db.utenteDao().AggiornaUtente(utente);
                                updateFuture.addListener(() -> {
                                    Log.d("TAG", "Aggiornamento utente completato");
                                    utentiList.add(utente);
                                    numChiamateCompletate++;
                                    if (numChiamateCompletate == numChiamateTotali) {
                                        Utente utenteVuoto = new Utente(0,"","",0,0,0,false); // Crea un utente vuoto
                                        utentiList.add(utenteVuoto);

                                        // Tutte le chiamate sono complete, aggiorna il LiveData
                                        utentiLiveData.setValue(utentiList);
                                    }

                                }, ContextCompat.getMainExecutor(context));
                            }
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }, ContextCompat.getMainExecutor(context));
                }


            }
            @Override
            public void onFailure(Call<ResponseUtenteData> call, Throwable t) {
                Log.d("Davide", "Error: " + t.getMessage());
            }
        });

    }

    public LiveData<String> getText() {
        return mText;
    }
}