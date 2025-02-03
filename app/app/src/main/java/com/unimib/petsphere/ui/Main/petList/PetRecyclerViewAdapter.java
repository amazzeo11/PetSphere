package com.unimib.petsphere.ui.Main.petList;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unimib.petsphere.R;
import com.unimib.petsphere.data.model.PetModel;


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
        holder.getTextViewNome().setText(petList.get(position).getName());
        holder.getTextViewTipoAnimale().setText(petList.get(position).getAnimal_type());

        holder.itemView.setOnClickListener(v -> {

            if (onItemClickListener != null) {
                onItemClickListener.onPetItemClick(petList.get(position));
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

        public ViewHolder(View view) {
            super(view);
            textViewNome = view.findViewById(R.id.nomePet);
            textViewTipoAnimale = view.findViewById(R.id.tipoAnimale);
        }

        public TextView getTextViewNome(){
        return textViewNome;
        }
        public TextView getTextViewTipoAnimale(){
            return textViewTipoAnimale;
        }

    }
}