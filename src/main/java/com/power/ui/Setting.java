package com.power.ui;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.Comparing;
import com.power.config.Config;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import javax.swing.*;

public class Setting implements Configurable {

    private JPanel rootPanel;
    private JSlider particleSizeSlider;
    private JSlider particleMaxCountSlider;
    private JSlider snowAndRainSlider;
    private JLabel maxCountValue;
    private JLabel sizeValue;
    private JLabel snowValue;

    private Config.State state = Config.getInstance().state;


    @Nls
    @Override
    public String getDisplayName() {
        return "Snow And Rain";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        initListener();
        initSetting();
        return this.rootPanel;
    }

    @Override
    public boolean isModified() {
        try {
            return !Comparing.equal(state.PARTICLE_MAX_COUNT, particleMaxCountSlider.getValue()) ||
                    !Comparing.equal(state.PARTICLE_SIZE, particleSizeSlider.getValue()) ||
                    !Comparing.equal(state.SNOW_AND_RAIN, snowAndRainSlider.getValue());
        } catch (NumberFormatException ex) {
            return true;
        }
    }

    @Override
    public void apply() {
        state.PARTICLE_MAX_COUNT = particleMaxCountSlider.getValue();
        state.PARTICLE_SIZE = particleSizeSlider.getValue();
        state.SNOW_AND_RAIN = snowAndRainSlider.getValue();
    }

    @Override
    public void reset() {
        this.initSetting();
    }

    private void initListener() {
        particleSizeSlider.addChangeListener(event -> {
            JSlider source = (JSlider) event.getSource();
            sizeValue.setText(String.valueOf(source.getValue()));
        });
        particleMaxCountSlider.addChangeListener(event -> {
            JSlider source = (JSlider) event.getSource();
            maxCountValue.setText(String.valueOf(source.getValue()));
        });
        snowAndRainSlider.addChangeListener(event -> {
            JSlider source = (JSlider) event.getSource();
            int value = source.getValue();
            snowValue.setText(value+"% / "+(100-value)+"%");
        });
    }

    private void initSetting() {
        particleMaxCountSlider.setValue(state.PARTICLE_MAX_COUNT);
        maxCountValue.setText(String.valueOf(state.PARTICLE_MAX_COUNT));
        particleSizeSlider.setValue(state.PARTICLE_SIZE);
        sizeValue.setText(String.valueOf(state.PARTICLE_SIZE));
        snowAndRainSlider.setValue(state.SNOW_AND_RAIN);
        snowValue.setText(state.SNOW_AND_RAIN+"% / "+(100-state.SNOW_AND_RAIN)+"%");
    }
}
