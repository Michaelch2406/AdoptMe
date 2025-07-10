package com.mjc.adoptme.adapters;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
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
import com.google.android.material.chip.Chip;
import com.mjc.adoptme.AdoptMePortafolioActivity;
import com.mjc.adoptme.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.mjc.adoptme.models.Animal;

public class AnimalesPortafolioAdapter extends RecyclerView.Adapter<AnimalesPortafolioAdapter.AnimalViewHolder> {

    private List<Animal> animales = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private OnAdoptClickListener onAdoptClickListener;

    public interface OnItemClickListener {
        void onItemClick(Animal animal);
    }

    public interface OnAdoptClickListener {
        void onAdoptClick(Animal animal);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnAdoptClickListener(OnAdoptClickListener listener) {
        this.onAdoptClickListener = listener;
    }

    // Dentro de AnimalesPortafolioAdapter.java

    @SuppressLint("NotifyDataSetChanged")
    public void setAnimales(List<Animal> nuevosAnimales) {
        // No hagas this.animales = nuevosAnimales;
        // En su lugar, limpia la lista existente y añade los nuevos datos.
        // Esto asegura que el adaptador mantenga la misma referencia de lista.
        this.animales.clear();
        this.animales.addAll(nuevosAnimales);

        // ¡Esta es la línea más importante!
        // Notifica al RecyclerView que todo el conjunto de datos ha cambiado
        // y que necesita redibujar todo desde cero.
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AnimalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_animal_portafolio, parent, false);
        return new AnimalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalViewHolder holder, int position) {
        Animal animal = animales.get(position);
        holder.bind(animal);

        // Animación de entrada escalonada
        holder.itemView.setAlpha(0f);
        holder.itemView.setTranslationY(50f);

        AnimatorSet set = new AnimatorSet();
        ObjectAnimator alpha = ObjectAnimator.ofFloat(holder.itemView, "alpha", 0f, 1f);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(holder.itemView, "translationY", 50f, 0f);

        set.playTogether(alpha, translationY);
        set.setDuration(300);
        set.setStartDelay((position % 4) * 50L); // Retraso basado en columna
        set.setInterpolator(new DecelerateInterpolator());
        set.start();
    }

    @Override
    public int getItemCount() {
        return animales.size();
    }

    class AnimalViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ImageView ivAnimal;
        private TextView tvNombre;
        private TextView tvEdad;
        private TextView tvUbicacion;
        private TextView tvDistancia;
        private Chip chipTipo;
        private MaterialButton btnAdoptar;
        private View viewGradient;

        public AnimalViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardAnimal);
            ivAnimal = itemView.findViewById(R.id.ivAnimal);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvEdad = itemView.findViewById(R.id.tvEdad);
            tvUbicacion = itemView.findViewById(R.id.tvUbicacion);
            tvDistancia = itemView.findViewById(R.id.tvDistancia);
            chipTipo = itemView.findViewById(R.id.chipTipo);
            btnAdoptar = itemView.findViewById(R.id.btnAdoptar);
            viewGradient = itemView.findViewById(R.id.viewGradient);
        }

        // Dentro de la clase AnimalViewHolder en AnimalesPortafolioAdapter.java
        public void bind(Animal animal) {
            // --- Configurar datos en las vistas ---
            tvNombre.setText(animal.getNombre());
            tvEdad.setText(animal.getEdadTexto()); // Usamos el método helper que ya tienes
            tvUbicacion.setText(animal.getUbicacionFundacion());

            // Mostrar u ocultar la distancia
            if (animal.getDistancia() > 0) {
                tvDistancia.setText(String.format(Locale.getDefault(), "A %.1f km de ti", animal.getDistancia()));
                tvDistancia.setVisibility(View.VISIBLE);
            } else {
                tvDistancia.setVisibility(View.GONE);
            }

            // --- Lógica visual para el tipo de animal ---
            if ("PERRO".equals(animal.getTipoAnimalNombre())) {
                chipTipo.setText("🐕");
                chipTipo.setChipBackgroundColorResource(R.color.colorAccent1); // Asegúrate de que este color exista en colors.xml
                // Aquí podrías usar una librería como Glide o Picasso para cargar la imagen real
                ivAnimal.setImageResource(R.drawable.ic_dog); // Placeholder
            } else if ("GATO".equals(animal.getTipoAnimalNombre())) {
                chipTipo.setText("🐈");
                chipTipo.setChipBackgroundColorResource(R.color.colorAccent2); // Asegúrate de que este color exista en colors.xml
                ivAnimal.setImageResource(R.drawable.ic_cat); // Placeholder
            } else {
                chipTipo.setText("🐾");
                chipTipo.setChipBackgroundColorResource(R.color.colorAccent3); // Asegúrate de que este color exista en colors.xml
                ivAnimal.setImageResource(R.drawable.ic_pets); // Placeholder genérico
            }

            // --- Configurar los Click Listeners ---
            // El listener para toda la tarjeta
            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(animal);
                }
            });

            // El listener específico para el botón de adoptar
            btnAdoptar.setOnClickListener(v -> {
                if (onAdoptClickListener != null) {
                    onAdoptClickListener.onAdoptClick(animal);
                }
            });
        }
    }
}