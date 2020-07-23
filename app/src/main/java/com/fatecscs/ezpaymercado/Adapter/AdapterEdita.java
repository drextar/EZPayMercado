package com.fatecscs.ezpaymercado.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fatecscs.ezpaymercado.Activity.EditarProduto;
import com.fatecscs.ezpaymercado.Activity.TesteEditaActivity;
import com.fatecscs.ezpaymercado.Model.Produto;
import com.fatecscs.ezpaymercado.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterEdita extends RecyclerView.Adapter<AdapterEdita.MyViewHolder> {
    private List<Produto> listaProduto;
    private Context context;

    public AdapterEdita(List<Produto> l, Context c){
        this.listaProduto = l;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_edita_produto, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Produto produto = listaProduto.get(position);

        if(produto.getImgProduto() != null){
            Uri uri = Uri.parse(produto.getImgProduto());
            Glide.with(context).load(uri).into(holder.foto);
        } else {
            holder.foto.setImageResource(R.drawable.default_product);
        }
        holder.nome.setText(produto.getNomeProduto());
        holder.codigoBarra.setText(produto.getCodigoBarras());
        holder.editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), TesteEditaActivity.class);
                intent.putExtra("EDITAR_PRODUTO", produto);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaProduto.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView foto;
        TextView nome, codigoBarra;
        FloatingActionButton editar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            foto = itemView.findViewById(R.id.imgFoto);
            nome = itemView.findViewById(R.id.txtNome);
            codigoBarra = itemView.findViewById(R.id.txtCodigo);
            editar = itemView.findViewById(R.id.fabEdita);
        }
    }
}
