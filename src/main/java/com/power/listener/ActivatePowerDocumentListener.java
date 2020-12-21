package com.power.listener;


import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.power.ActivatePowerModeManage;
import com.power.config.Config;
import com.power.particle.ParticlePanel;
import java.util.ArrayList;

/**
 * 震动文本监听接口
 * Created by suika on 15-12-13.
 */
public class ActivatePowerDocumentListener implements DocumentListener {

    private Config.State state = Config.getInstance().state;

    private Project mProject;
    private ArrayList<Document> mDocumentList = new ArrayList<>();

    private Editor mEditor;

    public ActivatePowerDocumentListener(Project project) {
        mProject = project;
    }

    @Override
    public void beforeDocumentChange(DocumentEvent documentEvent) {

    }

    @Override
    public void documentChanged(DocumentEvent documentEvent) {
        ActivatePowerModeManage manage = ActivatePowerModeManage.getInstance();

        if ( mProject != null) {
            // 处理ActivatePower效果
            Editor selectedTextEditor = FileEditorManager.getInstance(mProject).getSelectedTextEditor();
            if (mEditor == null || mEditor != selectedTextEditor) {
                mEditor = selectedTextEditor;
            }
            if (mEditor != null) {
                manage.resetEditor(mEditor);
                if (state.IS_SPARK) {
                    int fontSize = mEditor.getColorsScheme().getEditorFontSize();
                    ParticlePanel particlePanel = ParticlePanel.getInstance();
                    if (particlePanel.isEnable()) {
                        particlePanel.sparkAtPositionAction(fontSize);
                    }
                }
            }
        }

        Editor selectedTextEditor = FileEditorManager.getInstance(mProject).getSelectedTextEditor();
        if (mEditor == null || mEditor != selectedTextEditor)
            mEditor = selectedTextEditor;

        if (mEditor != null) {
            try {
                ParticlePanel.getInstance().setCurrentCaretPosition();
            } catch (Exception ignored) {

            }
        }
    }

    public boolean addDocument(Document document) {
        if (!mDocumentList.contains(document)) {
            mDocumentList.add(document);
            return true;
        } else {
            return false;
        }
    }

    public void clean(Document document, boolean isRemoveInList) {
        mEditor = null;
        if (document != null && mDocumentList.contains(document)) {
            document.removeDocumentListener(this);
            if (isRemoveInList)
                mDocumentList.remove(document);
        }
    }

    public void destroy() {
        if (mDocumentList != null) {
            for (Document document : mDocumentList)
                clean(document, false);
            mDocumentList.clear();
        }
        mProject = null;
    }

    /**
     *
     */

}
