package com.fatecgru.planvintas.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fatecgru.planvintas.R;
import com.fatecgru.planvintas.model.Planta;

import java.util.List;

public class PlantaAdapter extends RecyclerView.Adapter<PlantaAdapter.PlantaViewHolder> {

    private Context context;
    private List<Planta> listaPlantas;

    public PlantaAdapter(Context context, List<Planta> listaPlantas) {
        this.context = context;
        this.listaPlantas = listaPlantas;
    }

    @NonNull
    @Override
    public PlantaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.item_planta, parent, false);
        return new PlantaViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantaViewHolder holder, int position) {
        Planta planta = listaPlantas.get(position);

        holder.txtNome.setText(planta.getNome());
        holder.txtEspecie.setText(planta.getEspecie());

        // Verificar se o método getImagemPlanta() está implementado na classe Planta e retorna um drawable válido
        if (planta.getImagemPlanta() != 0) {
            holder.imgPlanta.setImageResource(planta.getImagemPlanta());
        } else {
            holder.imgPlanta.setImageResource(R.drawable.ic_launcher_foreground); // Imagem padrão caso não tenha
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Cuidar.class);
            intent.putExtra("id_planta", planta.getId());
            intent.putExtra("nome_planta", planta.getNome());
            intent.putExtra("idade_planta", planta.getIdadeDias());
            intent.putExtra("moedas_planta", planta.getMoedas());
            intent.putExtra("cor", planta.getCorVaso());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaPlantas.size();
    }

    public static class PlantaViewHolder extends RecyclerView.ViewHolder {
        TextView txtNome, txtEspecie;
        ImageView imgPlanta;

        public PlantaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.txtNome);
            txtEspecie = itemView.findViewById(R.id.txtEspecie);
            imgPlanta = itemView.findViewById(R.id.imgPlanta);
        }
    }
}
