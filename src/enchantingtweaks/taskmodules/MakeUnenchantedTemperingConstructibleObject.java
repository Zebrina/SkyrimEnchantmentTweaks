/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enchantingtweaks.taskmodules;

import enchantingtweaks.data.Records;
import java.util.ArrayList;
import skyproc.COBJ;
import skyproc.FormID;

/**
 *
 * @author Sabrina
 */
public class MakeUnenchantedTemperingConstructibleObject {
    private final ArrayList<FormID> workBenchKeywords;
    
    public MakeUnenchantedTemperingConstructibleObject() {
        workBenchKeywords = new ArrayList<>();
        workBenchKeywords.add(new FormID("088108", "Skyrim.esm"));
        workBenchKeywords.add(new FormID("0ADB78", "Skyrim.esm"));
    }
    
    public boolean isConstructibleObjectTempering(COBJ cobj) {
        return cobj != null && workBenchKeywords.contains(cobj.getBenchKeywordFormID());
    }
    
    public void process(COBJ cobj, FormID unenchanted) throws Exception {
        if (cobj != null && unenchanted != null && workBenchKeywords.contains(cobj.getBenchKeywordFormID())) {
            COBJ cobjCopy = Records.db().getCopyWithSuffix(cobj.getForm(), "NoEnch");
            cobjCopy.setResultFormID(unenchanted);
        }
    }
}
