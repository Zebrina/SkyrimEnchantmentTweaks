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
public class RecordCopyFailureException extends Exception {
    private static final String DEFAULT_MESSAGE = "Failed to copy record!";
    
    public RecordCopyFailureException(String message, FormID formID) {
        super(message + " [" + (formID.isNull() ? "NULL" : ("XX" + formID.getFormStr().substring(0, 6) + " in " + formID.getFormStr().substring(6))) + "]");
    }
    public RecordCopyFailureException(String message, MajorRecord record) {
        this(message, record != null ? record.getForm() : FormID.NULL);
    }
    public RecordCopyFailureException(FormID formID) {
        this(DEFAULT_MESSAGE, formID);
    }
    public RecordCopyFailureException(MajorRecord record) {
        this(DEFAULT_MESSAGE, record);
    }
}
