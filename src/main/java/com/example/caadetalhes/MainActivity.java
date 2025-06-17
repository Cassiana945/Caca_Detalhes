package com.example.caadetalhes;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;


public class MainActivity extends AppCompatActivity {

    ImageView img;
    ImageView btnTirarFoto, btnDesafiar;
    MediaPlayer playerErro, playerAcerto;
    Uri imagemUri;
    private int currentRequestCode = 0;
    ActivityResultLauncher<Intent> activityResultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        img = findViewById(R.id.img);
        btnTirarFoto = findViewById(R.id.tirar_foto);
        btnDesafiar = findViewById(R.id.desafiar);

        playerErro = MediaPlayer.create(this, R.raw.erro);
        playerAcerto = MediaPlayer.create(this, R.raw.acerto);

        btnDesafiar.setEnabled(false);


        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        img.setImageURI(imagemUri);
                    }

                    btnDesafiar.setEnabled(true);

                });


        btnTirarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File foto = new File(getExternalFilesDir(null), "temp_camera.jpg");
                imagemUri = FileProvider.getUriForFile(MainActivity.this, getPackageName() + ".fileprovider", foto);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imagemUri);
                cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                currentRequestCode = 1;
                activityResultLauncher.launch(cameraIntent);

            }
        });

        verificarPermissaoInicial();
        requestPermission();
        checkPermission();

        btnDesafiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



    }

    private void verificarPermissaoInicial() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Toast.makeText(this, "Permissão não é necessária nesta versão do Android!", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, 1);
        }
    }


    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return true;
        }
        int writeCheck = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        return writeCheck == PackageManager.PERMISSION_GRANTED;
    }

}