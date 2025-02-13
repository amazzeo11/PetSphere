package com.unimib.petsphere.ui.Main.petList.pet;
//Author: Alessia Mazzeo
import static androidx.core.app.PendingIntentCompat.getActivity;

import android.app.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.lifecycle.ViewModelProvider;

import com.unimib.petsphere.R;
import com.unimib.petsphere.data.model.PetModel;
import com.unimib.petsphere.ui.viewModel.CatFactViewModel;
import com.unimib.petsphere.ui.viewModel.CatFactViewModelFactory;
import com.unimib.petsphere.ui.viewModel.DogFactViewModel;
import com.unimib.petsphere.ui.viewModel.DogFactViewModelFactory;
import com.unimib.petsphere.ui.viewModel.PetViewModel;
import com.unimib.petsphere.ui.viewModel.PetViewModelFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ViewPetActivity extends AppCompatActivity {
    private EditText nome, soprannome, microchip, eta, compleanno, peso, allergie, note, razza;
    private Spinner colore, tipo;
    private ImageView petImageView;
    private PetViewModel petViewModel;
    private DogFactViewModel dogFactViewModel;
    private CatFactViewModel catFactViewModel;
    private Button editPetButton, savePetButton, editImageButton, deletePetButton, factButton;
    private boolean isEditing = false, bfact=false;
    private PetModel pet;
    String petImagePath;
    String[] tipi , colori;
    int selected;
    private CardView petCardView;
    private final Map<String, Integer> colorMap = new HashMap<String, Integer>() {{
        put("Rosso", R.color.rosso);
        put("Verde", R.color.verde);
        put("Blu", R.color.blu);
        put("Viola", R.color.viola);
        put("Azzurro", R.color.azzurro);
        put("Rosa", R.color.rosa);
        put("Giallo", R.color.giallo);
        put("Arancione", R.color.arancione);
        put("Bianco", R.color.bianco);
        put("Nero", R.color.nero);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pet);
        tipi=this.getApplication().getResources().getStringArray(R.array.tipi_animali);
        petCardView = findViewById(R.id.card);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());


        petViewModel = new ViewModelProvider(
                this,
                new PetViewModelFactory(this.getApplication())).get(PetViewModel.class);
        dogFactViewModel = new ViewModelProvider(
                this,
                new DogFactViewModelFactory(this.getApplication())).get(DogFactViewModel.class);
        catFactViewModel = new ViewModelProvider(
                this,
                new CatFactViewModelFactory(this.getApplication())).get(CatFactViewModel.class);

        nome = findViewById(R.id.text_nome);
        soprannome = findViewById(R.id.text_soprannome);
        microchip = findViewById(R.id.text_microchip);
        eta = findViewById(R.id.text_eta);
        compleanno = findViewById(R.id.text_compleanno);
        peso = findViewById(R.id.text_peso);
        colore = findViewById(R.id.colore);
        tipo = findViewById(R.id.tipo);
        razza = findViewById(R.id.razza);
        allergie = findViewById(R.id.allergie);
        note = findViewById(R.id.note);
        petImageView = findViewById(R.id.pet_image);

        editPetButton = findViewById(R.id.edit_btn);
        savePetButton = findViewById(R.id.save_btn);
        deletePetButton = findViewById(R.id.delete_btn);
        editImageButton = findViewById(R.id.edit_image);
        factButton = findViewById(R.id.fact_btn);

        colori = getResources().getStringArray(R.array.colori_pet);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, colori);
        colore.setAdapter(adapter);

        pet = (PetModel) getIntent().getSerializableExtra("pet");


        if (pet != null) {
            populateFields();
            setEditable(false);
            setCardBackgroundColor(pet.getColor());
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
            setEditable(false);
            savePetButton.setVisibility(View.GONE);
            editPetButton.setVisibility(View.VISIBLE);
            editImageButton.setVisibility(View.GONE);
            Toast.makeText(this, R.string.modifiche_salvate, Toast.LENGTH_SHORT).show();
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
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            if (pet.getAnimal_type().equals("Cane")) {
                dogFactViewModel.refreshFact();
                dogFactViewModel.getDogFact().observe(this, fact -> {
                    builder.setTitle(R.string.curiosita_cani)
                            .setMessage(fact)
                            .setPositiveButton(R.string.ok, (dialog, id) -> dialog.dismiss());

                    AlertDialog dialog = builder.create();
                    dialog.show();
                });
            } else if (pet.getAnimal_type().equals("Gatto")) {
                catFactViewModel.refreshFact();
                catFactViewModel.getCatFact().observe(this, fact -> {
                    builder.setTitle(R.string.curiosita_gatti)
                            .setMessage(fact)
                            .setPositiveButton(R.string.ok, (dialog, id) -> dialog.dismiss());

                    AlertDialog dialog = builder.create();
                    dialog.show();
                });
            }
        });

        colore.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedColor = colori[position];
                setCardBackgroundColor(selectedColor);
                pet.setColor(selectedColor);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    }

    private void setCardBackgroundColor(String colorName) {
        if (colorMap.containsKey(colorName)) {
            int baseColor = ContextCompat.getColor(this, colorMap.get(colorName));
            int transparentColor = ColorUtils.setAlphaComponent(baseColor, 80);
            petCardView.setCardBackgroundColor(transparentColor);
        } else {
            petCardView.setCardBackgroundColor(ColorUtils.setAlphaComponent(Color.WHITE, 80));
        }
    }


    private void populateFields() {
        nome.setText(pet.getName());
        soprannome.setText(pet.getNickname());
        microchip.setText(pet.getMicrochip());
        eta.setText(String.valueOf(pet.getAge()));
        compleanno.setText(pet.getBirthday());
        peso.setText(String.valueOf(pet.getWeight()));
        selected=Arrays.asList(tipi).indexOf(pet.getColor());
        allergie.setText(pet.getAllergies());
        note.setText(pet.getNotes());
        selected=Arrays.asList(tipi).indexOf(pet.getAnimal_type());
        tipo.setSelection(selected);
        razza.setText(pet.getBreed());
        int selectedColorIndex = Arrays.asList(colori).indexOf(pet.getColor());
        if (selectedColorIndex != -1) {
            colore.setSelection(selectedColorIndex);
        }



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
        razza.setEnabled(enabled);
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
        pet.setAnimal_type(tipo.getSelectedItem().toString());
        pet.setColor(colore.getSelectedItem().toString());
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
}