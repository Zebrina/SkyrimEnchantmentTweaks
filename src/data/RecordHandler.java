/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.util.HashMap;
import skyproc.FormID;
import skyproc.MajorRecord;
import skyproc.Mod;

/**
 *
 * @author Sabrina
 */
public class RecordHandler {
    private static RecordHandler instance = null;
    
    public static RecordHandler inst() {
        if (instance == null) {
            instance = new RecordHandler();
        }
        return instance;
    }
    
    private Mod merger = null;
    private Mod patch = null;
    private final HashMap<String, MajorRecord> editorIDCache = new HashMap<>();
    private final HashMap<FormID, MajorRecord> formIDCache = new HashMap<>();

    public void initialize(Mod merger, Mod patch) {
        this.merger = merger;
        this.patch = patch;
    }
    
    private void pushRecord(String editorID, FormID formID, MajorRecord record) {
        editorIDCache.put(editorID, record);
        formIDCache.put(formID, record);
    }
    
    public <T extends MajorRecord> T get(String editorID) {
        T result = (T)editorIDCache.get(editorID);
        if (result == null) {
            MajorRecord m = merger.getMajor(editorID);
            if (m != null) {
                pushRecord(editorID, m.getForm(), m);
                result = (T)m;
            }
        }
        return result;
    }
    public <T extends MajorRecord> T get(FormID formID) {
        T result = (T)formIDCache.get(formID);
        if (result == null) {
            MajorRecord m = merger.getMajor(formID);
            if (m != null) {
                pushRecord(m.getEDID(), formID, m);
                result = (T)m;
            }
        }
        return result;
    }
    public <T extends MajorRecord> FormID getFormID(String editorID) {
        T t = get(editorID);
        return t == null ? FormID.NULL : t.getForm();
    }
    
    public <T extends MajorRecord> T getCopy(String editorID, String prefix, String suffix) {
        return (T)patch.makeCopy(get(editorID), prefix + editorID + suffix);
    }
    public <T extends MajorRecord> T getCopy(String editorID) {
        return getCopy(editorID, "CopyOf", "");
    }
    public <T extends MajorRecord> T getCopyWithPrefix(String editorID, String prefix) {
        return getCopy(editorID, prefix, "");
    }
    public <T extends MajorRecord> T getCopyWithSuffix(String editorID, String suffix) {
        return getCopy(editorID, "", suffix);
    }
    public <T extends MajorRecord> T getCopy(FormID formID, String prefix, String suffix) {
        T t = get(formID);
        return t == null ? null : (T)patch.makeCopy(t, prefix + t.getEDID() + suffix);
    }
    public <T extends MajorRecord> T getCopy(FormID formID) {
        return getCopy(formID, "CopyOf", "");
    }
    public <T extends MajorRecord> T getCopyWithPrefix(FormID formID, String prefix) {
        return getCopy(formID, prefix, "");
    }
    public <T extends MajorRecord> T getCopyWithSuffix(FormID formID, String suffix) {
        return getCopy(formID, "", suffix);
    }
}
