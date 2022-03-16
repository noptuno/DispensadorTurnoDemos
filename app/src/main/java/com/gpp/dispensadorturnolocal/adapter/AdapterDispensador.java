package com.gpp.dispensadorturnolocal.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gpp.dispensadorturnolocal.R;
import com.gpp.dispensadorturnolocal.clases.Sector;

import java.util.ArrayList;
import java.util.List;

public class AdapterDispensador extends RecyclerView.Adapter<AdapterDispensador.NoteViewHolder> {

    private List<Sector> notes;
    private int CantidadSectores;
    private OnNoteSelectedListener onNoteSelectedListener;
    private OnNoteDetailListener onDetailListener;

    public AdapterDispensador() {
        this.notes = new ArrayList<>();
    }

    public AdapterDispensador(List<Sector> notes) {
        this.notes = notes;
    }


    public AdapterDispensador(int cantidad) {
        this.notes = new ArrayList<>();
        this.CantidadSectores = cantidad;
    }


    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View elementoTitular;

        if (CantidadSectores ==1){
            elementoTitular = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_note_sectores_imprimir_uno, parent, false);

        }else if (CantidadSectores ==2){
            elementoTitular = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_note_sectores_imprimir_dos, parent, false);
        }else{

            elementoTitular = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_note_sectors_imprimir, parent, false);
        }



        return new AdapterDispensador.NoteViewHolder(elementoTitular);
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
        private LinearLayout layout;



        public NoteViewHolder(View item) {
            super(item);

            nombre = (TextView) item.findViewById(R.id.txtnombresec);
            numero = (TextView) item.findViewById(R.id.txtnumerosec);
            layout  = (LinearLayout) item.findViewById(R.id.layoutsec);

        //falta color

        }

        public void bind(final Sector sector) {

            nombre.setText(sector.getNombreSector());
            numero.setText(""+sector.getNumeroSector());
            layout.setBackgroundColor(Color.parseColor(sector.getColorSector()));


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
