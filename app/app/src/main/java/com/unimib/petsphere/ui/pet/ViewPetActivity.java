package com.unimib.petsphere.ui.pet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.unimib.petsphere.R;
import com.unimib.petsphere.data.model.PetModel;
import com.unimib.petsphere.data.model.Result;
import com.unimib.petsphere.data.repository.PetRepository;
import com.unimib.petsphere.util.ServiceLocator;
import com.unimib.petsphere.viewModel.PetViewModel;
import com.unimib.petsphere.viewModel.PetViewModelFactory;

public class ViewPetActivity extends AppCompatActivity {
    private TextView nome, soprannome, microchip, eta, compleanno, peso, colore, tipo, allergie, note;
    private ImageView petImageView;
    private PetViewModel petViewModel;

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


        PetModel pet = (PetModel) getIntent().getSerializableExtra("pet");

        if (pet != null) {

            nome.setText(pet.getName());
            soprannome.setText("Soprannome: " + pet.getNickname());
            microchip.setText("Numero di microchip: " + pet.getMicrochip());
            eta.setText("Età: " + pet.getAge());
            compleanno.setText("Compleanno: " + pet.getBirthday());
            peso.setText(String.format("Peso: %.2f kg", pet.getWeight()));
            colore.setText("Colore: " + pet.getColor());
            tipo.setText("Tipo animale: " + pet.getAnimal_type());
            allergie.setText(pet.getAllergies());
            note.setText(pet.getNotes());

        }
        PetRepository petRepository =
                ServiceLocator.getInstance().getPetsRepository(
                        this.getApplication(),
                        this.getApplication().getResources().getBoolean(R.bool.debug_mode)
                );

        petViewModel = new ViewModelProvider(
                this,
                new PetViewModelFactory(petRepository)).get(PetViewModel.class);


        Button newPetButton = findViewById(R.id.delete_btn);
        newPetButton.setOnClickListener(v -> {
            petViewModel.deletePet(pet);
            finish();
        });

        petViewModel.getDeleteMsg().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });




    }
}