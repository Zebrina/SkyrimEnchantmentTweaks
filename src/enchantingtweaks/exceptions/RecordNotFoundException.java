/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enchantingtweaks.exceptions;

import skyproc.FormID;
import skyproc.MajorRecord;

/**
 *
 * @author Sabrina
 */
public class RecordNotFoundException extends Exception {
    private final static String DEFAULT_MESSAGE = "Record not found!";
    
    public RecordNotFoundException(String message, FormID formID) {
        super(message + " [XX" + formID.getFormStr().substring(0, 6) + " in " + formID.getFormStr().substring(6) + "]");
    }
    public RecordNotFoundException(String message, MajorRecord record) {
        this(message, record.getForm());
    }
    public RecordNotFoundException(FormID formID) {
        this(DEFAULT_MESSAGE, formID);
    }
    public RecordNotFoundException(MajorRecord record) {
        this(DEFAULT_MESSAGE, record);
    }
    public RecordNotFoundException(String message, String editorID) {
        super(message + " [" + editorID + "]");
    }
    public RecordNotFoundException(String editorID) {
        this(DEFAULT_MESSAGE, editorID);
    }
}
