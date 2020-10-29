package com.example.mensajesactividad;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mensajesactividad.modelos.Mensaje;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {


    private ArrayList<Mensaje> datos;

    public MyAdapter(Context context, ArrayList<Mensaje> list) {
        datos=list;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mitextview;
        public TextView fechayhora;
        public TextView telefonodelmensaje;


        public MyViewHolder(View v) {
            super(v);
            mitextview=v.findViewById(R.id.textView);
            fechayhora=v.findViewById(R.id.fechayhora);
            telefonodelmensaje=v.findViewById(R.id.telefonodelmensaje);
        }



    }




    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
       View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.holder, parent, false);

       return new MyViewHolder(view);
    }





    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        LinearLayout.LayoutParams misparametros=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        misparametros.setMarginStart(440);
        String telefono=Autenticacion.numerotelefono;

        if (!datos.get(position).getTelefono().toString().equals(telefono)) {
            holder.mitextview.setLayoutParams(misparametros);
            holder.fechayhora.setLayoutParams(misparametros);
            holder.telefonodelmensaje.setLayoutParams(misparametros);
            holder.mitextview.setBackgroundColor(R.color.minaranja);
        }

        holder.itemView.setTag(datos.get(position));


        holder.mitextview.setText(datos.get(position).getContenido().toString());
        holder.fechayhora.setText(datos.get(position).getFecha().toString());
        holder.telefonodelmensaje.setText(datos.get(position).getNombre().toString());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return datos.size();
    }
}
