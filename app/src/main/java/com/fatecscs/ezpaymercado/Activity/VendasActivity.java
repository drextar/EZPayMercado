package com.fatecscs.ezpaymercado.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.fatecscs.ezpaymercado.Adapter.AdapterRemove;
import com.fatecscs.ezpaymercado.Adapter.AdapterVendas;
import com.fatecscs.ezpaymercado.Helper.ConfiguracaoFireBase;
import com.fatecscs.ezpaymercado.Model.Vendas;
import com.fatecscs.ezpaymercado.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VendasActivity extends AppCompatActivity {
    private RecyclerView rvVendas;
    private AdapterVendas adapterVendas;
    private List<Vendas> listaVendas;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendas);

        /*Configura toolbar*/
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Relatório de Vendas");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        iniciaComponentes();

        //Recycler View
        rvVendas.setHasFixedSize(true);
        rvVendas.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapterVendas = new AdapterVendas(listaVendas, getApplicationContext());
        rvVendas.setAdapter(adapterVendas);

        buscaVendas();
    }

    public void iniciaComponentes(){
        rvVendas = findViewById(R.id.rvVendas);
        listaVendas = new ArrayList<>();
    }

    public void buscaVendas(){
        listaVendas.clear();
        adapterVendas.notifyDataSetChanged();
        dbRef = ConfiguracaoFireBase.getFirebase().child("vendas");
        dbRef.addListenerForSingleValueEvent(valueEventListener);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            listaVendas.clear();
            if(dataSnapshot.exists()){
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Vendas venda = ds.getValue(Vendas.class);
                    listaVendas.add(venda);
                }
                adapterVendas.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}
