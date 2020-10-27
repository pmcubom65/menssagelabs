package com.example.mensajesactividad;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mensajesactividad.modelos.Chat;
import com.example.mensajesactividad.modelos.Usuario;

import java.util.ArrayList;

public class AdaptadorListadoChats  extends RecyclerView.Adapter<AdaptadorListadoChats.ViewHolder>{

    private ArrayList<Chat> datos;
    // variable to hold context
    private Context context;

    public AdaptadorListadoChats(Context context, ArrayList<Chat> list) {
        datos=list;
        this.context=context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView inicio, codigo;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            inicio=itemView.findViewById(R.id.iniciochat);
            codigo=itemView.findViewById(R.id.codigochat);

        }
    }



    @NonNull
    @Override
    public AdaptadorListadoChats.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.holderlistachats, parent, false);

        return new AdaptadorListadoChats.ViewHolder(v);
    }





    @Override
    public void onBindViewHolder(@NonNull AdaptadorListadoChats.ViewHolder holder, int position) {
        holder.itemView.setTag(datos.get(position));

        holder.codigo.setText(datos.get(position).getNombre().toString());
        holder.inicio.setText("Inicio: "+datos.get(position).getInicio().replace('T', ' ').toString());


    }

    @Override
    public int getItemCount()  {
        return datos.size();
    }

}
