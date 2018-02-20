package br.com.aidavec.aidavec.controls;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

import br.com.aidavec.aidavec.core.Globals;
import br.com.aidavec.aidavec.views.MainActivity;

/**
 * Created by leonardo.saganski on 19/01/17.
 */

public class Ponteiro extends ImageView {
    Path ponteiro;
    Paint paint;
    boolean primeiraVez;

    Thread thread;
    Handler handler = new Handler();

    float angulo;
    float angulo_inicial = 68;
    public float pontos = 0;

    boolean crescendo = true;

    public Ponteiro(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        paint = new Paint();
        ponteiro = new Path();

        paint.setColor(Color.parseColor("#3284BA"));
        paint.setAntiAlias(true);

        primeiraVez = true;
        angulo = angulo_inicial;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.save();

        super.onDraw(canvas);

        if(primeiraVez)
        {
            criaPath();
            primeiraVez = false;
        }

        canvas.rotate(angulo,this.getWidth()/2, this.getHeight()/2);

        canvas.drawCircle(this.getWidth()/2, this.getHeight()/2, 10, paint);
        canvas.drawPath(ponteiro, paint);

        canvas.restore();
    }

    public void desenhar(float a)
    {
        angulo = a;
        invalidate();
    }

    public void criaPath()
    {
        ponteiro = new Path();

        ponteiro.moveTo((this.getWidth()/2)-10, this.getHeight()/2);
        ponteiro.lineTo((this.getWidth()/2)+10, this.getHeight()/2);
        ponteiro.lineTo((this.getWidth()/2)+2, (this.getHeight()/14)*10);
        ponteiro.lineTo((this.getWidth()/2)-2, (this.getHeight()/14)*10);
        ponteiro.close();

    }

    //------------

    public void animatePonteiro() {
        pontos = (float) (2.24 * (pontos / 2));

        new Thread() {
            @Override
            public void run() {
                while(angulo < pontos+angulo_inicial && angulo < 293)  // angulo 293 equivale a 200 pontos
                {
                    angulo+=1.4;

                    ((MainActivity)Globals.getInstance().context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            desenhar(angulo);
                        }
                    });

                    try
                    {
                        Thread.sleep(25);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                float xxx = angulo;

            }
        }.start();
    }
}
