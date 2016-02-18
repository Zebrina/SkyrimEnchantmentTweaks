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
public class RecordInvalidException extends RecordException {
    private static final String DEFAULT_MESSAGE = "Invalid record!";
    
    public RecordInvalidException(String message, FormID formID) {
        super(message + " [XX" + formID.getFormStr().substring(0, 6) + " in " + formID.getFormStr().substring(6) + "]");
    }
    public RecordInvalidException(String message, MajorRecord record) {
        this(message, record.getForm());
    }
    public RecordInvalidException(FormID formID) {
        this(DEFAULT_MESSAGE, formID);
    }
    public RecordInvalidException(MajorRecord record) {
        this(DEFAULT_MESSAGE, record);
    }
}
