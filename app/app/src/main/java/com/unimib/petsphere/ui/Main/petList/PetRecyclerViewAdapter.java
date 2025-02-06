package com.unimib.petsphere.ui.Main.petList;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.unimib.petsphere.R;
import com.unimib.petsphere.data.model.PetModel;


import java.io.File;
import java.util.List;

public class PetRecyclerViewAdapter extends RecyclerView.Adapter<PetRecyclerViewAdapter.ViewHolder> {
    private int layout;
    private List<PetModel> petList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onPetItemClick(PetModel pet);
    }

    public PetRecyclerViewAdapter(int layout, List<PetModel> petList, OnItemClickListener onItemClickListener) {
        this.layout=layout;
        this.petList=petList;
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

        String imagePath = pet.getImage();
        File imgFile = new File(imagePath);

        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.getImageView().setImageBitmap(bitmap);
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
        }

        public TextView getTextViewNome(){
        return textViewNome;
        }
        public TextView getTextViewTipoAnimale(){
            return textViewTipoAnimale;
        }

        public ImageView getImageView(){
            return imageView;
        }
    }
}