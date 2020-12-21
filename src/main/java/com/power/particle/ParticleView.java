package com.power.particle;

import com.intellij.ui.JBColor;
import com.power.config.Config;

import java.awt.*;
import java.awt.color.ColorSpace;

/**
 * 粒子类13152315
 * <p>
 * Created by KADO on 15/12/15.
 */
class ParticleView {
    public Color mColor;
    public float mAlpha;
    public float alpha;
    public float x, y;
    public float vX, vY;
    private double isRain;
    private Config config;

    public Point mPoint;

    private boolean isEnable = false;

    public ParticleView(Point point, Color color, boolean isEnable) {
        this.config = Config.getInstance();
        init(point, color, isEnable);
    }

    public void init(Point point, Color color, boolean isEnable) {
        this.isEnable = isEnable;
        this.mPoint = point;
        this.mColor = color;
        this.mAlpha = (float) (Math.random() / 100+1.09);
        this.alpha = (float) (Math.random() / 100+0.99);
        this.isRain = Math.random()*100;

        x = (float) ((Math.round(Math.random() * 40) - 34)*50 + this.mPoint.getX());
        y = (float) this.mPoint.getY() - Math.round(Math.random() * 150)+20;
        vY = (float) (((Math.round(Math.random() * 100)) / 100.0)+0.8);
        vX = (float)( Math.random()*2.5-0.6);
    }

    public void reset(Point point, Color color, boolean isEnable) {
        init(point, color, isEnable);
    }

    public void update() {
        this.mAlpha *= alpha;
        if (isRain >= config.state.SNOW_AND_RAIN){
            x += 0.1;
            vY +=0.07;
            y +=vY;
            mColor = JBColor.WHITE;
        }else {
            x = x + vX;
            y = y + vY;
        }

        ColorSpace colorSpace = mColor.getColorSpace();
        float[] floats = mColor.getColorComponents(null);
        float newAlpha = mAlpha + 0.3f;
        if (newAlpha > 1.0f) {
            newAlpha = 1.0f;
        } else if (newAlpha < 0.0f) {
            newAlpha = 0.0f;
        }
        mColor = new JBColor(new Color(colorSpace, floats, newAlpha), new Color(colorSpace, floats, newAlpha));
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }
}
