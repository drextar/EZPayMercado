package com.fatecscs.ezpaymercado.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fatecscs.ezpaymercado.Helper.ConfiguracaoFireBase;
import com.fatecscs.ezpaymercado.Model.Produto;
import com.fatecscs.ezpaymercado.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;

import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;

public class TesteEditaActivity extends AppCompatActivity {
    private String [] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_FINE_LOCATION", "android.permission.READ_PHONE_STATE", "android.permission.SYSTEM_ALERT_WINDOW","android.permission.CAMERA"};
    private TextView nome, preco, qtde, codigo;
    private ImageView imagem, imgCamera, imgGaleria;
    private Button salvar;
    private StorageReference storage;
    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;
    private Bitmap imagemFinal;
    private String url, imgBanco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teste_edita);
        int requestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
        /*Configura toolbar*/
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Atualizar Produto");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        storage = ConfiguracaoFireBase.getFirebaseStorage();
        iniciaCampos();
        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(it.resolveActivity(getPackageManager())!= null){
                    startActivityForResult(it, SELECAO_CAMERA);
                }
            }
        });

        imgGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });

        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validaCampos();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        //scan requestCode = 49374
        if (resultCode == RESULT_OK) {
            Bitmap imagemR = null;
            try {
                switch (requestCode) {
                    case SELECAO_CAMERA:
                        imagemR = (Bitmap) data.getExtras().get("data");
                        imagemFinal = imagemR;
                        break;
                    case SELECAO_GALERIA:
                        Uri localImagem = data.getData();
                        imagemR = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagem);
                        imagemFinal = imagemR;
                        break;
                }
                if (imagemR != null) {
                    imagem.setImageBitmap(imagemFinal);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void iniciaCampos(){
        Intent i = getIntent();
        Produto produto = (Produto) i.getSerializableExtra("EDITAR_PRODUTO");
        imgCamera           = findViewById(R.id.imgCamera);
        imgGaleria          = findViewById(R.id.imgGaleria);
        nome                = findViewById(R.id.txtNome);
        preco               = findViewById(R.id.txtPreco);
        qtde                = findViewById(R.id.txtQtde);
        codigo              = findViewById(R.id.txtCodigo);
        imagem              = findViewById(R.id.imgProduto);
        salvar              = findViewById(R.id.btnAdicionar);
        imgBanco            = produto.getImgProduto();

        nome.setText(produto.getNomeProduto());
        preco.setText(produto.getPreco());
        qtde.setText(String.valueOf(produto.getQtde()));
        codigo.setText(produto.getCodigoBarras());
        if(produto.getImgProduto() != null){
            Uri uri = Uri.parse(produto.getImgProduto());
            Glide.with(getApplicationContext()).load(uri).into(imagem);
        } else {
            imagem.setImageResource(R.drawable.default_product);
        }
    }

    public void validaCampos(){
        if(!nome.getText().toString().equals("")){
            if(codigo.getText().toString().length() < 13 || codigo.getText().toString().length() > 13){
                Toast.makeText(this, "Código de barras inválido", Toast.LENGTH_SHORT).show();
            } else {
                if(!preco.getText().toString().equals("")){
                    if(!qtde.getText().toString().equals("")){
                        salvarImagem(codigo.getText().toString());
                    } else {
                        Toast.makeText(this, "Preencha a quantidade total do produto", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Preencha o campo de preço", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "Preencha o nome do produto", Toast.LENGTH_SHORT).show();
        }
    }

    public void salvarImagem(String codigo){
        if(imagemFinal != null){
            //recupera dados da imagem para o firebase
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imagemFinal.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] dadosImagem = baos.toByteArray();

            //salva no firebase
            StorageReference imagemRef = storage.child("imagens").child("produtos").child(codigo);
            final UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(TesteEditaActivity.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            adicionarProduto(uri.toString());
                        }
                    });
                }
            });
        } else {
            adicionarProduto(imgBanco);
        }
    }

    public void adicionarProduto(String url){
        String nomeA = nome.getText().toString().toUpperCase();
        String codigoA = codigo.getText().toString();
        String precoA = preco.getText().toString();
        Integer qtdeA = Integer.parseInt(qtde.getText().toString());
        Produto atualizado = new Produto(codigoA, nomeA, precoA, qtdeA);
        atualizado.setImgProduto(url);
        atualizado.salvar(codigoA);
        Toast.makeText(this, "Produto atualizado com sucesso!", Toast.LENGTH_LONG).show();
        limparCampos();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        }, 1000);
    }

    public void limparCampos(){
        nome.setText("");
        preco.setText("");
        codigo.setText("");
        qtde.setText("");
        imagem.setImageResource(R.drawable.default_product);
    }
}
