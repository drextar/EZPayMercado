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
import com.fatecscs.ezpaymercado.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterEstoque extends RecyclerView.Adapter<AdapterEstoque.MyViewHolder>{
    private List<Produto> listaProduto;
    private Context context;

    public AdapterEstoque(List<Produto> l, Context c) {
        this.listaProduto = l;
        this.context = c;
    }

    @NonNull
    @Override
    public AdapterEstoque.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_estoque, parent, false);
        return new AdapterEstoque.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterEstoque.MyViewHolder holder, int position) {
        Produto produto = listaProduto.get(position);
        holder.nome.setText(produto.getNomeProduto());
        holder.qtde.setText("Qtde: " + produto.getQtde().toString());


        if(produto.getImgProduto() != null){
            Uri uri = Uri.parse(produto.getImgProduto());
            Glide.with(context).load(uri).into(holder.foto);
        } else {
            holder.foto.setImageResource(R.drawable.default_product);
        }

    }

    @Override
    public int getItemCount() {
        return listaProduto.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView foto;
        TextView nome, qtde;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            foto = itemView.findViewById(R.id.imgProduto);
            nome = itemView.findViewById(R.id.txtNome);
            qtde = itemView.findViewById(R.id.txtQtde);
        }
    }
}
