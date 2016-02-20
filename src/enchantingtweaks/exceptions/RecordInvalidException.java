/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enchantingtweaks.exceptions;

import enchantingtweaks.data.Records;
import skyproc.FormID;
import skyproc.MajorRecord;

/**
 *
 * @author Sabrina
 */
public class RecordInvalidException extends RecordException {
    private static final String DEFAULT_MESSAGE = "Invalid record!";
    
    public RecordInvalidException(String message, FormID formID) throws Exception {
        super(message + " [XX" + formID.getFormStr().substring(0, 6) + " in " + formID.getFormStr().substring(6) + " of type " + Records.db().get(formID).getClass() + "+]");
    }
    public RecordInvalidException(String message, MajorRecord record) throws Exception {
        super(message + " [XX" + record.getForm().getFormStr().substring(0, 6) + " in " + record.getForm().getFormStr().substring(6) + " of type " + record.getClass() + "+]");
    }
    public RecordInvalidException(FormID formID) throws Exception {
        this(DEFAULT_MESSAGE, formID);
    }
    public RecordInvalidException(MajorRecord record) throws Exception {
        this(DEFAULT_MESSAGE, record);
    }
}
