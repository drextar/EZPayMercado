package com.fatecscs.ezpaymercado.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.fatecscs.ezpaymercado.Adapter.AdapterEdita;
import com.fatecscs.ezpaymercado.Adapter.AdapterRemove;
import com.fatecscs.ezpaymercado.Helper.ConfiguracaoFireBase;
import com.fatecscs.ezpaymercado.Model.Produto;
import com.fatecscs.ezpaymercado.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;

public class EditarProduto extends AppCompatActivity {
    private String [] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_FINE_LOCATION", "android.permission.READ_PHONE_STATE", "android.permission.SYSTEM_ALERT_WINDOW","android.permission.CAMERA"};
    private DatabaseReference dbRef;
    private SearchView svPesquisa;
    private RecyclerView rvPesquisa;
    private List<Produto> listaProduto;
    private DatabaseReference produtosRef;
    private AdapterEdita adapterEdita;
    public static TextView txtNome, txtCodigo, txtPreco, txtQtde;
    public static ImageView imgProduto;
    public static Button btnAtt;
    public static CircleImageView fotoAtualiza;
    public static Bitmap imagemFinal;
    public static StorageReference storage;
    public static String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_produto);

        iniciaComponentes();

        //Recycler View
        rvPesquisa.setHasFixedSize(true);
        rvPesquisa.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapterEdita = new AdapterEdita(listaProduto, getApplicationContext());
        rvPesquisa.setAdapter(adapterEdita);

        storage = ConfiguracaoFireBase.getFirebaseStorage();


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
    }

    public void iniciaComponentes(){
        svPesquisa = findViewById(R.id.svPesquisa);
        rvPesquisa = findViewById(R.id.rvPesquisa);
        listaProduto = new ArrayList<>();
        produtosRef = ConfiguracaoFireBase.getFirebase().child("produtos");
    }

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
                    adapterEdita.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
