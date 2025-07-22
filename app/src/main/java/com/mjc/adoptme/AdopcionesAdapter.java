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
import com.mjc.adoptme.models.AdopcionUsuario;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdopcionesAdapter extends RecyclerView.Adapter<AdopcionesAdapter.AdopcionViewHolder> {

    private List<AdopcionUsuario> adopciones = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private OnCancelClickListener onCancelClickListener;

    public interface OnItemClickListener {
        void onItemClick(AdopcionUsuario adopcion);
    }

    public interface OnCancelClickListener {
        void onCancelClick(AdopcionUsuario adopcion);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnCancelClickListener(OnCancelClickListener listener) {
        this.onCancelClickListener = listener;
    }

    public void setAdopciones(List<AdopcionUsuario> adopciones) {
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
        AdopcionUsuario adopcion = adopciones.get(position);
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

        public void bind(AdopcionUsuario adopcion) {
            tvNombreMascota.setText(adopcion.getNombreAnimal());
            tvRaza.setText(""); // No hay raza en el modelo actual, podría agregarse después
            tvEstado.setText(adopcion.getEstadoDisplayText());
            tvFecha.setText(formatFecha(adopcion.getFechaRelevante()));

            // Configurar colores según estado usando los colores del helper
            String colorHex = adopcion.getEstadoColor();
            int color = android.graphics.Color.parseColor(colorHex);
            
            tvEstado.setTextColor(color);
            viewStatusIndicator.setBackgroundColor(color);
            ivPaw.setColorFilter(color);

            // Mostrar botón cancelar solo para adopciones que se pueden cancelar
            String estado = adopcion.getEstado();
            boolean puedeCanlar = "SOLICITADA".equals(estado) || "EN_REVISION".equals(estado) || "EN_ESPERA".equals(estado);
            btnCancelar.setVisibility(puedeCanlar ? View.VISIBLE : View.GONE);

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
        
        private String formatFecha(String fechaStr) {
            if (fechaStr == null || fechaStr.isEmpty()) {
                return "";
            }
            
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                Date fecha = inputFormat.parse(fechaStr);
                return fecha != null ? outputFormat.format(fecha) : fechaStr;
            } catch (ParseException e) {
                // Si no se puede parsear, devolver la fecha original
                return fechaStr.split(" ")[0]; // Tomar solo la parte de fecha
            }
        }
    }
}