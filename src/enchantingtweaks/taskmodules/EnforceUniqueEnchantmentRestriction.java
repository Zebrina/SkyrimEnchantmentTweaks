/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enchantingtweaks.taskmodules;

import enchantingtweaks.data.Records;
import enchantingtweaks.exceptions.RecordInvalidException;
import java.util.HashMap;
import skyproc.ARMO;
import skyproc.ENCH;
import skyproc.FLST;
import skyproc.FormID;
import skyproc.KYWD;
import skyproc.MajorRecord;
import skyproc.WEAP;

/**
 *
 * @author Sabrina
 */
public class EnforceUniqueEnchantmentRestriction {
    private final HashMap<FormID, FormID> restrictedEnchantments = new HashMap<>();
        
    public void process(MajorRecord unenchantedRecord, ENCH enchantmentRecord) throws Exception {
        if (unenchantedRecord != null && enchantmentRecord != null) {
            if (!(unenchantedRecord instanceof WEAP || unenchantedRecord instanceof ARMO)) {
                throw new RecordInvalidException("Wrong record type for arg unenchantedRecord", unenchantedRecord);
            }

            FormID kw = restrictedEnchantments.get(enchantmentRecord.getForm());

            if (kw == null) {
                FLST enchantmentRestrictions = Records.db().tryGet(enchantmentRecord.getWornRestrictions());
                if (enchantmentRestrictions == null) {
                    enchantmentRestrictions = new FLST("EnchRestriction" + enchantmentRecord.getEDID());
                }
                else {
                    Records.db().addRecordToPatch(enchantmentRestrictions);
                }

                enchantmentRestrictions.addFormEntry(kw = (new KYWD("UniqueEnchantment" + enchantmentRecord.getEDID())).getForm());

                restrictedEnchantments.put(enchantmentRecord.getForm(), kw);
            }

            (unenchantedRecord instanceof WEAP ? ((WEAP)unenchantedRecord).getKeywordSet() : ((ARMO)unenchantedRecord).getKeywordSet()).addKeywordRef(kw);
        }
    } 
}
