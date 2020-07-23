package com.fatecscs.ezpaymercado.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;

import com.fatecscs.ezpaymercado.Adapter.AdapterEstoque;
import com.fatecscs.ezpaymercado.Adapter.AdapterRemove;
import com.fatecscs.ezpaymercado.Helper.ConfiguracaoFireBase;
import com.fatecscs.ezpaymercado.Model.Produto;
import com.fatecscs.ezpaymercado.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EstoqueActivity extends AppCompatActivity {
    private DatabaseReference dbRef;
    private SearchView svEstoque;
    private RecyclerView rvEstoque;
    private List<Produto> listaProduto;
    private DatabaseReference produtosRef;
    private AdapterEstoque adapterEstoque;
    public static FloatingActionButton atualiza;
    public static View b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estoque);

        /*Configura toolbar*/
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Relatório de Estoque");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        iniciaComponentes();
        //Recycler View
        rvEstoque.setHasFixedSize(true);
        rvEstoque.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapterEstoque = new AdapterEstoque(listaProduto, getApplicationContext());
        rvEstoque.setAdapter(adapterEstoque);
        atualizaProdutos();
    }

    public void iniciaComponentes(){
        svEstoque = findViewById(R.id.svEstoque);
        rvEstoque = findViewById(R.id.rvEstoque);
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
        adapterEstoque.notifyDataSetChanged();
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
                adapterEstoque.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}
