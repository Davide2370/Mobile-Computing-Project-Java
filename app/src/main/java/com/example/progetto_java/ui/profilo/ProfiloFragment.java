package com.example.progetto_java.ui.profilo;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.progetto_java.R;
import com.example.progetto_java.databinding.FragmentProfiloBinding;
import com.example.progetto_java.ui.home.HomeViewModel;
import com.example.progetto_java.ui.vicinanze.VicinanzeViewModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ProfiloFragment extends Fragment {

    private ProfiloViewModel profiloViewModel;
    private HomeViewModel homeViewModel;
    private FragmentProfiloBinding binding;

    private TextView profileNameTextView;

    private TextView profileLifeTextView;
    private TextView profileXPTextView;

    private ImageView ProfiloImageView;

    private ImageView AmuletoImageView;
    private ImageView ArmaImageView;
    private ImageView ArmaturaImageView;

    private View overlay;
    private ProgressBar spinner;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Infla il layout del fragment
        ViewGroup parentViewGroup = (ViewGroup) container.getParent();
        binding = FragmentProfiloBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        overlay = parentViewGroup.findViewById(R.id.overlay);
        spinner = parentViewGroup.findViewById(R.id.spinnerID);

        profiloViewModel = new ViewModelProvider(this).get(ProfiloViewModel.class);
        profiloViewModel.setContext(requireContext());
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.setContext(requireContext());
        TextView profileNameTextView = root.findViewById(R.id.profileName);
        TextView profileLifeTextView = root.findViewById(R.id.life_textView);
        TextView profileXPTextView = root.findViewById(R.id.experienceTextView);
        ProfiloImageView = root.findViewById(R.id.imageView);
        ImageView AmuletoImageView = root.findViewById(R.id.amuletoImageView);
        ImageView ArmaImageView = root.findViewById(R.id.armaImageView);
        ImageView ArmaturaImageView = root.findViewById(R.id.armaturaImageView);
        Switch switchCondivisionePosizione = root.findViewById(R.id.switchCondivisionePosizione);
        homeViewModel.ritornaUtente().observe(getViewLifecycleOwner(), utente -> {
            switchCondivisionePosizione.setChecked(utente.isShareLocation());

            profileNameTextView.setText(utente.getName());
            profileLifeTextView.setText(String.valueOf(utente.getLife()));
            profileXPTextView.setText(String.valueOf(utente.getExperience()));
            AmuletoImageView.setVisibility(View.GONE);
            ArmaImageView.setVisibility(View.GONE);
            ArmaturaImageView.setVisibility(View.GONE);
            root.findViewById(R.id.amuletoImageView).setOnClickListener(v -> {
                openArtefattoFragment(utente.getAmulet());
            });
            root.findViewById(R.id.armaImageView).setOnClickListener(v -> {
                openArtefattoFragment(utente.getWeapon());
            });
            root.findViewById(R.id.armaturaImageView).setOnClickListener(v -> {
                openArtefattoFragment(utente.getArmor());
            });
            if(utente.getAmulet()!=0){
                AmuletoImageView.setVisibility(View.VISIBLE);
            }
            if(utente.getWeapon()!=0){
                ArmaImageView.setVisibility(View.VISIBLE);
            }
            if(utente.getArmor()!=0){
                ArmaturaImageView.setVisibility(View.VISIBLE);
            }

            if(utente.getPicture()!= null){
                String base64Image = utente.getPicture();

                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);

                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                ProfiloImageView.setImageBitmap(decodedByte);
            }

            root.findViewById(R.id.imageView).setOnClickListener(v -> {
                Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickImageIntent.setType("image/*");

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                Intent chooserIntent = Intent.createChooser(pickImageIntent, "Seleziona o scatta una foto");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { takePictureIntent });

                startActivityForResult(chooserIntent, 1);
            });


            root.findViewById(R.id.matitaImageView).setOnClickListener(v -> {
                // Apri un dialog per la modifica del testo
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Modifica nome utente");

                // Crea un EditText all'interno del dialog
                EditText input = new EditText(requireContext());
                input.setText(profileNameTextView.getText());

                builder.setView(input);

                input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});


                // Imposta il bottone "OK" del dialog
                builder.setPositiveButton("OK", (dialog, which) -> {
                    String newProfileName = input.getText().toString();
                    // Verifica che la lunghezza sia compresa tra 1 e 15 caratteri
                    if (newProfileName.length() >= 1 && newProfileName.length() <= 15) {
                        // Modifica il nome utente
                        homeViewModel.setProfileName(newProfileName);
                        // Aggiorna il testo della TextView
                        binding.profileName.setText(newProfileName);
                        profiloViewModel.settaNome(newProfileName,overlay,spinner);

                    } else {
                        // Mostra un messaggio di errore se la lunghezza non è valida
                        Toast.makeText(requireContext(), "La lunghezza del nome deve essere compresa tra 1 e 15 caratteri", Toast.LENGTH_SHORT).show();
                    }
                });

                // Mostra il dialog
                builder.show();
            });

            switchCondivisionePosizione.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Aggiungi qui la logica per gestire la condivisione della posizione
                if (isChecked) {
                    // La condivisione della posizione è attivata
                    Toast.makeText(requireContext(), "Condivisione della posizione attivata", Toast.LENGTH_SHORT).show();
                    profiloViewModel.settaSharePosition(true,overlay,spinner);
                } else {
                    // La condivisione della posizione è disattivata
                    Toast.makeText(requireContext(), "Condivisione della posizione disattivata", Toast.LENGTH_SHORT).show();
                    profiloViewModel.settaSharePosition(false,overlay,spinner);
                }
            });

        });


        return root;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) { // Codice utilizzato per l'attività di selezione immagine
                if (data != null && data.getData() != null) {
                    // L'utente ha selezionato un'immagine dalla galleria
                    Uri selectedImageUri = data.getData();

                    // Converti l'immagine in base64
                    String base64Image = imageToBase64(selectedImageUri);
                    if(isBase64StringOver100KB(base64Image)){
                        showErrorDialog("Errore", "L'immagine selezionata è troppo grande. Max 100 KB consentiti.");
                    }else{
                        overlay.setVisibility(View.VISIBLE);
                        spinner.setVisibility(View.VISIBLE);
                        Log.d("TAG", "immagine più piccola di 100kb: ");
                        profiloViewModel.settaImmagine(base64Image,overlay,spinner);
                        ProfiloImageView.setImageURI(selectedImageUri);
                    }
                    Log.d("TAG", "Base64: " + base64Image);
                    // Ora puoi utilizzare base64Image come desideri
                } else if (data != null && data.getExtras() != null) {
                    // L'utente ha scattato una foto dalla fotocamera

                    // L'utente ha scattato una foto dalla fotocamera
                    Bitmap photo = (Bitmap) data.getExtras().get("data");

                    // Converti la foto in base64
                    String base64Photo = bitmapToBase64(photo);
                    if(isBase64StringOver100KB(base64Photo)){
                        showErrorDialog("Errore", "La foto scattata è troppo grande. Max 100 KB consentiti.");
                    }else{
                        Log.d("TAG", "immagine più piccola di 100kb: ");
                        ProfiloImageView.setImageBitmap(photo);
                    }
                    // Visualizza la foto nell'ImageView

                    Log.d("TAG", "Base64: " + base64Photo);
                }
            }
        } else {
            // L'utente ha annullato l'operazione di selezione o cattura dell'immagine
            // Puoi gestire questo caso in base alle tue esigenze
        }
    }

    // Metodo per convertire un'immagine Uri in base64
    private String imageToBase64(Uri imageUri) {
        try {
            ContentResolver contentResolver = requireActivity().getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(imageUri);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Metodo per convertire un'immagine Bitmap in base64
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    // Metodo per verificare se una stringa Base64 è più pesante di 100 KB
    private boolean isBase64StringOver100KB(String base64String) {
        String base64WithoutPadding = base64String.replaceAll("=*$", "");

        int decodedLength = base64WithoutPadding.length() * 6 / 8;

        int fileSizeKB = decodedLength / 1024;

        return fileSizeKB > 100;
    }

    private void showErrorDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null) // Puoi aggiungere un listener al pulsante "OK" se necessario
                .show();
    }
    // Metodo per aggiornare l'ImageView con una nuova immagine


    private void openArtefattoFragment(int idOggetto) {
        // Crea una nuova istanza del Fragment
        profiloViewModel.getObjectDetails(idOggetto).observe(getViewLifecycleOwner(), oggetto -> {

            MioArtefattoFragment fragment = MioArtefattoFragment.newInstance(oggetto.getName(),oggetto.getType(),oggetto.getLevel(),oggetto.getImage());

            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(
                    R.anim.transation1, R.anim.transation1
            );
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_profilo, fragment, null)
                    .addToBackStack("profilo")
                    .commit();
        });
    }
}
