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
    private EditText editNome, editSoprannome, editMicrochip, editEta, editCompleanno, editAllergie, editNote, editWeight;
    private String spinnerTipo, spinnerColore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pet);


        editNome = findViewById(R.id.edit_nome);
        editSoprannome = findViewById(R.id.edit_soprannome);
        editMicrochip = findViewById(R.id.edit_microchip);
        editEta = findViewById(R.id.edit_eta);
        editWeight = findViewById(R.id.edit_weight);
        editCompleanno = findViewById(R.id.edit_compleanno);
        spinnerTipo = "cane";
        spinnerColore = "blu";
        editAllergie = findViewById(R.id.edit_allergie);
        editNote = findViewById(R.id.edit_note);
        Button saveButton = findViewById(R.id.btn_salva);


        saveButton.setOnClickListener(v -> {
            PetModel newPet = new PetModel();
            newPet.setName(editNome.getText().toString());
            newPet.setNickname(editSoprannome.getText().toString());
            newPet.setMicrochip(editMicrochip.getText().toString());
            newPet.setWeight(editWeight.getText().toString());
            newPet.setAge( editEta.getText().toString());
            newPet.setBirthday(editCompleanno.getText().toString());
            newPet.setAllergies(editAllergie.getText().toString());
            newPet.setNotes(editNote.getText().toString());
           /*
            PetModel pet = new PetModel(
                    spinnerTipo,
                    editMicrochip.getText().toString(),
                    editNome.getText().toString(),
                    editSoprannome.getText().toString(),
                    editWeight.getText().toString(),
                    editEta.getText().toString(),
                    editCompleanno.getText().toString(),
                    spinnerColore,
                    editNote.getText().toString(),
                    editAllergie.getText().toString()
            );
*/
            PetRoomDatabase.getDatabase(getApplicationContext()).PetDAO().insertAll(newPet);
            finish();
        });
    }
}
