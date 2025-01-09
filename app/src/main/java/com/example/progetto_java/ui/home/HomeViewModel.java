package com.example.progetto_java.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.Room;

import com.example.progetto_java.R;
import com.example.progetto_java.api.ApiInterface;
import com.example.progetto_java.api.ResponseAttivaOggetto;
import com.example.progetto_java.api.ResponseObjects;
import com.example.progetto_java.api.ResponseRanking;
import com.example.progetto_java.api.ResponseUtenteData;
import com.example.progetto_java.api.ResponseUtentiVicini;
import com.example.progetto_java.api.RetrofitProvider;
import com.example.progetto_java.api.SignUpResponse;
import com.example.progetto_java.model.Oggetto;
import com.example.progetto_java.model.OggettoModel;
import com.example.progetto_java.model.Utente;
import com.example.progetto_java.model.UtenteModel;
import com.example.progetto_java.ui.classifica.ClassificaViewModel;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;

public class HomeViewModel extends ViewModel {

    ApiInterface apiInterface= RetrofitProvider.getApinterface();
    private final MutableLiveData<String> mText;
    public LiveData<String> getText() {
        return mText;
    }
    private String profileName;
    private int profileImage;
    private UtenteModel utenteDb;
    private OggettoModel oggettiDb;
    private Context context;

    private int Life;

    private MutableLiveData<Integer> lifeLiveData = new MutableLiveData<>();

    private boolean died=false;

    private MutableLiveData<List<Utente>> utentiLiveData = new MutableLiveData<>();

    private int numChiamateCompletate = 0;
    private int numChiamateTotali = 0;

    //ho messo un listener quando la vita cambia così il dialog nel fragment aspetta l'aggiornamento prima di andare avanti
    public LiveData<Integer> getLifeLiveData() {
        return lifeLiveData;
    }

    public void setLife(int life) {
        this.Life = life;
        lifeLiveData.postValue(life);
    }


    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
        profileName = "UserProva";
        profileImage = R.drawable.profile_pic;
    }

    public String getProfileName() {
        return profileName;
    }
    public void setContext(Context context) {
        this.context = context;
        utenteDb = Room.databaseBuilder(context, UtenteModel.class, "utenti")
                .build();
        oggettiDb = Room.databaseBuilder(context, OggettoModel.class, "oggetti")
                .build();
    }

    public int getProfileImage() {
        return profileImage;
    }
    //voglio anche i setter perche' nel caso voglio cambiare il nome utente e l'immagine di profilo
    //e cosi' posso fare il set di questi due attributi
    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public void setProfileImage(int profileImage) {
        this.profileImage = profileImage;
    }

    // Metodo per salvare il SID
    public void salvaSID(SharedPreferences PreferenceSID, String defaultValue){

        //se non è stato salvato ancora nulla con la chiave SID_KEY restituisce defaultValue
        if(PreferenceSID.getString("SID", defaultValue).equals(defaultValue)){
            //qui attivo la chiamata signUp
            Call<SignUpResponse> signUpCall = apiInterface.register();
            signUpCall.enqueue(new Callback<SignUpResponse>() {
                @Override
                public void onResponse(Call<SignUpResponse> call, retrofit2.Response<SignUpResponse> response) {
                    if (!response.isSuccessful()) {
                        Log.d("Davide", "Error: " + response.code());
                        return;
                    }
                    SignUpResponse result = response.body();
                    Log.d("Davide", "Il SID non era salvato in modo persistente, ora è : " + result.sid);
                    //inserisco il SID nella sharedPreferences in modo da salvarlo in modo consistente
                    SharedPreferences.Editor editor = PreferenceSID.edit();
                    editor.putString("SID",result.sid);
                    editor.apply();
                    SharedPreferences.Editor uid = PreferenceSID.edit();
                    editor.putString("uid", String.valueOf(result.uid));
                    editor.apply();

                    Utente utenteCreato = new Utente(result.uid,"utenteBase",null,100,0,0,false);

                    ListenableFuture<Void> lf = utenteDb.utenteDao().InserisciUtente(utenteCreato);
                    //aggiungo un listener che viene eseguito quando la chiamata asincrona è completata
                    lf.addListener(() -> {
                        Log.d("TAG", "Chiamata al db locale completata");
                    }, ContextCompat.getMainExecutor(context));
                }
                @Override
                public void onFailure(Call<SignUpResponse> call, Throwable t) {
                    Log.d("Davide", "Error: " + t.getMessage());
                }
            });
        }else{
            Log.d("Davide","Il SID è salvato in modo persistente ed è : "+ PreferenceSID.getString("SID", defaultValue));
        }
    }

    public void setCoordinatesDB(double lat, double lon){
        SharedPreferences preferences = context.getSharedPreferences("myPreferences", MODE_PRIVATE);;
        String uidString = preferences.getString("uid", "default"); // "default" è il valore di default se la chiave non esiste
        int uidUtente = 0;
        Log.d("infoUser", "UID: " + uidString);

        if(!uidString.equals("default")){
             uidUtente = Integer.parseInt(uidString);

        }

        ListenableFuture<Void> lf = utenteDb.utenteDao().setUserCoordinates(lon,lat,uidUtente);
        //aggiungo un listener che viene eseguito quando la chiamata asincrona è completata
        lf.addListener(() -> {
            Log.d("TAG", "Coordinate inserite nel DB");

        },  ContextCompat.getMainExecutor(context));

    }

    public LiveData<Integer> getUserAmuletLevel() {
        MutableLiveData<Integer> amuletLevelLiveData = new MutableLiveData<>();

        SharedPreferences preferences = context.getSharedPreferences("myPreferences", MODE_PRIVATE);
        String uidString = preferences.getString("uid", "default");
        int uidUtente = 0;

        if (!uidString.equals("default")) {
            uidUtente = Integer.parseInt(uidString);
        }

        ListenableFuture<Utente> lf = utenteDb.utenteDao().getUserById(uidUtente);

        //aggiungo un listener che viene eseguito quando la chiamata asincrona è completata
        lf.addListener(() -> {
            try {
                Utente utente = lf.get();
                if (utente != null) {
                    //Log.d("TAG", utente.toString());

                    int idAmulet = utente.getAmulet();
                    ListenableFuture<Oggetto> ogg = oggettiDb.oggettoDao().getObjectById(idAmulet);

                    ogg.addListener(() -> {
                        Log.d("TAG", "Oggetto preso dal DB");
                        try {
                            Oggetto oggetto = ogg.get();
                            if (oggetto != null) {
                                Log.d("TAG", oggetto.toString());
                                int amuletLevel = oggetto.getLevel();

                                // Utilizza setValue per impostare il valore su amuletLevelLiveData
                                amuletLevelLiveData.setValue(amuletLevel);
                            }else{
                                Log.d("Prova", "amuleto utente nullo");
                                int defaultValue = 1;
                                amuletLevelLiveData.setValue(defaultValue);
                            }

                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }, ContextCompat.getMainExecutor(context));
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(context));

        return amuletLevelLiveData;
    }

    public void attivaOggetto(int idOggetto){
        SharedPreferences preferences = context.getSharedPreferences("myPreferences", MODE_PRIVATE);;

        String SID= preferences.getString("SID", "default");
        OggettoAttivazione oggettoAttivazione = new OggettoAttivazione();
        oggettoAttivazione.sid = SID;

        Call<ResponseAttivaOggetto> attivaOggettoCall = apiInterface.attivaOggetto(idOggetto, oggettoAttivazione);
        attivaOggettoCall.enqueue(new Callback<ResponseAttivaOggetto>() {
            @Override
            public void onResponse(Call<ResponseAttivaOggetto> call, retrofit2.Response<ResponseAttivaOggetto> response) {
                if (!response.isSuccessful()) {
                    Log.d("Davide", "Error: " + response.code());
                    return;
                }

                ResponseAttivaOggetto result = response.body();

                String uidString = preferences.getString("uid", "default"); // "default" è il valore di default se la chiave non esiste
                int uidUtente = 0;

                if(!uidString.equals("default")){
                    uidUtente = Integer.parseInt(uidString);
                }

                ListenableFuture<Utente> lf = utenteDb.utenteDao().getUserById(uidUtente);
                //aggiungo un listener che viene eseguito quando la chiamata asincrona è completata
                lf.addListener(() -> {
                    try {
                        Utente utente = lf.get();
                        if (utente != null) {
                            Log.d("nuovavita"," "+result.getLife());
                            utente.setLife(result.getLife());
                            if(result.isDied()== true){
                                died=true;
                                utente.setWeapon(0);
                                utente.setAmulet(0);
                                utente.setArmor(0);
                            }

                            utente.setExperience(result.getExperience());
                            if(result.getWeapon()!=null){
                                utente.setWeapon(result.getWeapon());
                            }
                            if(result.getArmor()!=null){
                                utente.setArmor(result.getArmor());
                            }
                            if(result.getAmulet()!=null){
                                utente.setAmulet(result.getAmulet());
                            }
                            ListenableFuture<Void> updateFuture = utenteDb.utenteDao().AggiornaUtente(utente);
                            updateFuture.addListener(() -> {
                                Log.d("Home", "Inserito utente dopo attivazione oggetto");
                                Life=utente.getLife();
                                setLife(utente.getLife());
                            }, ContextCompat.getMainExecutor(context));

                        }
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }, ContextCompat.getMainExecutor(context));
            }
            @Override
            public void onFailure(Call<ResponseAttivaOggetto> call, Throwable t) {
                Log.d("Davide", "Error: " + t.getMessage());
            }
        });
    }

    public LiveData<Integer> getUserWeaponLevel() {
        MutableLiveData<Integer> weaponLevelLiveData = new MutableLiveData<>();

        SharedPreferences preferences = context.getSharedPreferences("myPreferences", MODE_PRIVATE);
        String uidString = preferences.getString("uid", "default");
        int uidUtente = 0;

        if (!uidString.equals("default")) {
            uidUtente = Integer.parseInt(uidString);
        }

        ListenableFuture<Utente> lf = utenteDb.utenteDao().getUserById(uidUtente);

        //aggiungo un listener che viene eseguito quando la chiamata asincrona è completata
        lf.addListener(() -> {
            try {
                Utente utente = lf.get();
                if (utente != null) {
                    Log.d("TAG", utente.toString());

                    int idWeapon = utente.getWeapon();
                    ListenableFuture<Oggetto> ogg = oggettiDb.oggettoDao().getObjectById(idWeapon);

                    ogg.addListener(() -> {
                        Log.d("TAG", "Oggetto preso dal DB");
                        try {
                            Oggetto oggetto = ogg.get();
                            if (oggetto != null) {
                                Log.d("TAG", oggetto.toString());
                                int weaponLevel = oggetto.getLevel();

                                // Utilizza setValue per impostare il valore su amuletLevelLiveData
                                weaponLevelLiveData.setValue(weaponLevel);
                            }else{
                                Log.d("Prova", "amuleto utente nullo");
                                int defaultValue = 1;
                                weaponLevelLiveData.setValue(defaultValue);
                            }

                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }, ContextCompat.getMainExecutor(context));
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(context));

        return weaponLevelLiveData;
    }

    public boolean getDied(){
        return died;
    }
    public LiveData<Utente> ritornaUtente() {
        MutableLiveData<Utente> utenteLiveData = new MutableLiveData<>();

        SharedPreferences preferences = context.getSharedPreferences("myPreferences", MODE_PRIVATE);
        String uidString = preferences.getString("uid", "default");
        int uidUtente = 0;

        if (!uidString.equals("default")) {
            uidUtente = Integer.parseInt(uidString);
        }

        //chiamata al db
        ListenableFuture<Utente> lf = utenteDb.utenteDao().getUserById(uidUtente);

        //aggiungo un listener che viene eseguito quando la chiamata asincrona è completata
        lf.addListener(() -> {
            try {
                Utente utente = lf.get();
                if (utente != null) {
                    utenteLiveData.setValue(utente);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(context));

        return utenteLiveData;
    }

    public void prendiVicini( SharedPreferences preferenceSID, String defaultValue, UtenteModel utentiDb){
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

                    double lon = utente.getLon();
                    double lat = utente.getLat();
                Call<List<ResponseUtentiVicini>> rankingCall = apiInterface.prendiUtentiVicini(preferenceSID.getString("SID", defaultValue), lat, lon);
                rankingCall.enqueue(new Callback<List<ResponseUtentiVicini>>() {
                    @Override
                    public void onResponse(Call<List<ResponseUtentiVicini>> call, retrofit2.Response<List<ResponseUtentiVicini>> response) {
                        if (response.isSuccessful()) {
                            List<ResponseUtentiVicini> resultList = response.body();
                            List<Utente> utentiList = new ArrayList<>();

                            numChiamateCompletate = 0;
                            numChiamateTotali = resultList.size();

                            for (ResponseUtentiVicini result : resultList) {
                                // Aggiungi le informazioni di longitudine e latitudine all'Utente
                                double lon = result.getLon();
                                double lat = result.getLat();
                                int life = result.getLife();
                                int experience = result.getExperience();

                                prendiUtentedaDbLocale(preferenceSID, defaultValue, result.getUid(), utentiDb, lon, lat,life,experience,result.getProfileVersion(),true,utentiList);
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
                    public void onFailure(Call<List<ResponseUtentiVicini>> call, Throwable t) {
                        Log.d("Davide", "Error: " + t.getMessage());
                    }
                });

                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            },  ContextCompat.getMainExecutor(context));
    }

    public void prendiUtentedaDbLocale(SharedPreferences preferenceSID, String defaultValue,int uidUtente,UtenteModel db,double lon,double lat,int life,int experience,int profileVersion,boolean positionShare,List<Utente> utentiList){
        //faccio la chiamata asincrona
        ListenableFuture<Utente> lf = db.utenteDao().getUserById(uidUtente);
        //aggiungo un listener che viene eseguito quando la chiamata asincrona è completata
        lf.addListener(() -> {

            Log.d("TAG", "Chiamata al db locale completata");


            try {
                Utente utente = lf.get();
                //Log.d("TAG", ""+utente.getProfileVersion()+" -"+profileVersion);
                if(utente == null){
                }

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
                        // Tutte le chiamate sono complete, aggiorna il LiveData
                        utentiLiveData.setValue(utentiList);
                    }

                } else if(utente != null && utente.getProfileVersion() != profileVersion){

                    prendiUtenteDaApi("aggiorna",uidUtente,preferenceSID,db,defaultValue,lat,lon,utentiList);
                }else{
                    // Se il mostro non è presente nel database locale, lo recupero dall'API

                    prendiUtenteDaApi("inserisci",uidUtente,preferenceSID,db,defaultValue,lat,lon,utentiList);
                }

            }catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Log.e("debug", "Eccezione durante l'esecuzione asincrona", e);

            }
        },  ContextCompat.getMainExecutor(context));
    }


    public void prendiUtenteDaApi(String operazione,int uidUtente,SharedPreferences PreferenceSID,UtenteModel db,String defaultValue,double lat,double lon,List<Utente> utentiList){
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
                Log.d("Davide", "Utente preso da API");
                ResponseUtenteData result = response.body();

                if(operazione == "inserisci"){
                    Utente utenteCreato = new Utente(result.getUid(),result.getName(),result.getPicture(),result.getLife(),result.getExperience(),result.getProfileVersion(),true);
                    //inserisce l'utente nel database
                    //faccio la chiamata asincrona
                    ListenableFuture<Void> lf = db.utenteDao().InserisciUtente(utenteCreato);
                    //aggiungo un listener che viene eseguito quando la chiamata asincrona è completata
                    lf.addListener(() -> {
                        Log.d("TAG", "Chiamata al db locale completata");
                    }, ContextCompat.getMainExecutor(context));
                    utenteCreato.setLon(lon);
                    utenteCreato.setLat(lat);
                    utentiList.add(utenteCreato);
                    Log.d("Davide", utentiList.toString());

                    numChiamateCompletate++;
                    if (numChiamateCompletate == numChiamateTotali) {
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

                                    numChiamateCompletate++;
                                    if (numChiamateCompletate == numChiamateTotali) {
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
    public LiveData<List<Utente>> getListaUtentiLiveData() {
        return utentiLiveData;
    }


    public class OggettoAttivazione
    {
        public String sid;
    }

    public void chiudiDb(){
        utenteDb.close();
        oggettiDb.close();
    }
    }


