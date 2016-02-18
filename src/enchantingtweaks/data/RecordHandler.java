/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enchantingtweaks.data;

import enchantingtweaks.exceptions.RecordCopyFailureException;
import enchantingtweaks.exceptions.RecordInvalidException;
import enchantingtweaks.exceptions.RecordNotFoundException;
import java.util.HashMap;
import skyproc.ENCH;
import skyproc.FormID;
import skyproc.MajorRecord;
import skyproc.Mod;
import skyproc.SPGlobal;

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
    private final HashMap<Object, MajorRecord> recordCache = new HashMap<>();
    //private final HashMap<String, MajorRecord> copiedCache = new HashMap<>();

    private RecordHandler() {
        patch = SPGlobal.getGlobalPatch();
	merger = new Mod("RecordHandlerDB", false);
	merger.addAsOverrides(SPGlobal.getDB());
    }
    
    private void saveRecord(MajorRecord record) {
        recordCache.put(record.getEDID(), record);
        recordCache.put(record.getForm(), record);
        //SPGlobal.log("saveRecord", "Pushed [" + record.getEDID() + " :  XX" + record.getForm().getFormStr().substring(0, 6) + " in " + record.getForm().getFormStr().substring(6) + "]");
    }
    
    private <T extends MajorRecord> T getMajor(Object key) throws Exception {
        MajorRecord result = recordCache.get(key);
        if (result == null) {
            if (key instanceof String) {
                result = merger.getMajor((String)key);
            }
            else if (key instanceof FormID) {
                result = merger.getMajor((FormID)key);
            }
            else {
                throw new Exception("Invalid key type passed to getMajor(" + key.getClass().toString() + " key)");
            }

            if (result == null) {
                throw key instanceof String ? new RecordNotFoundException((String)key) : new RecordNotFoundException((FormID)key);
            }
            
            saveRecord(result);
        }
        
        if ((T)result == null) {
            throw new RecordInvalidException("Record found but is of wrong type (type is " + result.getClass().toString() + ")", result);
        }
        
        return (T)result;
    }
        
    public boolean isValid(Object key) {
        boolean isValid;
        try {
            isValid = getMajor(key) != null;
        }
        catch (Exception ex) {
            SPGlobal.log("RecordHandler", "Record was not valid -> " + ex.getMessage());
            isValid = false;
        }
        return isValid;
    }
    
    public <T extends MajorRecord> T getRecord(Object key) throws Exception {
        return getMajor(key);
    }
        
    private <T extends MajorRecord> T getCopy(Object key, String newEditorID) throws Exception {
        T copy = (T)recordCache.get(newEditorID);
        if (copy == null) {
            copy = (T)getRecord(key).copy(newEditorID);
            if (copy == null) {
                throw new RecordCopyFailureException(getRecord(key));
            }
            else {
                saveRecord(copy);
                
                if (copy instanceof ENCH) {
                }
            }
        }
        return copy;
    }
    
    public <T extends MajorRecord> T get(String editorID) throws Exception {
        if (editorID.isEmpty()) {
            throw new Exception("Empty editor id passed");
        }
        return getRecord(editorID);
    }
    public <T extends MajorRecord> T get(FormID formID) throws Exception {
        return getRecord(formID);
    }
    
    public String getEditorID(FormID formID) throws Exception {
        return getRecord(formID).getEDID();
    }
    public FormID getFormID(String editorID) throws Exception {
        return getRecord(editorID).getForm();
    }
    
    public <T extends MajorRecord> T getCopy(String editorID, String newEditorID) throws Exception {
        return getCopy((Object)editorID, newEditorID);
    }
    public <T extends MajorRecord> T getCopyWithPrefix(String editorID, String prefix) throws Exception {
        return getCopy((Object)editorID, prefix + editorID);
    }
    public <T extends MajorRecord> T getCopyWithSuffix(String editorID, String suffix) throws Exception {
        return getCopy((Object)editorID, editorID + suffix);
    }
    public <T extends MajorRecord> T getCopy(String editorID, String prefix, String suffix) throws Exception {
        return getCopy((Object)editorID, prefix + editorID + suffix);
    }
    public <T extends MajorRecord> T getCopy(FormID formID, String newEditorID) throws Exception {
        return getCopy((Object)formID, newEditorID);
    }
    public <T extends MajorRecord> T getCopy(FormID formID) throws Exception {
        return getCopy((Object)formID, "CopyOf" + getEditorID(formID));
    }
    public <T extends MajorRecord> T getCopyWithPrefix(FormID formID, String prefix) throws Exception {
        return getCopy((Object)formID, prefix + getEditorID(formID));
    }
    public <T extends MajorRecord> T getCopyWithSuffix(FormID formID, String suffix) throws Exception {
        return getCopy((Object)formID, getEditorID(formID) + suffix);
    }
    public <T extends MajorRecord> T getCopy(FormID formID, String prefix, String suffix) throws Exception {
        return getCopy((Object)formID, prefix + getEditorID(formID) + suffix);
    }
    
    public <T extends MajorRecord> boolean isValid(String editorID) {
        return isValid((Object)editorID);
    }
    public <T extends MajorRecord> boolean isValid(FormID formID) {
        return isValid((Object)formID);
    }
    public <T extends MajorRecord> boolean isNull(String editorID) {
        return !isValid((Object)editorID);
    }
    public <T extends MajorRecord> boolean isNull(FormID formID) {
        return !isValid((Object)formID);
    }
    
    public Mod getDB() {
        return merger;
    }
    
    public void addToPatch(String editorID) throws Exception {
        patch.addRecord(get(editorID));
    }
    public void addToPatch(FormID formID) throws Exception {
        patch.addRecord(get(formID));
    }
    public void addToPatch(MajorRecord record) {
        patch.addRecord(record);
    }
}
