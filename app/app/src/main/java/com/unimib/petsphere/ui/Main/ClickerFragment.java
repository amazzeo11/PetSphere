package com.unimib.petsphere.ui.Main;
//Author: Alessia Mazzeo
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.unimib.petsphere.R;

public class ClickerFragment extends Fragment {

    private MediaPlayer mediaPlayer;
    private TextView click1, click2, click3;
    public ClickerFragment() {
        // Required empty public constructor
    }

    public static ClickerFragment newInstance() {
        return new ClickerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_clicker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button button = view.findViewById(R.id.clicker_button);
        Button info =view.findViewById(R.id.buttoninfo);
        click1 = view.findViewById(R.id.click1);
        click2 = view.findViewById(R.id.click2);
        click3 = view.findViewById(R.id.click3);
        click1.setVisibility(View.GONE);
        click2.setVisibility(View.GONE);
        click3.setVisibility(View.GONE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        info.setOnClickListener(v -> {
        builder.setTitle(R.string.titolo_clicker_info)
                .setMessage(R.string.testo_clicker)
                .setPositiveButton(R.string.ho_capito, (dialog, id) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
        });

        button.setOnClickListener(v -> {
            int n = (int)(Math.random()*3)+1;
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }

            switch(n){
                case 1:
                    click1.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    click2.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    click3.setVisibility(View.VISIBLE);
                    break;
            }

            mediaPlayer = MediaPlayer.create(getContext(), R.raw.clicker_sound);
            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(mp -> {
                mediaPlayer.release();
                click1.setVisibility(View.GONE);
                click2.setVisibility(View.GONE);
                click3.setVisibility(View.GONE);
                mediaPlayer = null;
            });
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
