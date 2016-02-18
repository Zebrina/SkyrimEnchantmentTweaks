/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enchantingtweaks.exceptions;

/**
 *
 * @author Sabrina
 */
public abstract class RecordException extends Exception {
    protected RecordException(String message) {
        super(message);
    }
}
