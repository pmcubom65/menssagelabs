package com.example.mensajesactividad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mensajesactividad.modelos.Chat;
import com.example.mensajesactividad.modelos.Grupo;

import java.util.ArrayList;

public class AdaptadorGrupos  extends RecyclerView.Adapter<AdaptadorGrupos.ViewHolder> {

    private ArrayList<Grupo> datos;
    // variable to hold context
    private Context context;


    public AdaptadorGrupos(Context context, ArrayList<Grupo> list) {
        datos=list;
        this.context=context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView nombre, membresia;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre=itemView.findViewById(R.id.nombregrupo);
            membresia=itemView.findViewById(R.id.membresia);



        }
    }



    @NonNull
    @Override
    public AdaptadorGrupos.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.holdergrupos, parent, false);

        return new AdaptadorGrupos.ViewHolder(v);
    }





    @Override
    public void onBindViewHolder(@NonNull AdaptadorGrupos.ViewHolder holder, int position) {
        holder.itemView.setTag(datos.get(position));

        holder.nombre.setText(datos.get(position).getNombre().toString());
        holder.membresia.setText(datos.get(position).getMiembros().toString());

    }

    @Override
    public int getItemCount()  {
        return datos.size();
    }
}
