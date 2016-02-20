/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enchantingtweaks.taskmodules;

import enchantingtweaks.data.Records;
import enchantingtweaks.exceptions.RecordInvalidException;
import java.util.HashSet;
import skyproc.ARMO;
import skyproc.ENCH;
import skyproc.FormID;
import skyproc.MGEF;
import skyproc.MagicEffectRef;
import skyproc.MajorRecord;
import skyproc.WEAP;
import skyproc.genenums.CastType;
import skyproc.genenums.DeliveryType;

/**
 *
 * @author Sabrina
 */
public class ResolveUniqueEnchantedRecordDescription {
    private final HashSet<FormID> modifiedEnchantments = new HashSet<>();
    
    private boolean hasDescription(MajorRecord record) {
        String description = record instanceof WEAP ? ((WEAP)record).getDescription() : ((ARMO)record).getDescription();
        return description != null && !description.equals("") && !description.equals("<NO TEXT>");
    }
    
    private MGEF createDescriptionMagicEffect(MajorRecord record) throws Exception {
        MGEF descriptionMagicEffect = Records.db().getCopy("__EnchTw_DescriptionTemplate", "Desc" + record.getEDID());
        
        if (record instanceof WEAP) {
            WEAP weaponRecord = (WEAP)record;
            
            descriptionMagicEffect.setName(weaponRecord.getName());
            descriptionMagicEffect.setDescription(weaponRecord.getDescription());
            descriptionMagicEffect.setCastType(CastType.FireAndForget);
            descriptionMagicEffect.setDeliveryType(DeliveryType.Touch);
        }
        else {
            ARMO armorRecord = (ARMO)record;
            
            descriptionMagicEffect.setName(armorRecord.getName());
            descriptionMagicEffect.setDescription(armorRecord.getDescription());
        }
        
        return descriptionMagicEffect;
    }
    
    public void process(MajorRecord record, ENCH enchantmentRecord) throws Exception {
        if (record != null && enchantmentRecord != null) {
            if (!(record instanceof WEAP || record instanceof ARMO)) {
                throw new RecordInvalidException("Wrong record type for arg record", record);
            }

            if (hasDescription(record)) {
                ENCH baseEnchantmentRecord = Records.db().tryGet(enchantmentRecord.getBaseEnchantment());

                if (baseEnchantmentRecord != null || modifiedEnchantments.contains(enchantmentRecord.getForm())) {
                    enchantmentRecord = Records.db().getCopy(enchantmentRecord.getForm(), "NewEnchFor" + record.getEDID());

                    enchantmentRecord.getMagicEffects().set(enchantmentRecord.getMagicEffects().size() - 1, new MagicEffectRef(createDescriptionMagicEffect(record).getForm()));

                    if (record instanceof WEAP) {
                        ((WEAP)record).setEnchantment(enchantmentRecord.getForm());
                    }
                    else {
                        ((ARMO)record).setEnchantment(enchantmentRecord.getForm());
                    }
                }
                else {
                    for (int i = 0; i < enchantmentRecord.getMagicEffects().size(); ++i) {
                        MagicEffectRef ref = enchantmentRecord.getMagicEffects().get(i);

                        MGEF magicEffectRecord = Records.db().get(ref.getMagicRef());
                        if (!magicEffectRecord.get(MGEF.SpellEffectFlag.HideInUI)) {
                            magicEffectRecord = Records.db().getCopyWithSuffix(magicEffectRecord.getForm(), "Hidden");
                            magicEffectRecord.set(MGEF.SpellEffectFlag.HideInUI, true);
                            magicEffectRecord.set(MGEF.SpellEffectFlag.PowerAffectsMagnitude, false);
                            magicEffectRecord.set(MGEF.SpellEffectFlag.PowerAffectsDuration, false);
                            ref.setMagicRef(magicEffectRecord.getForm());
                        }
                        else if (magicEffectRecord.get(MGEF.SpellEffectFlag.PowerAffectsMagnitude) || magicEffectRecord.get(MGEF.SpellEffectFlag.PowerAffectsDuration)){
                            magicEffectRecord.set(MGEF.SpellEffectFlag.PowerAffectsMagnitude, false);
                            magicEffectRecord.set(MGEF.SpellEffectFlag.PowerAffectsDuration, false);
                            Records.db().addRecordToPatch(magicEffectRecord);
                        }
                    }

                    enchantmentRecord.addMagicEffect(createDescriptionMagicEffect(record));
                    Records.db().addRecordToPatch(enchantmentRecord);

                    modifiedEnchantments.add(enchantmentRecord.getForm());
                }

                if (record instanceof WEAP) {
                    ((WEAP)record).setDescription("");
                }
                else {
                    ((ARMO)record).setDescription("");
                }

                Records.db().addRecordToPatch(record);
            }
        }
    }
}
