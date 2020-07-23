package com.fatecscs.ezpaymercado.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fatecscs.ezpaymercado.Helper.ConfiguracaoFireBase;
import com.fatecscs.ezpaymercado.Model.Produto;
import com.fatecscs.ezpaymercado.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;

import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;

public class AdicionarProdutoActivity extends AppCompatActivity {
    private String [] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_FINE_LOCATION", "android.permission.READ_PHONE_STATE", "android.permission.SYSTEM_ALERT_WINDOW","android.permission.CAMERA"};
    private EditText nomeProduto, local, codigoBarras, preco, qtde;
    private ImageView imgProduto, imgCamera, imgGaleria, imgScan;
    private Button salvar;
    private Produto produto;
    private StorageReference storage;
    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;
    private Bitmap imagemFinal;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_produto);
        int requestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }

        /*Configura toolbar*/
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Adicionar Produtos");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        storage = ConfiguracaoFireBase.getFirebaseStorage();
        iniciaComponentes();

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

        imgScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan();
            }
        });
        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validaCampos();
            }
        });
    }

    public void scan(){
        IntentIntegrator intent = new IntentIntegrator(this);
        intent.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intent.setPrompt("CÓDIGO DE BARRAS");
        intent.setCameraId(0);
        intent.setBeepEnabled(false);
        intent.setBarcodeImageEnabled(false);
        intent.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        //scan requestCode = 49374
        if(resultCode == RESULT_OK){
            Bitmap imagem = null;
            try{
                switch(requestCode){
                    case 49374:
                        if(result != null){
                            if(result.getContents() != null){
                                codigoBarras.setText(result.getContents());
                            }
                        } else {
                            super.onActivityResult(requestCode,resultCode,data);
                        }
                        break;
                    case SELECAO_CAMERA:
                        imagem = (Bitmap) data.getExtras().get("data");
                        imagemFinal = imagem;
                        break;
                    case SELECAO_GALERIA:
                        Uri localImagem = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagem);
                        imagemFinal = imagem;
                        break;
                }
                if(imagem != null){
                    imgProduto.setImageBitmap(imagemFinal);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public void iniciaComponentes(){
        nomeProduto         = findViewById(R.id.txtNome);
        imgCamera           = findViewById(R.id.imgCamera);
        imgGaleria          = findViewById(R.id.imgGaleria);
        imgProduto          = findViewById(R.id.imgProduto);
        local               = findViewById(R.id.txtLocal);
        codigoBarras        = findViewById(R.id.txtCodigo);
        preco               = findViewById(R.id.txtPreco);
        qtde                = findViewById(R.id.txtQtde);
        salvar              = findViewById(R.id.btnAdicionar);
        imgScan             = findViewById(R.id.imgScan);
    }

    public void validaCampos(){
        if(!nomeProduto.getText().toString().equals("")){
            if(codigoBarras.getText().toString().length() < 13 || codigoBarras.getText().toString().length() > 13){
                Toast.makeText(this, "Código de barras inválido", Toast.LENGTH_SHORT).show();
            } else {
                if(!preco.getText().toString().equals("")){
                    if(!qtde.getText().toString().equals("")){
                        salvarImagem(codigoBarras.getText().toString());
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

    public void adicionarProduto(String url){
        String nomeA = nomeProduto.getText().toString().toUpperCase();
        String codigoA = codigoBarras.getText().toString();
        String precoA = preco.getText().toString();
        Integer qtdeA = Integer.parseInt(qtde.getText().toString());
        String localA = local.getText().toString();
        produto = new Produto(codigoA, nomeA, precoA, qtdeA);

        if(localA != null){
            produto.setLocal(localA);
        }

        if(url != "Null"){
            produto.setImgProduto(url);
        }
        produto.salvar(codigoA);
        Toast.makeText(this, "Produto adicionado com sucesso!", Toast.LENGTH_LONG).show();
        limparCampos();
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
                    Toast.makeText(AdicionarProdutoActivity.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
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
            adicionarProduto("Null");
        }
    }

    public void limparCampos(){
        nomeProduto.setText("");
        preco.setText("");
        local.setText("");
        codigoBarras.setText("");
        qtde.setText("");
        imgProduto.setImageResource(R.drawable.default_product);
    }
}
