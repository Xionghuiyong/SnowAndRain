package com.power.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * 配置文件
 * Created by KADO on 15/12/17.
 */
@State(
        name = "activate-com.power-mode",
        storages = {
                @Storage(
                        value = "$APP_CONFIG$/activate-com.power-mode_setting.xml"
                )
        }
)
public class Config implements PersistentStateComponent<Config.State> {

    public static final String DEFAULT = "default";

    @Nullable
    @Override
    public State getState() {
        return this.state;
    }

    @Override
    public void loadState(State state) {
        XmlSerializerUtil.copyBean(state, this.state);
    }

    public State state = new State();

    public Config() {
        defaultInitState();
    }

    public void defaultInitState() {

        state.IS_ENABLE = true;

        state.IS_SPARK = true;

        state.IS_COLORFUL = true;

        state.SNOW_AND_RAIN = 70;

        state.PARTICLE_MAX_COUNT = 20;

        state.PARTICLE_COLOR = null;

        state.PARTICLE_SIZE = 3;
    }

    public static Config getInstance() {
        return ServiceManager.getService(Config.class);
    }

    public static final class State {

        /**
         * 是否开启
         */
        public boolean IS_ENABLE = true;

        /**
         * 是否显示火花
         */
        public boolean IS_SPARK = true;

        /**
         * 色彩鲜艳的配置项
         */
        public boolean IS_COLORFUL = true;

        /**
         * 每次生成的粒子量
         */
        public int PARTICLE_MAX_COUNT = 20;
        /**
         * 雨占所有粒子数的比例
         */
        public int SNOW_AND_RAIN = 70;

        /**
         * 粒子颜色,为null则代表auto
         */
        public Color PARTICLE_COLOR = null;

        /**
         * 粒子大小
         */
        public int PARTICLE_SIZE = 3;

    }


}
