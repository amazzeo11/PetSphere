package com.unimib.petsphere.ui;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.unimib.petsphere.R;
import com.unimib.petsphere.data.model.PetModel;
import com.unimib.petsphere.ui.placeholder.PlaceholderContent.PlaceholderItem;
import com.unimib.petsphere.databinding.PreviewPetCardBinding;

import java.util.List;

public class PetRecyclerViewAdapter extends RecyclerView.Adapter<PetRecyclerViewAdapter.ViewHolder> {
    private int layout;
    private List<PetModel> petList;
    private Context context;


    public PetRecyclerViewAdapter(int layout, List<PetModel> petList) {
        this.layout=layout;
        this.petList=petList;
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