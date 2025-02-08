package com.unimib.petsphere.ui.pet;

import static androidx.core.app.PendingIntentCompat.getActivity;

import android.app.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.unimib.petsphere.R;
import com.unimib.petsphere.data.model.PetModel;
import com.unimib.petsphere.data.repository.CatFactRepository;
import com.unimib.petsphere.data.repository.DogFactRepository;
import com.unimib.petsphere.data.repository.PetRepository;
import com.unimib.petsphere.util.ServiceLocator;
import com.unimib.petsphere.viewModel.CatFactViewModel;
import com.unimib.petsphere.viewModel.CatFactViewModelFactory;
import com.unimib.petsphere.viewModel.DogFactViewModel;
import com.unimib.petsphere.viewModel.DogFactViewModelFactory;
import com.unimib.petsphere.viewModel.PetViewModel;
import com.unimib.petsphere.viewModel.PetViewModelFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

public class ViewPetActivity extends AppCompatActivity {
    private EditText nome, soprannome, microchip, eta, compleanno, peso, colore, allergie, note;
    private Spinner tipo;
    private ImageView petImageView;
    private PetViewModel petViewModel;
    private DogFactViewModel dogFactViewModel;
    private CatFactViewModel catFactViewModel;
    private Button editPetButton, savePetButton, editImageButton, deletePetButton, factButton;
    private boolean isEditing = false, bfact=false;
    private PetModel pet;
    String petImagePath;
    String[] tipi ;
    int selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pet);
        tipi=this.getApplication().getResources().getStringArray(R.array.tipi_animali);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        PetRepository petRepository =
                ServiceLocator.getInstance().getPetsRepository(
                        this.getApplication(),
                        this.getApplication().getResources().getBoolean(R.bool.debug_mode)
                );
        DogFactRepository dogFactRepository =  ServiceLocator.getInstance().getDogFactRepository(
                this.getApplication(),
                this.getApplication().getResources().getBoolean(R.bool.debug_mode)
        );
        CatFactRepository catFactRepository =  ServiceLocator.getInstance().getCatFactRepository(
                this.getApplication(),
                this.getApplication().getResources().getBoolean(R.bool.debug_mode)
        );

        petViewModel = new ViewModelProvider(
                this,
                new PetViewModelFactory(petRepository)).get(PetViewModel.class);
        dogFactViewModel = new ViewModelProvider(
                this,
                new DogFactViewModelFactory(dogFactRepository)).get(DogFactViewModel.class);
        catFactViewModel = new ViewModelProvider(
                this,
                new CatFactViewModelFactory(catFactRepository)).get(CatFactViewModel.class);

        nome = findViewById(R.id.text_nome);
        soprannome = findViewById(R.id.text_soprannome);
        microchip = findViewById(R.id.text_microchip);
        eta = findViewById(R.id.text_eta);
        compleanno = findViewById(R.id.text_compleanno);
        peso = findViewById(R.id.text_peso);
        colore = findViewById(R.id.colore);
        tipo = findViewById(R.id.tipo);
        allergie = findViewById(R.id.allergie);
        note = findViewById(R.id.note);
        petImageView = findViewById(R.id.pet_image);

        editPetButton = findViewById(R.id.edit_btn);
        savePetButton = findViewById(R.id.save_btn);
        deletePetButton = findViewById(R.id.delete_btn);
        editImageButton = findViewById(R.id.edit_image);
        factButton = findViewById(R.id.fact_btn);

        pet = (PetModel) getIntent().getSerializableExtra("pet");

        if (pet != null) {
            populateFields();
            setEditable(false);
            if(pet.getAnimal_type().equals("Cane")||pet.getAnimal_type().equals("Gatto")){
                bfact=true;
            }
            factButton.setVisibility(bfact ? View.VISIBLE : View.GONE);
        }

        editPetButton.setOnClickListener(v -> {
            isEditing = !isEditing;
            setEditable(isEditing);
            editPetButton.setVisibility(View.GONE);
            savePetButton.setVisibility(isEditing ? View.VISIBLE : View.GONE);
            editImageButton.setVisibility(isEditing ? View.VISIBLE : View.GONE);
            petImagePath=pet.getImage();
        });

        savePetButton.setOnClickListener(v -> {
            isEditing = !isEditing;
            updatePetData();
            petViewModel.updatePet(pet);
            setEditable(false);
            savePetButton.setVisibility(View.GONE);
            editPetButton.setVisibility(View.VISIBLE);
            editImageButton.setVisibility(View.GONE);
            Toast.makeText(this, "Modifiche salvate", Toast.LENGTH_SHORT).show();
        });

        deletePetButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(R.string.conferma_eliminazione)
                    .setMessage(R.string.messaggio_conferma_eliminazione)
                    .setPositiveButton(R.string.elimina, (dialog, id) -> {
                        petViewModel.deletePet(pet);
                        finish();
                    })
                    .setNegativeButton(R.string.annulla, (dialog, id) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        });




        petViewModel.getDeleteMsg().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });

        editImageButton.setOnClickListener(v -> openImageChooser());

        factButton.setOnClickListener(view -> {
            if(pet.getAnimal_type().equals("Cane")){
            dogFactViewModel.refreshFact();
            dogFactViewModel.getDogFact().observe(this, fact ->
                    Snackbar.make(view, fact, Snackbar.LENGTH_LONG).show()
            );
            }else if(pet.getAnimal_type().equals("Gatto")){
                catFactViewModel.refreshFact();
                catFactViewModel.getCatFact().observe(this, fact ->
                        Snackbar.make(view, fact, Snackbar.LENGTH_LONG).show()
                );
            }
        });

    }

    private void populateFields() {
        nome.setText(pet.getName());
        soprannome.setText(pet.getNickname());
        microchip.setText(pet.getMicrochip());
        eta.setText(String.valueOf(pet.getAge()));
        compleanno.setText(pet.getBirthday());
        peso.setText(String.valueOf(pet.getWeight()));
        colore.setText(pet.getColor());
        allergie.setText(pet.getAllergies());
        note.setText(pet.getNotes());
        selected=Arrays.asList(tipi).indexOf(pet.getAnimal_type());
        tipo.setSelection(selected);



        if (pet.getImage() != null) {
            String image = pet.getImage();
            File imgFile = new File(image);
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            petImageView.setImageBitmap(bitmap);
        }
    }

    private void setEditable(boolean enabled) {
        nome.setEnabled(enabled);
        soprannome.setEnabled(enabled);
        microchip.setEnabled(enabled);
        eta.setEnabled(enabled);
        compleanno.setEnabled(enabled);
        peso.setEnabled(enabled);
        colore.setEnabled(enabled);
        tipo.setEnabled(enabled);
        allergie.setEnabled(enabled);
        note.setEnabled(enabled);
    }

    private void updatePetData() {
        pet.setName(nome.getText().toString());
        pet.setNickname(soprannome.getText().toString());
        pet.setMicrochip(microchip.getText().toString());
        pet.setAge(eta.getText().toString());
        pet.setBirthday(compleanno.getText().toString());
        pet.setWeight(Double.parseDouble(peso.getText().toString()));
        pet.setColor(colore.getText().toString());
        pet.setAnimal_type(tipo.getSelectedItem().toString());
        pet.setAllergies(allergie.getText().toString());
        pet.setNotes(note.getText().toString());
        pet.setImage(petImagePath);
        petViewModel.updatePet(pet);
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                petImageView.setImageBitmap(bitmap);


                String imagePath = saveImageToInternalStorage(bitmap);

                if (imagePath != null) {
                    petImagePath = imagePath;
                }

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Errore nel caricamento dell'immagine", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private String saveImageToInternalStorage(Bitmap bitmap) {
        File directory = new File(getFilesDir(), "pet_images");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = "img_" + System.currentTimeMillis() + ".jpg";
        File imageFile = new File(directory, fileName);

        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.flush();
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}