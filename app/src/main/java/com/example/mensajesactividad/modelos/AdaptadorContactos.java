package com.example.mensajesactividad.modelos;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mensajesactividad.R;
import com.example.mensajesactividad.controladores.MainActivity;
import com.example.mensajesactividad.controladores.MostrarContactos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AdaptadorContactos extends RecyclerView.Adapter<AdaptadorContactos.ViewHolder> {


    private ArrayList<Usuario> datos;
    // variable to hold context
    private Context context;

    public AdaptadorContactos(Context context, ArrayList<Usuario> list) {
        datos=list;
        this.context=context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView contacto1,contacto2, mibadge;
        ImageView imageView;
        CheckBox checkBox;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contacto1=itemView.findViewById(R.id.contacto1);
            contacto2=itemView.findViewById(R.id.contacto2);
            imageView=itemView.findViewById(R.id.imageView);
            checkBox=itemView.findViewById(R.id.checkBox);
            checkBox.setVisibility(View.GONE);

            mibadge=itemView.findViewById(R.id.badgecontacto);
            mibadge.setVisibility(View.GONE);
        }
    }


    @NonNull
    @Override
    public AdaptadorContactos.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.holdercontactos, parent, false);

        return new ViewHolder(v);
    }





    @Override
    public void onBindViewHolder(@NonNull AdaptadorContactos.ViewHolder holder, int position) {
        holder.itemView.setTag(datos.get(position));
   //     holder.foto.setImageBitmap(datos.get(position).getFoto());
        holder.contacto1.setText(datos.get(position).getNombre().toString());
        holder.contacto2.setText(datos.get(position).getTelefono().toString());
        String ruta=datos.get(position).getUri();
        Glide.with(context).load(Uri.parse(datos.get(position).getUri()))
                .placeholder(R.drawable.account_circle)
                .into(holder.imageView);


        if (Integer.parseInt(datos.get(position).getMensajesnoleidos())>0) {
            holder.mibadge.setVisibility(View.VISIBLE);
            holder.mibadge.setText(datos.get(position).getMensajesnoleidos());
        }



    }

    @Override
    public int getItemCount()  {



            return datos.size();


    }



}
