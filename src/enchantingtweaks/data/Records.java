/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enchantingtweaks.data;

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
    private class RecordNotFoundException extends Exception {
        public RecordNotFoundException(RecordDBKey key) {
            super("Record '" + key + "' not found");
        }
    }
    private class RecordTypeMismatchException extends Exception {
        public RecordTypeMismatchException(RecordDBKey key, MajorRecord record) {
            super("Record '" + key + "' is of type '" + record.getClass().getName() + "'.");
        }
    }
    private class RecordCopyFailureException extends Exception {
        public RecordCopyFailureException(RecordDBKey key) {
            super("Failed to copy record '" + key + "'");
        }
    }
    
    private static class RecordDBKey {
        public final String editorID;
        public final FormID formID;
        
        public RecordDBKey(String editorID, FormID formID) throws IllegalArgumentException {
            if (editorID == null || formID == null) {
                throw new IllegalArgumentException();
            }
            
            this.editorID = editorID;
            this.formID = formID;
        }
        public RecordDBKey(MajorRecord record) throws NullPointerException, IllegalArgumentException {
            this(record.getEDID(), record.getForm());
        }
        
        public static RecordDBKey byEditorID(String editorID) {
            return new RecordDBKey(editorID, FormID.NULL);
        }
        public static RecordDBKey byFormID(FormID formID) {
            return new RecordDBKey("", formID);
        }
        public static RecordDBKey byKey(Object key) throws NullPointerException, IllegalArgumentException {
            if (key instanceof String) {
                return byEditorID((String)key);
            }
            else if (key instanceof FormID) {
                return byFormID((FormID)key);
            }
            throw new IllegalArgumentException("Invalid key type passed to RecordDBKey.byKey(key) (key type is " + key.getClass().getName() + ")");
        }
        
        public boolean hasEditorID() {
            return !editorID.isEmpty();
        }
        public boolean hasFormID() {
            return !formID.isNull();
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
        
        @Override
        public String toString() {
            if (hasEditorID() && hasFormID()) {
                return editorID + "|XX" + formID.getFormStr().substring(0, 6) + " in " + formID.getFormStr().substring(6);
            }
            else if (hasEditorID()) {
                return editorID;
            }
            else if (hasFormID()) {
                return "XX" + formID.getFormStr().substring(0, 6) + " in " + formID.getFormStr().substring(6);
            }
            return "NULL";
        }
    }
    
    private static Records instance = null;
    
    public static Records db() {
        if (instance == null) {
            instance = new Records();
        }
        return instance;
    }
    
    private final Mod mergedMod;
    private final Mod patchMod;
    
    private final HashMap<RecordDBKey, MajorRecord> recordTable;

    private Records() {
        patchMod = SPGlobal.getGlobalPatch();
	mergedMod = new Mod("RecordHandlerDB", false);
	mergedMod.addAsOverrides(SPGlobal.getDB());
        
        recordTable = new HashMap<>();
    }
    
    private void saveRecord(MajorRecord record) {
        recordTable.put(new RecordDBKey(record), record);
    }
    
    private <T extends MajorRecord> T getMajor(RecordDBKey key) throws IllegalArgumentException, RecordNotFoundException, RecordTypeMismatchException {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        
        MajorRecord result = recordTable.get(key);
        if (result == null) {
            if (key.hasEditorID()) {
                result = mergedMod.getMajor(key.editorID);
            }
            else {
                result = mergedMod.getMajor(key.formID);
            }

            if (result == null) {
                throw new RecordNotFoundException(key);
            }
            
            saveRecord(result);
        }
        
        if ((T)result == null) {
            throw new RecordTypeMismatchException(key, result);
        }
        
        return (T)result;
    }
        
    public boolean isValid(Object key) {
        return tryGet(key) != null;
    }
    public boolean isNull(Object key) {
        return !isValid(key);
    }
    
    public <T extends MajorRecord> T get(Object key) throws IllegalArgumentException, RecordNotFoundException, RecordTypeMismatchException {
        return getMajor(RecordDBKey.byKey(key));
    }
    public <T extends MajorRecord> T tryGet(Object key) {
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
        
    public String getEditorID(Object key) throws IllegalArgumentException, RecordNotFoundException, RecordTypeMismatchException {
        return get(key).getEDID();
    }
    public FormID getFormID(Object key) throws IllegalArgumentException, RecordNotFoundException, RecordTypeMismatchException {
        return get(key).getForm();
    }
    
    public <T extends MajorRecord> T getCopy(Object key, String newEditorID) throws IllegalArgumentException, RecordNotFoundException, RecordTypeMismatchException, RecordCopyFailureException {
        RecordDBKey dbKey = RecordDBKey.byKey(key);
        T copy = (T)recordTable.get(dbKey);
        if (copy == null) {
            copy = (T)get(key).copy(newEditorID);
            if (copy == null) {
                throw new RecordCopyFailureException(dbKey);
            }
            else {
                saveRecord(copy);
            }
        }
        return copy;
    }
    public <T extends MajorRecord> T getCopyWithPrefix(Object key, String prefix) throws IllegalArgumentException, RecordNotFoundException, RecordTypeMismatchException, RecordCopyFailureException {
        return getCopy(key, prefix + getEditorID(key));
    }
    public <T extends MajorRecord> T getCopyWithSuffix(Object key, String suffix) throws IllegalArgumentException, RecordNotFoundException, RecordTypeMismatchException, RecordCopyFailureException {
        return getCopy(key, getEditorID(key) + suffix);
    }
    public <T extends MajorRecord> T getCopy(Object key, String prefix, String suffix) throws IllegalArgumentException, RecordNotFoundException, RecordTypeMismatchException, RecordCopyFailureException {
        return getCopy(key, prefix + getEditorID(key) + suffix);
    }
    
    public Mod getMergedMod() {
        return mergedMod;
    }
    public Mod getPatchMod() {
        return patchMod;
    }
    
    public void addRecordToPatch(Object key) throws IllegalArgumentException, RecordNotFoundException, RecordTypeMismatchException {
        patchMod.addRecord(get(RecordDBKey.byKey(key)));
    }
    public void addRecordToPatch(MajorRecord record) {
        patchMod.addRecord(record);
    }
}
