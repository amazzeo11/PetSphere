package com.unimib.petsphere.ui.pet;
//Author: Alessia Mazzeo
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.unimib.petsphere.R;
import com.unimib.petsphere.data.model.PetModel;
import com.unimib.petsphere.data.repository.PetRepository;
import com.unimib.petsphere.util.ServiceLocator;
import com.unimib.petsphere.viewModel.PetViewModel;
import com.unimib.petsphere.viewModel.PetViewModelFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CreatePetActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText nome, soprannome, microchip, eta, compleanno, peso, allergie, note, razza;
    private Spinner colore, tipo;
    private Button saveButton, uploadImageButton;
    private ImageView petImageView;
    private PetViewModel petViewModel;
    private String petImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pet);

        PetRepository petRepository = ServiceLocator.getInstance().getPetsRepository(
                this.getApplication(),
                this.getApplication().getResources().getBoolean(R.bool.debug_mode)
        );
        petViewModel = new ViewModelProvider(this, new PetViewModelFactory(petRepository)).get(PetViewModel.class);

        nome = findViewById(R.id.edit_nome);
        soprannome = findViewById(R.id.edit_soprannome);
        microchip = findViewById(R.id.edit_microchip);
        eta = findViewById(R.id.edit_eta);
        compleanno = findViewById(R.id.edit_compleanno);
        peso = findViewById(R.id.edit_peso);
        colore = findViewById(R.id.spinnerColore);
        tipo = findViewById(R.id.spinnerTipo);
        razza = findViewById(R.id.edit_razza);
        allergie = findViewById(R.id.edit_allergie);
        note = findViewById(R.id.edit_note);
        petImageView = findViewById(R.id.pet_image);
        uploadImageButton = findViewById(R.id.image_btn);
        saveButton = findViewById(R.id.btn_salva);

        uploadImageButton.setOnClickListener(v -> openImageChooser());

        saveButton.setOnClickListener(v -> {
            PetModel newPet = newPet();
            petViewModel.createPet(newPet);
        });

        petViewModel.getCreatePetMsg().observe(this, msg -> {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            finish();
        });

    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
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
                Toast.makeText(this, R.string.errore_immagine, Toast.LENGTH_SHORT).show();
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


    private PetModel newPet() {
        return new PetModel(
                petImagePath,
                tipo.getSelectedItem().toString(),
                razza.getText().toString(),
                microchip.getText().toString(),
                nome.getText().toString(),
                soprannome.getText().toString(),
                Double.parseDouble(peso.getText().toString()),
                eta.getText().toString(),
                compleanno.getText().toString(),
                colore.getSelectedItem().toString(),
                note.getText().toString(),
                allergie.getText().toString()
        );
    }
}

