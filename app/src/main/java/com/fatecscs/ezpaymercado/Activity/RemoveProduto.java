package com.fatecscs.ezpaymercado.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import com.fatecscs.ezpaymercado.Adapter.AdapterRemove;
import com.fatecscs.ezpaymercado.Helper.ConfiguracaoFireBase;
import com.fatecscs.ezpaymercado.Model.Produto;
import com.fatecscs.ezpaymercado.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RemoveProduto extends AppCompatActivity {
    private DatabaseReference dbRef;
    private SearchView svPesquisa;
    private RecyclerView rvPesquisa;
    private List<Produto> listaProduto;
    private DatabaseReference produtosRef;
    private AdapterRemove adapterRemove;
    public static FloatingActionButton atualiza;
    public static View b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_produto);
        iniciaComponentes();

        //Recycler View
        rvPesquisa.setHasFixedSize(true);
        rvPesquisa.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapterRemove = new AdapterRemove(listaProduto, getApplicationContext());
        rvPesquisa.setAdapter(adapterRemove);

        //SearchView
        svPesquisa.setQueryHint("Buscar Produtos");
        svPesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String textoDigitado = newText.toUpperCase();
                pesquisarProduto(textoDigitado);
                return true;
            }
        });
        atualizaProdutos();
    }

    public void iniciaComponentes(){
        svPesquisa = findViewById(R.id.svPesquisa);
        rvPesquisa = findViewById(R.id.rvPesquisa);
        listaProduto = new ArrayList<>();
        produtosRef = ConfiguracaoFireBase.getFirebase().child("produtos");
        atualiza = findViewById(R.id.fabAtualiza);
        b = findViewById(R.id.fabAtualiza);
        b.setVisibility(View.GONE);
        atualiza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atualizaProdutos();
            }
        });
    }

    public void atualizaProdutos(){
        listaProduto.clear();
        adapterRemove.notifyDataSetChanged();
        dbRef = ConfiguracaoFireBase.getFirebase().child("produtos");
        dbRef.addListenerForSingleValueEvent(valueEventListener);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            listaProduto.clear();
            if(dataSnapshot.exists()){
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Produto produto = ds.getValue(Produto.class);
                    listaProduto.add(produto);
                }
                adapterRemove.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public void pesquisarProduto(String texto){
        listaProduto.clear();
        if(texto.length() > 0){
            Query query = produtosRef.orderByChild("nomeProduto").startAt(texto).endAt(texto + "\uf8ff");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    listaProduto.clear();
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        listaProduto.add(ds.getValue(Produto.class));
                    }
                    adapterRemove.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
