/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enchantingtweaks.taskmodules;

import enchantingtweaks.data.Records;
import java.util.HashMap;
import skyproc.ARMO;
import skyproc.COBJ;
import skyproc.Condition;
import skyproc.FormID;
import skyproc.GLOB;
import skyproc.GLOB.GLOBType;
import skyproc.MajorRecord;
import skyproc.SPGlobal;
import skyproc.ScriptPackage;
import skyproc.ScriptRef;
import skyproc.WEAP;

/**
 *
 * @author Sabrina
 */
public class AttachUnenchantedRecordScript {
    private static final String ENCHANTABLESCRIPT = "EnchTw_Enchantable"; 
    
    // Keywords
    private final static FormID CRAFTINGARCANEFONT = new FormID("04ECF3", "EnchantingTweaks.esp");
    
    private final HashMap<FormID, FormID> scriptAttachedEnchantedRecords = new HashMap<>();
    
    public HashMap<FormID, FormID> getScriptAttachedEnchantedRecords() {
        return scriptAttachedEnchantedRecords;
    }
    
    public void process(COBJ cobj) throws Exception {
        if (cobj != null && cobj.getBenchKeywordFormID().equals(CRAFTINGARCANEFONT)) {
            MajorRecord unenchantedRecord = Records.db().get(cobj.getResultFormID());
            MajorRecord enchantedRecord = Records.db().get(cobj.getIngredients().get(0).getForm());
            
            if ((unenchantedRecord instanceof WEAP || unenchantedRecord instanceof ARMO) && unenchantedRecord.getClass().equals(enchantedRecord.getClass())) {
                if (Records.db().isNull(unenchantedRecord instanceof WEAP ? ((WEAP)enchantedRecord).getTemplate(): ((ARMO)enchantedRecord).getTemplate())) {
                    ScriptPackage scripts = unenchantedRecord instanceof WEAP ? ((WEAP)unenchantedRecord).getScriptPackage() : ((ARMO)unenchantedRecord).getScriptPackage();
                    if (!scripts.hasScript(ENCHANTABLESCRIPT)) {
                        

                        ScriptRef enchantableScript = scripts.hasScript(ENCHANTABLESCRIPT) ? scripts.getScript(ENCHANTABLESCRIPT) : new ScriptRef(ENCHANTABLESCRIPT);

                        if (enchantedRecord.getForm().equals(cobj.getResultFormID())) {
                            FormID toggleVisibilityGVar = (new GLOB(unenchantedRecord.getEDID() + "ToggleVisibility", GLOBType.Long)).getForm();
                        }
                        else {
                            enchantableScript.setProperty("BaseEnchantedObject", enchantedRecord.getForm());

                            scriptAttachedEnchantedRecords.put(enchantedRecord.getForm(), unenchantedRecord.getForm());
                        }
                        enchantableScript.setProperty("Visible", toggleVisibilityGVar);

                        scripts.addScript(enchantableScript);

                        Condition c = new Condition(Condition.P_FormID.GetGlobalValue, toggleVisibilityGVar);
                        c.setOperator(Condition.Operator.GreaterThan);
                        c.setValue(0);
                        c.setRunOnType(Condition.RunOnType.Subject);

                        cobj.addCondition(c);

                        SPGlobal.log("SCRIPTS", "Attaching 'EnchTw_Enchantable' script to " + unenchantedRecord.getEDID());

                        Records.db().addRecordToPatch(unenchantedRecord);
                    }
                }
            }
        }
    }
}
