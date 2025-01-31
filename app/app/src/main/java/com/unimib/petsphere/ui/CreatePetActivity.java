package com.unimib.petsphere.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import com.unimib.petsphere.R;
import com.unimib.petsphere.data.PetRoomDatabase;
import com.unimib.petsphere.data.model.PetModel;

public class CreatePetActivity extends AppCompatActivity {
    private EditText editNome, editSoprannome, editMicrochip, editEta, editCompleanno, editAllergie, editNote;
    private Spinner spinnerTipo, spinnerColore;
    private Button btnSalva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pet); // Usa la card come layout


        editNome = findViewById(R.id.edit_nome);
        editSoprannome = findViewById(R.id.edit_soprannome);
        editMicrochip = findViewById(R.id.edit_microchip);
        editEta = findViewById(R.id.edit_eta);
        editCompleanno = findViewById(R.id.edit_compleanno);
        spinnerTipo = findViewById(R.id.spinner_tipo);
        spinnerColore = findViewById(R.id.spinner_colore);
        editAllergie = findViewById(R.id.edit_allergie);
        editNote = findViewById(R.id.edit_note);
        btnSalva = findViewById(R.id.btn_salva);


        btnSalva.setOnClickListener(v -> {
            PetModel pet = new PetModel(
                    spinnerTipo.getSelectedItem().toString(),
                    editMicrochip.getText().toString(),
                    editNome.getText().toString(),
                    editSoprannome.getText().toString(),
                    0, // Peso non gestito
                    Integer.parseInt(editEta.getText().toString()),
                    editCompleanno.getText().toString(),
                    editNote.getText().toString(),
                    editAllergie.getText().toString()
            );

            PetRoomDatabase.getDatabase(getApplicationContext()).PetDAO().insertAll(pet);
            finish();
        });
    }
}
