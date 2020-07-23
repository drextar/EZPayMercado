package com.fatecscs.ezpaymercado.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.fatecscs.ezpaymercado.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton adicionar, remover, editar, estoque, vendas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iniciaComponentes();

        /*Configura toolbar*/
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Inicio");
        setSupportActionBar(toolbar);

        adicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AdicionarProdutoActivity.class));
            }
        });
        remover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RemoveProduto.class));
            }
        });
        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), EditarProduto.class));
            }
        });
        estoque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), EstoqueActivity.class));
            }
        });
        vendas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), VendasActivity.class));
            }
        });
    }

    public void iniciaComponentes(){
        adicionar       = findViewById(R.id.fabAdicionar);
        remover         = findViewById(R.id.fabRemover);
        editar          = findViewById(R.id.fabEditar);
        estoque         = findViewById(R.id.fabEstoque);
        vendas          = findViewById(R.id.fabVendas);
    }
}
