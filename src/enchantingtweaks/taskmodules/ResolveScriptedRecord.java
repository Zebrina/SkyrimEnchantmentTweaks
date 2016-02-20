/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enchantingtweaks.taskmodules;

import skyproc.ARMO;
import skyproc.MajorRecord;
import skyproc.ScriptPackage;
import skyproc.WEAP;

/**
 *
 * @author Sabrina
 */
public class ResolveScriptedRecord {
    public void process(MajorRecord record, String scriptToRemove, String scriptToAdd) throws Exception {
        if (record != null && scriptToAdd != null && !scriptToAdd.isEmpty()) {
            if (!(record instanceof WEAP || record instanceof ARMO)) {
                throw new IllegalArgumentException("record");
            }
            
            ScriptPackage scripts = record instanceof WEAP ? ((WEAP)record).getScriptPackage() : ((ARMO)record).getScriptPackage();
        }
    }
}
