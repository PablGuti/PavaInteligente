package com.example.pavainteligente;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class DispositivosAdapter extends RecyclerView.Adapter<DispositivosAdapter.ViewHolder> {

    private final String[] datos;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(View view) {
            super(view);
             textView = (TextView) view.findViewById(R.id.itemTextView);
        }

        public TextView getTextView() {
            return textView;
        }
    }

    public DispositivosAdapter(String[] datos) {
        this.datos = datos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.dispositivo_item, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);

        view.setOnClickListener(v -> {
//          Toast toast = Toast.makeText(v.getContext(), viewHolder.getTextView().getText(), Toast.LENGTH_SHORT);
//          toast.show();
            Intent controlarPava = new Intent(v.getContext(), Control.class);
            controlarPava.putExtra("dispositivo", viewHolder.getTextView().getText());
            v.getContext().startActivity(controlarPava);
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.getTextView().setText(datos[i]);
    }

    @Override
    public int getItemCount() {
        return datos.length;
    }
}
