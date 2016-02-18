/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enchantingtweaks;

import enchantingtweaks.data.SoulGems;
import enchantingtweaks.data.RecordHandler;
import java.util.HashMap;
import java.util.HashSet;
import skyproc.*;
import skyproc.Condition.P_FormID;
import skyproc.genenums.CastType;
import skyproc.genenums.DeliveryType;

/**
 *
 * @author Sabrina
 */
public class EnchantableObjectProcessor {
    private final HashMap<FormID, FLST> templateRecordLists = new HashMap<>();
    
    private final HashSet<FormID> modifiedEnchantments = new HashSet<>();
    private final HashMap<FormID, FormID> duplicatedRecords = new HashMap<>();
    
    private MGEF createDescriptionMagicEffect(EnchantableObject record) throws Exception {
        MGEF descriptionMagicEffect = RecordHandler.inst().getCopy("DescriptionMagicEffectTemplate", "Desc" + record.getEditorID());
        
        descriptionMagicEffect.setName(record.getName());
        descriptionMagicEffect.setDescription(record.getDescription());
        
        if (record.get() instanceof WEAP) {
            descriptionMagicEffect.setCastType(CastType.FireAndForget);
            descriptionMagicEffect.setDeliveryType(DeliveryType.Touch);
        }
        
        return descriptionMagicEffect;
    }
    
    public void processRecord(EnchantableObject record) throws Exception {
        ENCH enchantment = RecordHandler.inst().get(record.getEnchantment());
        
        if (RecordHandler.inst().isValid(enchantment.getBaseEnchantment()) && record.getKeywords().getKeywordRefs().contains(RecordHandler.inst().getFormID("MagicDisallowEnchanting"))) {
            record.getKeywords().removeKeywordRef(RecordHandler.inst().getFormID("MagicDisallowEnchanting"));
            RecordHandler.inst().addToPatch(record.get());
        }
        
        if (record.getTemplate().isNull()) {
            EnchantableObject unenchantedRecord = record.getCopy();
            
            duplicatedRecords.put(record.getFormID(), unenchantedRecord.getFormID());

            COBJ recipe = new COBJ("RmEnch" + record.getEditorID());
            recipe.addIngredient(SoulGems.SoulGemBlackFilled, 1);
            if (enchantment.getBaseCost() > 0 || record.getValue() > 0) {
                recipe.addIngredient(new FormID("F", "Skyrim.esm") /* Gold */, enchantment.getBaseCost() + record.getValue());
            }
            Condition c = new Condition(P_FormID.GetItemCount, record.getFormID());
            c.setOperator(Condition.Operator.GreaterThan);
            c.setValue(0);
            c.setRunOnType(Condition.RunOnType.Subject);
            recipe.addCondition(c);
            recipe.setResultFormID(unenchantedRecord.getFormID());
            recipe.setBenchKeywordFormID(RecordHandler.inst().getFormID("CraftingArcaneFont"));
            recipe.setOutputQuantity(1);
            
            if (record.getDescription().isEmpty()) {
                //SPGlobal.log("DEBUG", "I'm empty!");
            }
            else {
                //SPGlobal.log("WTF", record.getEditorID() + " : '" + record.getDescription() + "'");
                
                KYWD uniqueKeyword = new KYWD("UniqKW" + record.getEditorID());

                // Add unique keyword to.
                unenchantedRecord.getKeywords().addKeywordRef(uniqueKeyword.getForm());

                FLST restrictions = new FLST("WrnRes" + record.getEditorID());

                restrictions.addFormEntry(uniqueKeyword.getForm());
                
                if (modifiedEnchantments.contains(enchantment.getForm())) {
                    enchantment = RecordHandler.inst().getCopy(enchantment.getForm(), "RplcEnchFor" + record.getEditorID());

                    enchantment.getMagicEffects().set(enchantment.getMagicEffects().size() - 1, new MagicEffectRef(createDescriptionMagicEffect(record).getForm()));
                        
                    record.setEnchantment(enchantment.getForm());
                }
                else {
                    for (int i = 0; i < enchantment.getMagicEffects().size(); ++i) {
                        MagicEffectRef ref = enchantment.getMagicEffects().get(i);

                        MGEF m = RecordHandler.inst().get(ref.getMagicRef());
                        if (!m.get(MGEF.SpellEffectFlag.HideInUI)) {
                            MGEF newMagicEffect = RecordHandler.inst().getCopyWithSuffix(m.getForm(), "Hidden");
                            newMagicEffect.set(MGEF.SpellEffectFlag.HideInUI, true);
                            ref.setMagicRef(newMagicEffect.getForm());
                        }
                    }
                    
                    modifiedEnchantments.add(enchantment.getForm());

                    enchantment.addMagicEffect(createDescriptionMagicEffect(record));

                    RecordHandler.inst().addToPatch(record.get());
                    RecordHandler.inst().addToPatch(enchantment);
                }
                
                record.setDescription("");
                
                RecordHandler.inst().addToPatch(record.get());
            }
        }
        else {
            if (!templateRecordLists.containsKey(record.getTemplate())) {
                FLST requirements = new FLST("TmpUsers" + RecordHandler.inst().get(record.getTemplate()).getEDID());
                
                EnchantableObject templateRecord = record.getTemplateRecord();
                
                COBJ recipe = new COBJ("RmEnch" + templateRecord.getEditorID());
                recipe.addIngredient(SoulGems.SoulGemBlackFilled, 1);
                if (templateRecord.getValue() > 0) {
                    recipe.addIngredient(new FormID("F", "Skyrim.esm") /* Gold */, templateRecord.getValue());
                }
                Condition c = new Condition(P_FormID.GetItemCount, requirements.getForm());
                c.setOperator(Condition.Operator.GreaterThan);
                c.setValue(0);
                c.setRunOnType(Condition.RunOnType.Subject);
                recipe.addCondition(c);
                recipe.setResultFormID(templateRecord.getFormID());
                recipe.setBenchKeywordFormID(RecordHandler.inst().getFormID("CraftingArcaneFont"));
                recipe.setOutputQuantity(1);
                
                templateRecordLists.put(record.getTemplate(), requirements);
            }
            templateRecordLists.get(record.getTemplate()).addFormEntry(record.getFormID());
        }
    }
    
    public FormID getRecordDuplicate(FormID formID) {
        return duplicatedRecords.get(formID);
    }
}
