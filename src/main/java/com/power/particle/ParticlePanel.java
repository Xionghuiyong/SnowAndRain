package com.power.particle;

import com.intellij.ui.JBColor;
import com.intellij.util.ui.ImageUtil;
import com.intellij.util.ui.JBUI;
import com.power.colorful.ColorFactory;
import com.power.config.Config;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 粒子容器
 * <p>
 * Created by KADO on 15/12/15.
 */
public class ParticlePanel implements Runnable, Border {
    private static final int MAX_PARTICLE_COUNT = 500;

    private static ParticlePanel mParticlePanel;

    private int mParticleIndex = 0;
    private ConcurrentHashMap<String, ParticleView> mParticleViews = new ConcurrentHashMap<>();

    private JComponent mNowEditorJComponent;

    private int mParticleAreaWidth, mParticleAreaHeight;
    private BufferedImage mParticleAreaImage;
    private Graphics2D mParticleAreaGraphics;
    private Point mCaretPoint = new Point();
    private Point mParticleAreaSpeed = new Point();

    private Point mCurrentCaretPosition = null;

    private Thread mPThread;

    private boolean isEnable = false;

    private Config.State state = Config.getInstance().state;

    public static ParticlePanel getInstance() {
        if (mParticlePanel == null) {
            mParticlePanel = new ParticlePanel();
        }
        return mParticlePanel;
    }

    private ParticlePanel() {
    }

    @Override
    public void run() {
        while (isEnable) {
            if (mParticleAreaGraphics != null) {
                mParticleAreaGraphics.setBackground(new JBColor(new Color(0x00FFFFFF, true), new Color(0x00FFFFFF, true)));
                mParticleAreaGraphics.clearRect(0, 0, mParticleAreaWidth * 2, mParticleAreaHeight * 2);

                for (String key : mParticleViews.keySet()) {

                    ParticleView particleView = mParticleViews.get(key);
                    if (particleView != null && particleView.isEnable()) {
                        mParticleAreaGraphics.setColor(particleView.mColor);
                        mParticleAreaGraphics.fillOval((int) particleView.x, (int) particleView.y, state.PARTICLE_SIZE, state.PARTICLE_SIZE);
                        if (particleView.mAlpha <= 0.1) {
                            particleView.setEnable(false);
                            continue;
                        }
                        particleView.update();
                    }
                }
                if (mNowEditorJComponent != null) {
                    mNowEditorJComponent.repaint();
                }
            }
            try {
                Thread.sleep(12);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if (mParticleAreaImage == null) {
            return;
        }
        Graphics2D graphics2 = (Graphics2D) g;
        graphics2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        Point point = ParticlePositionCalculateUtil.getParticleAreaPositionOnEditorArea(mCaretPoint, mParticleAreaWidth, mParticleAreaHeight);
        graphics2.drawImage(mParticleAreaImage, point.x, point.y, c);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return JBUI.emptyInsets();
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }

    public void reset(JComponent jComponent) {
        clear();
        // 初始化jComponent
        if (mParticleViews == null) {
            mParticleIndex = 0;
            mParticleViews = new ConcurrentHashMap<>();
        }
        if (mPThread == null) {
            mPThread = new Thread(this);
        }

        if (mNowEditorJComponent != null) {
            mNowEditorJComponent.setBorder(null);
            mNowEditorJComponent = null;
        }

        mNowEditorJComponent = jComponent;
        // 更新图片背景
        mParticleAreaWidth = ParticlePositionCalculateUtil.getParticleAreaWidth(jComponent.getFont().getSize());
        mParticleAreaHeight = ParticlePositionCalculateUtil.getParticleAreaHeight(jComponent.getFont().getSize());

        mParticleAreaImage = ImageUtil.createImage(mParticleAreaWidth, mParticleAreaHeight, BufferedImage.TYPE_INT_BGR);
        mParticleAreaGraphics = mParticleAreaImage.createGraphics();
        /** 设置 透明窗体背景 */
        mParticleAreaImage = mParticleAreaGraphics.getDeviceConfiguration().createCompatibleImage(mParticleAreaWidth, mParticleAreaHeight, Transparency.TRANSLUCENT);
        mParticleAreaGraphics.dispose();
        mParticleAreaGraphics = mParticleAreaImage.createGraphics();
        /** 设置 透明窗体背景 END */

        this.isEnable = true;
        if (mParticleAreaImage != null && mParticleAreaGraphics != null && mNowEditorJComponent != null) {
            if (mPThread == null) {
                mPThread = new Thread(this);
            }
            mPThread.start();
        } else {
            this.isEnable = false;
            System.out.println("还没初始化 ParticlePanel");
        }
    }

    public void clear() {
        isEnable = false;

        if (mPThread != null) {
            mPThread.checkAccess();
            mPThread = null;
        }

        if (mNowEditorJComponent != null) {
            mNowEditorJComponent.setBorder(null);
            mNowEditorJComponent = null;
        }

        if (mParticleAreaGraphics != null) {
            mParticleAreaGraphics = null;
        }

        if (mParticleAreaImage != null) {
            mParticleAreaImage = null;
        }
    }

    public void destroy() {
        clear();

        if (mParticleViews != null) {
            mParticleViews.clear();
        }
        mParticleViews = null;
    }

    public void sparkAtPositionAction(int fontSize) {
        if (mCurrentCaretPosition != null) {
            mParticleAreaSpeed.setLocation(mCurrentCaretPosition.x - mCaretPoint.x, mCurrentCaretPosition.y - mCaretPoint.y);

            for (String key : mParticleViews.keySet()) {
                ParticleView particle = mParticleViews.get(key);
                particle.setX(particle.x - mParticleAreaSpeed.x);
                particle.setY(particle.y - mParticleAreaSpeed.y);
            }
            mCaretPoint = mCurrentCaretPosition;

            mParticleAreaWidth = ParticlePositionCalculateUtil.getParticleAreaWidth(fontSize);
            mParticleAreaHeight = ParticlePositionCalculateUtil.getParticleAreaHeight(fontSize);

            Point particlePoint = ParticlePositionCalculateUtil.getParticlePositionOnArea(mParticleAreaWidth, mParticleAreaHeight);

            int particleNumber = state.PARTICLE_MAX_COUNT;
            for (int i = 0; i < particleNumber; i++) {
                if (mParticleIndex >= MAX_PARTICLE_COUNT) {
                    mParticleViews.get(String.valueOf(mParticleIndex % MAX_PARTICLE_COUNT)).reset(particlePoint, ColorFactory.gen(), true);
                } else {
                    ParticleView particleView = new ParticleView(particlePoint, ColorFactory.gen(), true);
                    mParticleViews.put(String.valueOf(mParticleIndex), particleView);
                }

                if (mParticleIndex < MAX_PARTICLE_COUNT * 10) {
                    mParticleIndex++;
                } else {
                    mParticleIndex = MAX_PARTICLE_COUNT;
                }
            }
            mCurrentCaretPosition = null;
        }
    }

    public boolean isEnable() {
        return isEnable;
    }

    public JComponent getNowEditorJComponent() {
        return mNowEditorJComponent;
    }

    public void setCurrentCaretPosition() {
        mCurrentCaretPosition = new Point(1000,0);
    }
}
