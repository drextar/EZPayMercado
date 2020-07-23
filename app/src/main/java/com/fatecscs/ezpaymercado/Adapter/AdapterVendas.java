package com.fatecscs.ezpaymercado.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fatecscs.ezpaymercado.Activity.RemoveProduto;
import com.fatecscs.ezpaymercado.Model.Produto;
import com.fatecscs.ezpaymercado.Model.Vendas;
import com.fatecscs.ezpaymercado.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterVendas extends RecyclerView.Adapter<AdapterVendas.MyViewHolder>{
    private List<Vendas> listaVenda;
    private Context context;

    public AdapterVendas(List<Vendas> l, Context c) {
        this.listaVenda = l;
        this.context = c;
    }

    @NonNull
    @Override
    public AdapterVendas.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_vendas, parent, false);
        return new AdapterVendas.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterVendas.MyViewHolder holder, int position) {
        Vendas venda = listaVenda.get(position);
        holder.id.setText("ID Compra: " + venda.getIdCompra().toString());
        holder.idUsuario.setText("ID Usuário: " + venda.getId());
        holder.data.setText("Data: " + venda.getData());
        holder.total.setText("Total: R$ " + venda.getTotal());
    }

    @Override
    public int getItemCount() {
        return listaVenda.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView id, idUsuario, total, data;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.txtIdCompra);
            idUsuario = itemView.findViewById(R.id.txtIdUser);
            total = itemView.findViewById(R.id.txtTotal);
            data = itemView.findViewById(R.id.txtData);
        }
    }
}
