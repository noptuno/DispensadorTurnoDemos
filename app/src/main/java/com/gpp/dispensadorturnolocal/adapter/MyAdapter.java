package com.gpp.dispensadorturnolocal.adapter;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.gpp.dispensadorturnolocal.R;
import com.gpp.dispensadorturnolocal.clases.Producto;

import java.util.List;

import static java.lang.Integer.parseInt;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<Producto> productos;
    private int layout;
    private OnclickListener itemclicklistener;
    public Activity activity;


    public MyAdapter(List<Producto> movie, int layout, Activity c, OnclickListener listener) {
        this.productos = movie;
        this.layout = layout;
        this.itemclicklistener = listener;
        this.activity = c;

    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // conetex = parent.getContext();

        // Inflamos el layout y se lo pasamos al constructor del ViewHolder, donde manejaremos
        // toda la lógica como extraer los datos, referencias...

        View v = LayoutInflater.from(activity).inflate(layout, parent, false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Llamamos al método Bind del ViewHolder pasándole objeto y listener
        holder.bind(productos.get(position), itemclicklistener);

    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Elementos UI a rellenar
        private TextView descripcion;

        private Button btnsumar;
        private Button btnrestar;
        private TextView cantidad,txtprecio;

        public ViewHolder(View itemView) {
            super(itemView);
            descripcion = itemView.findViewById(R.id.txt_descripcion_devolucion);
            btnsumar = itemView.findViewById(R.id.btnsuma);
            btnrestar = itemView.findViewById(R.id.btnrestar);
            cantidad = itemView.findViewById(R.id.txtcantidad);
            txtprecio= itemView.findViewById(R.id.txtprecio);


        }


        public void bind(final Producto productos, final OnclickListener listener) {

            txtprecio.setText(""+productos.getPrecio().toString());
            descripcion.setText(productos.getDescripcion());
            cantidad.setText(""+productos.getCantidad());

            this.btnsumar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (productos.getCantidad()<50){
                        listener.OnitemClick(productos, getAdapterPosition());
                    }

                }
            });

            this.btnrestar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (productos.getCantidad()>0){
                        listener.OnitemClickrestar(productos, getAdapterPosition());
                    }

                }
            });

        }

        public void reset(final Producto productos){

            cantidad.setText(0);

        }


    }

    // Declaramos nuestra interfaz con el/los método/s a implementar
    public interface OnclickListener {
        void OnitemClick(Producto productos, int position);
        void OnitemClickrestar(Producto productos, int position);
    }




}
