package com.example.caadetalhes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
        Canvas canvas = new Canvas(imagem);

        imageView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN && palpites.size() < 5) {
                float x = event.getX();
                float y = event.getY();
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

                drawFeedback(canvas, x, y, acertou);
                imageView.invalidate();
                v.performClick();

                if (palpites.size() == 5) {
                    txtPontos.setText(String.valueOf(acertos));
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

    private void drawFeedback (Canvas canvas,float x, float y, boolean acerto){
        Paint paint = new Paint();
        paint.setColor(acerto ? Color.GREEN : Color.BLUE);
        canvas.drawCircle(x, y, 15, paint);
    }
}
