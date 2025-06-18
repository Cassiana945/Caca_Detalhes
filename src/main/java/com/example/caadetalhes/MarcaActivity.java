package com.example.caadetalhes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MarcaActivity extends AppCompatActivity {

    ArrayList<PointF> pontos = new ArrayList<>();
    Bitmap imagemOriginal, imagemComMarcadores;
    ImageView imagem, btnDesafiar;
    Canvas canvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marcar);

        imagem = findViewById(R.id.img);
        btnDesafiar = findViewById(R.id.desafio);

        imagemOriginal = ImageUtils.loadImage(getFilesDir() + "/image.jpg");
        imagemComMarcadores = imagemOriginal.copy(Bitmap.Config.ARGB_8888, true);
        canvas = new Canvas(imagemComMarcadores);

        imagem.setImageBitmap(imagemComMarcadores);

        imagem.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN && pontos.size() < 5) {

                float[] point = new float[]{event.getX(), event.getY()};
                ImageView imageView = (ImageView) v;
                Matrix inverseMatrix = new Matrix();
                imageView.getImageMatrix().invert(inverseMatrix);
                inverseMatrix.mapPoints(point);

                float x = point[0];
                float y = point[1];


                if (x >= 0 && x < imagemOriginal.getWidth() && y >= 0 && y < imagemOriginal.getHeight()) {
                    pontos.add(new PointF(x, y));
                    desenharX(x, y);
                    v.performClick();

                    if (pontos.size() == 5) {
                        ImageUtils.savePoints(pontos, getFilesDir() + "/pontos.json");
                        ImageUtils.saveImage(imagemComMarcadores, getFilesDir() + "/marcada.jpg");
                    }
                }
            }
            return true;
        });
        btnDesafiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MarcaActivity.this, DesafioActivity.class));
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
        imagem.invalidate();
    }
}
