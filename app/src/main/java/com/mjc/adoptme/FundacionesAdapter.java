package com.mjc.adoptme;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class FundacionesAdapter extends RecyclerView.Adapter<FundacionesAdapter.FundacionViewHolder> {

    private List<FundacionesActivity.Fundacion> fundaciones = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private OnContactClickListener onContactClickListener;
    private OnLocationClickListener onLocationClickListener;

    public interface OnItemClickListener {
        void onItemClick(FundacionesActivity.Fundacion fundacion);
    }

    public interface OnContactClickListener {
        void onContactClick(FundacionesActivity.Fundacion fundacion);
    }

    public interface OnLocationClickListener {
        void onLocationClick(FundacionesActivity.Fundacion fundacion);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnContactClickListener(OnContactClickListener listener) {
        this.onContactClickListener = listener;
    }

    public void setOnLocationClickListener(OnLocationClickListener listener) {
        this.onLocationClickListener = listener;
    }

    public void setFundaciones(List<FundacionesActivity.Fundacion> fundaciones) {
        this.fundaciones = fundaciones;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FundacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fundacion, parent, false);
        return new FundacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FundacionViewHolder holder, int position) {
        FundacionesActivity.Fundacion fundacion = fundaciones.get(position);
        holder.bind(fundacion);

        // Animación de entrada
        holder.itemView.setAlpha(0f);
        holder.itemView.setTranslationY(50f);

        AnimatorSet set = new AnimatorSet();
        ObjectAnimator alpha = ObjectAnimator.ofFloat(holder.itemView, "alpha", 0f, 1f);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(holder.itemView, "translationY", 50f, 0f);

        set.playTogether(alpha, translationY);
        set.setDuration(300);
        set.setStartDelay(position * 50L);
        set.setInterpolator(new DecelerateInterpolator());
        set.start();
    }

    @Override
    public int getItemCount() {
        return fundaciones.size();
    }

    class FundacionViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ImageView ivFoundation;
        private TextView tvNombre;
        private TextView tvUbicacion;
        private TextView tvTotalAnimales;
        private LinearLayout layoutChips;
        private Chip chipPerros;
        private Chip chipGatos;
        private Chip chipOtros;
        private MaterialButton btnContactar;
        private MaterialButton btnUbicacion;

        public FundacionViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardFundacion);
            ivFoundation = itemView.findViewById(R.id.ivFoundation);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvUbicacion = itemView.findViewById(R.id.tvUbicacion);
            tvTotalAnimales = itemView.findViewById(R.id.tvTotalAnimales);
            layoutChips = itemView.findViewById(R.id.layoutChips);
            chipPerros = itemView.findViewById(R.id.chipPerros);
            chipGatos = itemView.findViewById(R.id.chipGatos);
            chipOtros = itemView.findViewById(R.id.chipOtros);
            btnContactar = itemView.findViewById(R.id.btnContactar);
            btnUbicacion = itemView.findViewById(R.id.btnUbicacion);
        }

        public void bind(FundacionesActivity.Fundacion fundacion) {
            tvNombre.setText(fundacion.getNombre());
            tvUbicacion.setText(fundacion.getUbicacion());

            int totalAnimales = fundacion.getCantidadPerros() + fundacion.getCantidadGatos() + fundacion.getCantidadOtros();
            tvTotalAnimales.setText(totalAnimales + " animales en total");

            // Mostrar/ocultar chips según tipo de animales
            chipPerros.setVisibility(fundacion.isTienePerros() ? View.VISIBLE : View.GONE);
            chipGatos.setVisibility(fundacion.isTieneGatos() ? View.VISIBLE : View.GONE);
            chipOtros.setVisibility(fundacion.isTieneOtros() ? View.VISIBLE : View.GONE);

            if (fundacion.isTienePerros()) {
                chipPerros.setText("Perros: " + fundacion.getCantidadPerros());
            }
            if (fundacion.isTieneGatos()) {
                chipGatos.setText("Gatos: " + fundacion.getCantidadGatos());
            }
            if (fundacion.isTieneOtros()) {
                chipOtros.setText("Otros: " + fundacion.getCantidadOtros());
            }

            // Click listeners
            cardView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    // Animación de click
                    ObjectAnimator.ofFloat(cardView, "scaleX", 1f, 0.95f, 1f).setDuration(200).start();
                    ObjectAnimator.ofFloat(cardView, "scaleY", 1f, 0.95f, 1f).setDuration(200).start();

                    v.postDelayed(() -> onItemClickListener.onItemClick(fundacion), 200);
                }
            });

            btnContactar.setOnClickListener(v -> {
                if (onContactClickListener != null) {
                    onContactClickListener.onContactClick(fundacion);
                }
            });

            btnUbicacion.setOnClickListener(v -> {
                if (onLocationClickListener != null) {
                    onLocationClickListener.onLocationClick(fundacion);
                }
            });
        }
    }
}