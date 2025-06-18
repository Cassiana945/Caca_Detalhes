package com.example.caadetalhes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class DesafioActivity extends AppCompatActivity {

    ArrayList<PointF> pontosOriginais;
    ArrayList<PointF> palpites = new ArrayList<>();
    Bitmap imagem;
    ImageView imageView, btnTirarFoto;
    TextView txtPontos;
    int acertos = 0;
    Canvas canvas;
    MediaPlayer playerErro, playerAcerto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desafio);

        imageView = findViewById(R.id.img);
        btnTirarFoto = findViewById(R.id.tirar_foto);
        txtPontos = findViewById(R.id.pontos);

        playerErro = MediaPlayer.create(this, R.raw.erro);
        playerAcerto = MediaPlayer.create(this, R.raw.acerto);

        imagem = ImageUtils.loadImage(getFilesDir() + "/image.jpg").copy(Bitmap.Config.ARGB_8888, true);
        imageView.setImageBitmap(imagem);
        pontosOriginais = ImageUtils.loadPoints(getFilesDir() + "/pontos.json");
        canvas = new Canvas(imagem);

        imageView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN && palpites.size() < 5) {

                float[] point = new float[]{event.getX(), event.getY()};
                ImageView imageView = (ImageView) v;
                Matrix inverseMatrix = new Matrix();
                imageView.getImageMatrix().invert(inverseMatrix);
                inverseMatrix.mapPoints(point);

                float x = point[0];
                float y = point[1];


                if (x >= 0 && x < imagem.getWidth() && y >= 0 && y < imagem.getHeight()) {
                    palpites.add(new PointF(x, y));

                    boolean acertou = false;
                    for (PointF p : pontosOriginais) {
                        if (Math.hypot(p.x - x, p.y - y) <= 50) {
                            acertou = true;
                            playerAcerto.start();
                            acertos++;
                            break;
                        }
                    }

                    if (!acertou) {
                        playerErro.start();
                    }

                    desenharX(x, y);
                    imageView.invalidate();
                    v.performClick();

                    if (palpites.size() == 5) {
                        txtPontos.setText(String.valueOf(acertos));
                    }
                }
            }
            return true;
        });
        btnTirarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DesafioActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

    }

    private void desenharX(float x, float y) {
        Bitmap originalX = BitmapFactory.decodeResource(getResources(), R.drawable.x);
        int novaLargura = 30;
        int novaAltura = 30;
        Bitmap marcadorBitmap = Bitmap.createScaledBitmap(originalX, novaLargura, novaAltura, true);

        float marcaX = x - marcadorBitmap.getWidth() / 2f;
        float marcaY = y - marcadorBitmap.getHeight() / 2f;
        canvas.drawBitmap(marcadorBitmap, marcaX, marcaY, null);

    }
}
