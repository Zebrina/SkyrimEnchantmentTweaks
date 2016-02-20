/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enchantingtweaks.taskmodules;

import enchantingtweaks.data.Records;
import enchantingtweaks.data.Settings;
import enchantingtweaks.data.SoulGems;
import java.util.HashSet;
import skyproc.ARMO;
import skyproc.BodyTemplate;
import skyproc.COBJ;
import skyproc.Condition;
import skyproc.ENCH;
import skyproc.FLST;
import skyproc.FormID;
import skyproc.MajorRecord;
import skyproc.NPC_;
import skyproc.RACE;
import skyproc.WEAP;

/**
 *
 * @author Sabrina
 */
public class MakeEnchantmentRemovalConstructibleObject {
    // Keywords
    private final static FormID MAGICDISALLOWENCHANTING = new FormID("0C27BD", "Skyrim.esm");
    private final static FormID CRAFTINGARCANEFONT = new FormID("04ECF3", "EnchantingTweaks.esp");
    
    private final HashSet<FormID> skins;
    
    // Sub modules.
    EnforceUniqueEnchantmentRestriction uniqueEnchantmentRestrictionEnforcer;
    ResolveUniqueEnchantedRecordDescription uniqueEnchantmentDescriptionResolver = new ResolveUniqueEnchantedRecordDescription();
    
    public MakeEnchantmentRemovalConstructibleObject() {
        skins = new HashSet<>();
        
        for (RACE race : Records.db().getMergedMod().getRaces()) {
            if (!race.getWornArmor().isNull()) {
                skins.add(race.getWornArmor());
            }
        }
        for (NPC_ npc : Records.db().getMergedMod().getNPCs()) {
            if (!npc.getSkin().isNull()) {
                skins.add(npc.getSkin());
            }
        }
        
        uniqueEnchantmentRestrictionEnforcer = Settings.EnforceUniqueEnchantmentRestrictions ? new EnforceUniqueEnchantmentRestriction() : null;
    }
    
    private boolean isPlayable(MajorRecord record) {
        return !record.get(MajorRecord.MajorFlags.NonPlayable) &&
                !(record instanceof WEAP ? ((WEAP)record).get(WEAP.WeaponFlag.NonPlayable) : ((ARMO)record).getBodyTemplate().get(BodyTemplate.GeneralFlags.NonPlayable));
    }
    private boolean isSkin(FormID armor) {
        return skins.contains(armor);
    }
    
    private ENCH getEnchantment(MajorRecord record) throws Exception {
        return Records.db().tryGet(record instanceof WEAP ? ((WEAP)record).getEnchantment() : ((ARMO)record).getEnchantment());
    }
    private MajorRecord getTemplate(MajorRecord record) throws Exception {
        return Records.db().tryGet(record instanceof WEAP ? ((WEAP)record).getTemplate(): ((ARMO)record).getTemplate());
    }
    
    private MajorRecord createUnenchantedCopy(MajorRecord record) throws Exception {
        MajorRecord recordCopy = Records.db().getCopyWithSuffix(record.getForm(), "NoEnch");
        
        if (recordCopy instanceof WEAP) {
            WEAP weaponRecord = (WEAP)recordCopy;
            
            weaponRecord.setDescription("");
            weaponRecord.setEnchantment(FormID.NULL);
            weaponRecord.setEnchantmentCharge(0);
            weaponRecord.getKeywordSet().removeKeywordRef(MAGICDISALLOWENCHANTING);
        }
        else {
            ARMO armorRecord = (ARMO)recordCopy;
            
            armorRecord.setDescription("");
            armorRecord.setEnchantment(FormID.NULL);
            armorRecord.getKeywordSet().removeKeywordRef(MAGICDISALLOWENCHANTING);
        }
        
        return recordCopy;
    }
    private COBJ createRemoveEnchantmentConstructibleObject(MajorRecord fromRecord, MajorRecord toRecord) {
        COBJ cobj = new COBJ("RemoveEnch" + fromRecord.getEDID());
        
        cobj.addIngredient(fromRecord.getForm(), 1);
        cobj.addIngredient(SoulGems.SoulGemBlackFilled, 1);

        Condition c = new Condition(Condition.P_FormID.GetItemCount, fromRecord.getForm());
        c.setOperator(Condition.Operator.GreaterThan);
        c.setValue(0);
        c.setRunOnType(Condition.RunOnType.Subject);
        cobj.addCondition(c);

        cobj.setResultFormID(toRecord.getForm());
        cobj.setBenchKeywordFormID(CRAFTINGARCANEFONT);
        cobj.setOutputQuantity(1);
                
        return cobj;
    }
    
    public void process(MajorRecord record) throws Exception {
        if (record != null) {
            if (!(record instanceof WEAP || record instanceof ARMO)) {
                throw new IllegalArgumentException("record");
            }

            // Make sure item is playable and not a skin armor.
            if (isPlayable(record) && (record instanceof ARMO && !isSkin(record.getForm()))) {
                ENCH enchantmentRecord = getEnchantment(record);
                MajorRecord templateRecord = getTemplate(record);

                if (templateRecord == null) {
                    boolean added = false;

                    if (enchantmentRecord == null) {
                        // Item has no enchantment.
                        // Make sure item does not have prevent enchanting keyword. If that's the case then it's not meant to be enchanted.
                        if (!(record instanceof WEAP ? ((WEAP)record).getKeywordSet() : ((ARMO)record).getKeywordSet()).getKeywordRefs().contains(MAGICDISALLOWENCHANTING)) {
                            createRemoveEnchantmentConstructibleObject(record, record);

                            added = true;
                        }
                    }
                    else {
                        // Item has unique enchantment.
                        // Create new unenchanted copy.
                        MajorRecord unenchantedRecord = createUnenchantedCopy(record);

                        createRemoveEnchantmentConstructibleObject(record, unenchantedRecord);

                        if (uniqueEnchantmentRestrictionEnforcer != null) {
                            uniqueEnchantmentRestrictionEnforcer.process(unenchantedRecord, enchantmentRecord);
                        }
                        uniqueEnchantmentDescriptionResolver.process(record, enchantmentRecord);

                        added = true;
                    }

                    if (added) {
                        FLST inventoryFilter = Records.db().get("__EnchTw_InventoryFilter");
                        inventoryFilter.addFormEntry(record.getForm());
                        Records.db().addRecordToPatch(inventoryFilter);
                    }
                }
                else if (enchantmentRecord != null) {
                    // Item has a template.
                    createRemoveEnchantmentConstructibleObject(record, templateRecord);
                }
            }
        }
    }
}
