package com.example.mensajesactividad.modelos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mensajesactividad.R;
import com.example.mensajesactividad.services.MyBroadcastReceiver;
import com.example.mensajesactividad.services.MyFirebaseInstanceService;

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

        holder.codigo.setText("Chat con " +datos.get(position).getNombre().toString());
        holder.inicio.setText("Iniciado: "+datos.get(position).getInicio().replace('T', ' ').toString());



    }

    @Override
    public int getItemCount()  {
        return datos.size();
    }

}
