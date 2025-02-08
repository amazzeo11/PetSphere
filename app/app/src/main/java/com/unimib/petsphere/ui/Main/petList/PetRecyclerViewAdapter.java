package com.unimib.petsphere.ui.Main.petList;

import androidx.cardview.widget.CardView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;

import com.unimib.petsphere.R;
import com.unimib.petsphere.data.model.PetModel;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PetRecyclerViewAdapter extends RecyclerView.Adapter<PetRecyclerViewAdapter.ViewHolder> {
    private int layout;
    private List<PetModel> petList;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private CardView cardView;
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

    public interface OnItemClickListener {
        void onPetItemClick(PetModel pet);
    }

    public PetRecyclerViewAdapter(int layout, List<PetModel> petList, OnItemClickListener onItemClickListener) {
        this.layout = layout;
        this.petList = petList;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(layout, viewGroup, false);
        if (this.context == null) this.context = viewGroup.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        PetModel pet = petList.get(position);
        holder.getTextViewNome().setText(pet.getName());
        holder.getTextViewTipoAnimale().setText(pet.getAnimal_type());

        String petColor = pet.getColor();
        if (colorMap.containsKey(petColor)) {
            int colorResId = colorMap.get(petColor);
            int color = ContextCompat.getColor(context, colorResId);
            int transparentColor = ColorUtils.setAlphaComponent(color, 80);
            holder.getCardView().setCardBackgroundColor(transparentColor);
        }

        String imagePath = pet.getImage();
        if (imagePath != null) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.getImageView().setImageBitmap(bitmap);
            } else {
                holder.getImageView().setImageResource(R.drawable.paw_icon);
            }
        } else {
            holder.getImageView().setImageResource(R.drawable.paw_icon);
        }

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onPetItemClick(pet);
            }
        });
    }



    @Override
    public int getItemCount() {
        return petList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView textViewNome;
        public final TextView textViewTipoAnimale;
        public final ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            textViewNome = view.findViewById(R.id.nomePet);
            textViewTipoAnimale = view.findViewById(R.id.tipoAnimale);
            imageView = view.findViewById(R.id.imagePet);
            cardView = view.findViewById(R.id.cardView);
        }
        public TextView getTextViewNome() {
            return textViewNome;
        }
        public TextView getTextViewTipoAnimale() {
            return textViewTipoAnimale;
        }
        public ImageView getImageView() {
            return imageView;
        }
        public CardView getCardView() {
            return cardView;
        }
    }

}