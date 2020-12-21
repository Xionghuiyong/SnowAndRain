package com.power;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBusConnection;
import com.power.config.Config;
import com.power.listener.ActivatePowerDocumentListener;
import com.power.particle.ParticlePanel;
import org.jetbrains.annotations.NotNull;
import javax.swing.*;
import java.util.Timer;
import java.util.*;

/**
 * 效果管理器
 * @author by KADO on 2017/1/11.
 */
public class ActivatePowerModeManage {

    private static ActivatePowerModeManage sActivatePowerModeManage;

    public static ActivatePowerModeManage getInstance() {
        if (sActivatePowerModeManage == null) {
            sActivatePowerModeManage = new ActivatePowerModeManage();
        }
        return sActivatePowerModeManage;
    }
    private Config.State state = Config.getInstance().state;

    private HashMap<Project, ActivatePowerDocumentListener> mDocListenerMap = new HashMap<>();
    private Map<String, MessageBusConnection> activeConnections = new HashMap<>();
    private Editor mCurrentEditor;

    public void init(Project project) {

        if (project != null) {
            // 监听FileEditor的状态
            MessageBusConnection connection = project.getMessageBus().connect();
            this.activeConnections.put(project.getBasePath(), connection);
            connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerListener() {
                @Override
                public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
                    destroyParticle();
                    mCurrentEditor = null;

                    initDocument(source.getProject(), FileDocumentManager.getInstance().getDocument(file));
                }

                @Override
                public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
                    ActivatePowerDocumentListener activatePowerDocumentListener = mDocListenerMap.get(source.getProject());
                    if (activatePowerDocumentListener != null) {
                        activatePowerDocumentListener.clean(FileDocumentManager.getInstance().getDocument(file), true);
                    }
                }

                @Override
                public void selectionChanged(@NotNull FileEditorManagerEvent event) {
                    if (state.IS_ENABLE) {
                        destroyParticle();
                        mCurrentEditor = null;

                        FileEditorManager fileEditorManager = event.getManager();
                        VirtualFile virtualFile = event.getNewFile();
                        if (virtualFile != null) {
                            initDocument(fileEditorManager.getProject(), FileDocumentManager.getInstance().getDocument(virtualFile));
                        }
                    }
                }
            });

            FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);

            if (fileEditorManager != null) {
                Editor editor = fileEditorManager.getSelectedTextEditor();
                if (editor != null) {
                    destroyParticle();
                    mCurrentEditor = null;
                    initDocument(project, editor.getDocument());
                }
            }
        } else {
            System.out.println("ActivatePowerEnableAction " + "初始化数据失败");
        }
    }

    private void initDocument(Project project, Document document) {
        if (project != null && document != null) {
            ActivatePowerDocumentListener activatePowerDocumentListener = mDocListenerMap.get(project);
            if (activatePowerDocumentListener == null) {
                activatePowerDocumentListener = new ActivatePowerDocumentListener(project);
                mDocListenerMap.put(project, activatePowerDocumentListener);
            }
            if (activatePowerDocumentListener.addDocument(document)) {
                document.addDocumentListener(activatePowerDocumentListener);
            }
       }
    }

    public void destroy(Project project, boolean isRemoveProject) {
        destroyParticle();
        destroyDocumentListener(project, isRemoveProject);
        mCurrentEditor = null;
        if (project != null && !project.isDisposed()) {
            Optional.ofNullable(this.activeConnections.remove(project.getBasePath()))
                    .ifPresent(MessageBusConnection::disconnect);
        }
        if (isRemoveProject) {
            mDocListenerMap.remove(project);
        }
    }

    public void destroyAll() {
        for (Project project : mDocListenerMap.keySet()) {
            destroy(project, false);
        }
        mDocListenerMap.clear();
    }

    public void resetEditor(Editor editor) {
        if (mCurrentEditor != editor) {
            mCurrentEditor = editor;
            if (mCurrentEditor != null) {
                JComponent jContentComponent = editor.getContentComponent();
                if (state.IS_SPARK && ParticlePanel.getInstance().getNowEditorJComponent() != jContentComponent) {
                    ParticlePanel.getInstance().reset(jContentComponent);
                    jContentComponent.setBorder(ParticlePanel.getInstance());
                }
            }
        }
    }

    private void destroyDocumentListener(Project project, boolean isRemoveProject) {
        ActivatePowerDocumentListener activatePowerDocumentListener = mDocListenerMap.get(project);
        if (activatePowerDocumentListener != null) {
            activatePowerDocumentListener.destroy();
            if (isRemoveProject) {
                mDocListenerMap.remove(project);
            }
        }
    }

    private void destroyParticle() {
        ParticlePanel.getInstance().destroy();
    }
}
