package com.gpp.devoluciondeenvases.adapter;

import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gpp.devoluciondeenvases.R;
import com.gpp.devoluciondeenvases.clases.Producto;
import com.gpp.devoluciondeenvases.clases.Sector;

import java.util.ArrayList;
import java.util.List;

public class AdapterSector extends RecyclerView.Adapter<AdapterSector.NoteViewHolder> {

    private List<Sector> notes;
    private OnNoteSelectedListener onNoteSelectedListener;
    private OnNoteDetailListener onDetailListener;

    public AdapterSector() {
        this.notes = new ArrayList<>();
    }

    public AdapterSector(List<Sector> notes) {
        this.notes = notes;
    }


    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View elementoTitular = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note_sectores, parent, false);

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

    public List<Sector> getNotes() {
        return notes;
    }

    public void setNotes(List<Sector> notes) {
        this.notes = notes;
    }

    public void setOnNoteSelectedListener(OnNoteSelectedListener onNoteSelectedListener) {
        this.onNoteSelectedListener = onNoteSelectedListener;
    }

    public void setOnDetailListener(OnNoteDetailListener onDetailListener) {
        this.onDetailListener = onDetailListener;
    }

    public interface OnNoteSelectedListener {
        void onClick(Sector note);
    }

    public interface OnNoteDetailListener {
        void onDetail(Sector note);
    }

    public Sector getposicionactual(int position) {
        return notes.get(position);
    }


    public class NoteViewHolder extends RecyclerView.ViewHolder {
        private TextView nombre;
        private TextView numero;
        private CheckBox checkBox;
        private LinearLayout layout;



        public NoteViewHolder(View item) {
            super(item);

            nombre = (TextView) item.findViewById(R.id.txtnombresector);
            numero = (TextView) item.findViewById(R.id.txtnumero);
            checkBox = (CheckBox) item.findViewById(R.id.checkBox);
            layout  = (LinearLayout) item.findViewById(R.id.layout);

        //falta color

        }

        public void bind(final Sector sector) {

            nombre.setText(sector.getNombreSector());
            numero.setText(sector.getNumeroSector());
            layout.setBackgroundColor(Color.parseColor(sector.getColorSector()));

            if (sector.getHabilitadoSector() == 1){
               // checkBox.isChecked();
                checkBox.setChecked(true);
            }else{
                checkBox.setChecked(false);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onNoteSelectedListener != null) {
                        onNoteSelectedListener.onClick(sector);
                    }
                }
            });
        }
    }
}
