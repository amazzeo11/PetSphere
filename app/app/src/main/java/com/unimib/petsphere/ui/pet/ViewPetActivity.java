package com.unimib.petsphere.ui.pet;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.unimib.petsphere.R;
import com.unimib.petsphere.data.model.PetModel;

public class ViewPetActivity extends AppCompatActivity {
    private TextView nome, soprannome, microchip, eta, compleanno, colore, tipo, allergie, note;
    private ImageView petImageView;

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
        colore = findViewById(R.id.colore);
        tipo = findViewById(R.id.tipo);
        allergie = findViewById(R.id.allergie);
        note = findViewById(R.id.note);


        PetModel pet = (PetModel) getIntent().getSerializableExtra("pet");

        if (pet != null) {

            nome.setText(pet.getName());
            soprannome.setText("@string/soprannome" + pet.getNickname());
            microchip.setText("@string/numero_di_microchip" + pet.getMicrochip());
            eta.setText("@string/numero_di_microchip" + pet.getAge());
            compleanno.setText("@string/compleanno" + pet.getBirthday());
            colore.setText("@string/colore" + pet.getColore());
            tipo.setText("@string/tipo_animale" + pet.getAnimal_type());
            allergie.setText("@string/allergie" + pet.getAllergies());
            note.setText("@string/note" + pet.getNotes());

        }
    }
}