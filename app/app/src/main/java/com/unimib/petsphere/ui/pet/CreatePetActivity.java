package com.unimib.petsphere.ui.pet;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.unimib.petsphere.R;
import com.unimib.petsphere.data.model.PetModel;
import com.unimib.petsphere.data.repository.PetRepository;
import com.unimib.petsphere.util.ServiceLocator;
import com.unimib.petsphere.viewModel.PetViewModel;
import com.unimib.petsphere.viewModel.PetViewModelFactory;

public class CreatePetActivity extends AppCompatActivity {
    private EditText nome, soprannome, microchip, eta, compleanno, peso, colore, tipo, allergie, note;
    private Button saveButton;
    private PetViewModel petViewModel;

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
        colore = findViewById(R.id.edit_colore);
        tipo = findViewById(R.id.edit_tipo);
        allergie = findViewById(R.id.edit_allergie);
        note = findViewById(R.id.edit_note);
        saveButton = findViewById(R.id.btn_salva);


        saveButton.setOnClickListener(v -> {
            PetModel newPet = newPet();
            petViewModel.createPet(newPet);
        });


        petViewModel.getCreatePetMsg().observe(this, msg -> {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private PetModel newPet() {
        return new PetModel(
                null,
                tipo.getText().toString(),
                microchip.getText().toString(),
                nome.getText().toString(),
                soprannome.getText().toString(),
                Double.parseDouble(peso.getText().toString()),
                eta.getText().toString(),
                compleanno.getText().toString(),
                colore.getText().toString(),
                note.getText().toString(),
                allergie.getText().toString()
        );
    }
}
