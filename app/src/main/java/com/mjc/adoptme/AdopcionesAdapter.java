package com.mjc.adoptme;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class AdopcionesAdapter extends RecyclerView.Adapter<AdopcionesAdapter.AdopcionViewHolder> {

    private List<MisAdopcionesActivity.Adopcion> adopciones = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private OnCancelClickListener onCancelClickListener;

    public interface OnItemClickListener {
        void onItemClick(MisAdopcionesActivity.Adopcion adopcion);
    }

    public interface OnCancelClickListener {
        void onCancelClick(MisAdopcionesActivity.Adopcion adopcion);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnCancelClickListener(OnCancelClickListener listener) {
        this.onCancelClickListener = listener;
    }

    public void setAdopciones(List<MisAdopcionesActivity.Adopcion> adopciones) {
        this.adopciones = adopciones;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdopcionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_adopcion, parent, false);
        return new AdopcionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdopcionViewHolder holder, int position) {
        MisAdopcionesActivity.Adopcion adopcion = adopciones.get(position);
        holder.bind(adopcion);

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
        return adopciones.size();
    }

    class AdopcionViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView tvNombreMascota;
        private TextView tvRaza;
        private TextView tvEstado;
        private TextView tvFecha;
        private ImageView ivPaw;
        private MaterialButton btnCancelar;
        private View viewStatusIndicator;

        public AdopcionViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardAdopcion);
            tvNombreMascota = itemView.findViewById(R.id.tvNombreMascota);
            tvRaza = itemView.findViewById(R.id.tvRaza);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            ivPaw = itemView.findViewById(R.id.ivPaw);
            btnCancelar = itemView.findViewById(R.id.btnCancelar);
            viewStatusIndicator = itemView.findViewById(R.id.viewStatusIndicator);
        }

        public void bind(MisAdopcionesActivity.Adopcion adopcion) {
            tvNombreMascota.setText(adopcion.getNombreMascota());
            tvRaza.setText(adopcion.getRaza());
            tvEstado.setText(adopcion.getEstadoDescripcion());
            tvFecha.setText(adopcion.getFechaSolicitud());

            // Configurar colores según estado
            int colorResId;
            int pawColorResId;
            switch (adopcion.getEstado()) {
                case EN_PROCESO:
                    colorResId = R.color.colorAccent2;
                    pawColorResId = R.color.colorAccent1;
                    btnCancelar.setVisibility(View.VISIBLE);
                    break;
                case COMPLETADA:
                    colorResId = R.color.colorPrimary;
                    pawColorResId = R.color.colorPrimary;
                    btnCancelar.setVisibility(View.GONE);
                    break;
                case CANCELADA:
                    colorResId = R.color.colorAccent3;
                    pawColorResId = R.color.colorAccent3;
                    btnCancelar.setVisibility(View.GONE);
                    break;
                default:
                    colorResId = R.color.black;
                    pawColorResId = R.color.black;
                    btnCancelar.setVisibility(View.GONE);
            }

            tvEstado.setTextColor(ContextCompat.getColor(itemView.getContext(), colorResId));
            viewStatusIndicator.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), colorResId));
            ivPaw.setColorFilter(ContextCompat.getColor(itemView.getContext(), pawColorResId));

            // Click listeners
            cardView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    // Animación de click
                    ObjectAnimator.ofFloat(cardView, "scaleX", 1f, 0.95f, 1f).setDuration(200).start();
                    ObjectAnimator.ofFloat(cardView, "scaleY", 1f, 0.95f, 1f).setDuration(200).start();

                    v.postDelayed(() -> onItemClickListener.onItemClick(adopcion), 200);
                }
            });

            btnCancelar.setOnClickListener(v -> {
                if (onCancelClickListener != null) {
                    onCancelClickListener.onCancelClick(adopcion);
                }
            });
        }
    }
}