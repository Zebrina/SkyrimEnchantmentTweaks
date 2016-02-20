/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enchantingtweaks.data;

import enchantingtweaks.exceptions.RecordCopyFailureException;
import enchantingtweaks.exceptions.RecordInvalidException;
import enchantingtweaks.exceptions.RecordNotFoundException;
import enchantingtweaks.exceptions.RecordNullException;
import java.util.HashMap;
import skyproc.FormID;
import skyproc.MajorRecord;
import skyproc.Mod;
import skyproc.SPGlobal;

/**
 *
 * @author Sabrina
 */
public class Records {
    private static class RecordDBKey {
        private final String editorID;
        private final FormID formID;
        
        public RecordDBKey(String editorID, FormID formID) {
            this.editorID = editorID;
            this.formID = formID;
        }
        
        public static RecordDBKey byEditorID(String editorID) {
            return new RecordDBKey(editorID, FormID.NULL);
        }
        public static RecordDBKey byFormID(FormID formID) {
            return new RecordDBKey("", formID);
        }
        
        private boolean compare(RecordDBKey other) {
            return other.editorID.equals(editorID) || other.formID.equals(formID);
        }
        
        @Override
        public boolean equals(Object other) {
            if (other != null && this.getClass().equals(other.getClass())) {
                return compare((RecordDBKey)other);
            }
            return false;
        } 

        @Override
        public int hashCode() {
            return RecordDBKey.class.getName().hashCode();
        }
    }
    
    private static Records instance = null;
    
    public static Records db() {
        if (instance == null) {
            instance = new Records();
        }
        return instance;
    }
    
    private Mod merger = null;
    private Mod patch = null;
    private final HashMap<Object, MajorRecord> recordCache = new HashMap<>();
    //private final HashMap<String, MajorRecord> copiedCache = new HashMap<>();

    private Records() {
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
                throw new Exception("Invalid key type passed to getMajor (" + key.getClass().toString() + " key)");
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
            /*
            if (key instanceof FormID && !((FormID)key).isNull()) {
                SPGlobal.log("Records.db().isValid(...)", "Record was not valid -> " + ex.getMessage());
            }
            */
            isValid = false;
        }
        return isValid;
    }
    public boolean isNull(Object key) {
        return !isValid(key);
    }
    
    public <T extends MajorRecord> T get(Object key) throws Exception {
        return getMajor(key);
    }
    public <T extends MajorRecord> T tryGet(Object key) throws Exception {
        T result;
        try {
            result = get(key);
        }
        catch (Exception ex) {
            if (key instanceof FormID && !((FormID)key).isNull()) {
                SPGlobal.log("Records.db().tryGet(...)", "Record was not valid -> " + ex.getMessage());
            }
            result = null;
        }
        return result;
    }
        
    public String getEditorID(Object key) throws Exception {
        return get(key).getEDID();
    }
    public FormID getFormID(Object key) throws Exception {
        return get(key).getForm();
    }
    
    public <T extends MajorRecord> T getCopy(Object key, String newEditorID) throws Exception {
        T copy = (T)recordCache.get(newEditorID);
        if (copy == null) {
            copy = (T)get(key).copy(newEditorID);
            if (copy == null) {
                throw new RecordCopyFailureException(get(key));
            }
            else {
                saveRecord(copy);
            }
        }
        return copy;
    }
    public <T extends MajorRecord> T getCopyWithPrefix(Object key, String prefix) throws Exception {
        return getCopy(key, prefix + getEditorID(key));
    }
    public <T extends MajorRecord> T getCopyWithSuffix(Object key, String suffix) throws Exception {
        return getCopy(key, getEditorID(key) + suffix);
    }
    public <T extends MajorRecord> T getCopy(Object key, String prefix, String suffix) throws Exception {
        return getCopy(key, prefix + getEditorID(key) + suffix);
    }
    
    public Mod getMergedMod() {
        return merger;
    }
    public Mod getPatchMod() {
        return patch;
    }
    
    public void addRecordToPatch(Object key) throws Exception {
        if (key instanceof FormID && ((FormID)key).isNull()) {
            throw new RecordNullException();
        }
        patch.addRecord(get(key));
    }
    public void addRecordToPatch(MajorRecord record) throws RecordNullException {
        if (record == null) {
            throw new RecordNullException();
        }
        patch.addRecord(record);
    }
}
