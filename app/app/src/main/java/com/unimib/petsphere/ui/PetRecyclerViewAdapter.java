package com.unimib.petsphere.ui;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.unimib.petsphere.R;
import com.unimib.petsphere.data.model.PetModel;
import com.unimib.petsphere.ui.placeholder.PlaceholderContent.PlaceholderItem;
import com.unimib.petsphere.databinding.PreviewPetCardBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class PetRecyclerViewAdapter extends RecyclerView.Adapter<PetRecyclerViewAdapter.ViewHolder> {
    private int layout;
    private List<PetModel> petList;

    public PetRecyclerViewAdapter(int layout, List<PetModel> petList) {
        this.layout=layout;
        this.petList=petList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        return new ViewHolder(PreviewPetCardBinding.inflate(LayoutInflater.from(layout), viewGroup, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.getTextViewNome().setText(petList.get(position).getName());
        holder.getTextViewTipoAnimale().setText(petList.get(position).getAnimal_type());
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