package com.mjc.adoptme.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.mjc.adoptme.R;
import com.mjc.adoptme.models.Fundacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FundacionesPortafolioAdapter extends RecyclerView.Adapter<FundacionesPortafolioAdapter.FundacionViewHolder> {

    private List<Fundacion> fundaciones = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Fundacion fundacion);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setFundaciones(List<Fundacion> fundaciones) {
        this.fundaciones = fundaciones != null ? fundaciones : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FundacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fundacion_portafolio, parent, false);
        return new FundacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FundacionViewHolder holder, int position) {
        holder.bind(fundaciones.get(position));
    }

    @Override
    public int getItemCount() {
        return fundaciones.size();
    }

    class FundacionViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardFundacion;
        private TextView tvNombreFundacion;
        private TextView tvSucursal;
        private TextView tvDireccion;
        private TextView tvCantidadAnimales;

        public FundacionViewHolder(@NonNull View itemView) {
            super(itemView);
            cardFundacion = itemView.findViewById(R.id.cardFundacion);
            tvNombreFundacion = itemView.findViewById(R.id.tvNombreFundacion);
            tvSucursal = itemView.findViewById(R.id.tvSucursal);
            tvDireccion = itemView.findViewById(R.id.tvDireccion);
            tvCantidadAnimales = itemView.findViewById(R.id.tvCantidadAnimales);

            cardFundacion.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && position < fundaciones.size()) {
                        onItemClickListener.onItemClick(fundaciones.get(position));
                    }
                }
            });
        }

        public void bind(Fundacion fundacion) {
            if (fundacion == null) return;
            
            tvNombreFundacion.setText(fundacion.getNombreFundacion() != null ? 
                fundacion.getNombreFundacion() : "Fundación sin nombre");
            tvSucursal.setText(fundacion.getNombreSucursal() != null ? 
                fundacion.getNombreSucursal() : "Sucursal principal");
            tvDireccion.setText(fundacion.getDireccion() != null ? 
                fundacion.getDireccion() : "Dirección no disponible");
            
            int cantidadAnimales = fundacion.getCantidadAnimales();
            String animalesText = String.format(Locale.getDefault(), 
                "%d animal%s disponible%s", 
                cantidadAnimales,
                cantidadAnimales != 1 ? "es" : "",
                cantidadAnimales != 1 ? "s" : ""
            );
            tvCantidadAnimales.setText(animalesText);
        }
    }
}