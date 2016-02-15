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
    public RecordNotFoundException(String message, FormID form) {
        super(message + " [XX" + form.getFormStr().substring(0, 6) + " in " + form.getFormStr().substring(6) + "]");
    }
    public RecordNotFoundException(String message, MajorRecord record) {
        this(message, record.getForm());
    }
}
