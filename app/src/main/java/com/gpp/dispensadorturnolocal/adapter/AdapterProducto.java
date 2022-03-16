package com.gpp.dispensadorturnolocal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.gpp.dispensadorturnolocal.R;
import com.gpp.dispensadorturnolocal.clases.Producto;

import java.util.ArrayList;
import java.util.List;

public class AdapterProducto extends RecyclerView.Adapter<AdapterProducto.NoteViewHolder> {

    private List<Producto> notes;
    private OnNoteSelectedListener onNoteSelectedListener;
    private OnNoteDetailListener onDetailListener;

    public AdapterProducto() {
        this.notes = new ArrayList<>();
    }

    public AdapterProducto(List<Producto> notes) {
        this.notes = notes;
    }


    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View elementoTitular = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note_producto, parent, false);

        return new NoteViewHolder(elementoTitular);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder view, int pos) {
        view.bind(notes.get(pos));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public List<Producto> getNotes() {
        return notes;
    }

    public void setNotes(List<Producto> notes) {
        this.notes = notes;
    }

    public void setOnNoteSelectedListener(OnNoteSelectedListener onNoteSelectedListener) {
        this.onNoteSelectedListener = onNoteSelectedListener;
    }

    public void setOnDetailListener(OnNoteDetailListener onDetailListener) {
        this.onDetailListener = onDetailListener;
    }

    public interface OnNoteSelectedListener {
        void onClick(Producto note);
    }

    public interface OnNoteDetailListener {
        void onDetail(Producto note);
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        private TextView descripcion;
        private TextView precio;


        public NoteViewHolder(View item) {
            super(item);

            descripcion = (TextView) item.findViewById(R.id.txt_descripcion);
            precio= (TextView) item.findViewById(R.id.txt_precio);

        }

        public void bind(final Producto producto) {

            descripcion.setText(producto.getDescripcion().toString());
            precio.setText(""+(producto.getPrecio().toString()));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onNoteSelectedListener != null) {
                        onNoteSelectedListener.onClick(producto);
                    }
                }
            });
        }
    }
}
