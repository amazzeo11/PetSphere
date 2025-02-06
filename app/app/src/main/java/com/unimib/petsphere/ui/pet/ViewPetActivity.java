package com.unimib.petsphere.ui.pet;

import android.app.Activity;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.unimib.petsphere.R;
import com.unimib.petsphere.data.model.PetModel;
import com.unimib.petsphere.data.repository.PetRepository;
import com.unimib.petsphere.util.ServiceLocator;
import com.unimib.petsphere.viewModel.PetViewModel;
import com.unimib.petsphere.viewModel.PetViewModelFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ViewPetActivity extends AppCompatActivity {
    private EditText nome, soprannome, microchip, eta, compleanno, peso, colore, tipo, allergie, note;
    private ImageView petImageView;
    private PetViewModel petViewModel;
    private Button editPetButton, savePetButton, editImageButton, deletePetButton;
    private boolean isEditing = false;
    private PetModel pet;
    String petImagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pet);

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

        petViewModel = new ViewModelProvider(
                this,
                new PetViewModelFactory(petRepository)).get(PetViewModel.class);

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

        pet = (PetModel) getIntent().getSerializableExtra("pet");

        if (pet != null) {
            populateFields();
            setEditable(false);
        }

        editPetButton.setOnClickListener(v -> {
            isEditing = !isEditing;
            setEditable(isEditing);
            editPetButton.setVisibility(View.GONE);
            savePetButton.setVisibility(isEditing ? View.VISIBLE : View.GONE);
            editImageButton.setVisibility(isEditing ? View.VISIBLE : View.GONE);
        });

        savePetButton.setOnClickListener(v -> {
            updatePetData();
            petViewModel.updatePet(pet);
            setEditable(false);
            savePetButton.setVisibility(View.GONE);
            Toast.makeText(this, "Modifiche salvate", Toast.LENGTH_SHORT).show();
        });

        deletePetButton.setOnClickListener(v -> {
            petViewModel.deletePet(pet);
            finish();
        });

        petViewModel.getDeleteMsg().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });

        editImageButton.setOnClickListener(v -> openImageChooser());
    }

        private void populateFields() {
        nome.setText(pet.getName());
        soprannome.setText(pet.getNickname());
        microchip.setText(pet.getMicrochip());
        eta.setText(String.valueOf(pet.getAge()));
        compleanno.setText(pet.getBirthday());
        peso.setText(String.valueOf(pet.getWeight()));
        colore.setText(pet.getColor());
        tipo.setText(pet.getAnimal_type());
        allergie.setText(pet.getAllergies());
        note.setText(pet.getNotes());



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
        pet.setAnimal_type(tipo.getText().toString());
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