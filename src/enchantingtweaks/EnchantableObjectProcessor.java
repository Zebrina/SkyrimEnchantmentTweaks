/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enchantingtweaks;

import enchantingtweaks.data.SoulGems;
import enchantingtweaks.data.RecordHandler;
import enchantingtweaks.exceptions.RecordCopyFailureException;
import enchantingtweaks.exceptions.RecordNotFoundException;
import java.util.HashMap;
import skyproc.*;
import skyproc.Condition.P_FormID;
import skyproc.genenums.CastType;
import skyproc.genenums.DeliveryType;

/**
 *
 * @author Sabrina
 * @param <T>
 */
public class EnchantableObjectProcessor<T extends EnchantableObject> {
    private final Mod merger;
    private final Mod patch;
    
    private final HashMap<FormID, FLST> templateObjects = new HashMap<>();
    
    public EnchantableObjectProcessor(Mod merger, Mod patch) throws RecordNotFoundException {
        this.merger = merger;
        this.patch = patch;
    }
    
    public void processRecord(T record, boolean isWeapon) throws RecordNotFoundException, RecordCopyFailureException {
        ENCH enchantment = RecordHandler.inst().get(record.getEnchantment());

        if (record.getTemplate() == null || record.getTemplate().isNull()) {
            T unenchantedRecord = (T)record.copy();

            /*
            // Remove enchantment.
            unenchantedRecord.setEnchantment(FormID.NULL);

            KYWD uniqueKeyword = new KYWD("UniqKW" + record.getEditorID());

            // Add unique keyword to.
            unenchantedRecord.getKeywords().addKeywordRef(uniqueKeyword.getForm());

            FLST restrictions = new FLST("WrnRes" + record.getEditorID());

            restrictions.addFormEntry(uniqueKeyword.getForm());
            */

            COBJ recipe = new COBJ("RmEnch" + record.getEditorID());
            recipe.addIngredient(SoulGems.SoulGemBlackFilled, 1);
            Condition c = new Condition(P_FormID.GetItemCount, record.getFormID());
            c.setOperator(Condition.Operator.GreaterThan);
            c.setValue(0);
            c.setRunOnType(Condition.RunOnType.Subject);
            recipe.addCondition(c);
            recipe.setResultFormID(unenchantedRecord.getFormID());
            recipe.setBenchKeywordFormID(RecordHandler.inst().getFormID("CraftingArcaneFont"));
            //recipe.setOutputQuantity(1);
            //patch.addRecord(recipe);

            //recipe.getConditions().get(0).setReference(record.getFormID());
            
            /*
            if (record.getDescription() != null && !record.getDescription().isEmpty()) {
                ENCH newEnchantment = (ENCH)patch.makeCopy(enchantment, enchantment.getEDID() + "Copy");
                if (newEnchantment == null) {
                    throw new RecordCopyFailureException("Failed to copy enchantment", enchantment);
                }
                patch.addRecord(newEnchantment);

                for (int i = 0; i < newEnchantment.getMagicEffects().size(); ++i) {
                    MagicEffectRef ref = newEnchantment.getMagicEffects().get(i);

                    if (ref.getMagicRef() != null && !ref.getMagicRef().isNull()) {
                        MGEF magicEffect = (MGEF)merger.getMajor(ref.getMagicRef(), GRUP_TYPE.MGEF);
                        if (magicEffect == null) {
                            throw new RecordNotFoundException("Magic effect not found", ref.getMagicRef());
                        }
                        else if (magicEffect.get(MGEF.SpellEffectFlag.HideInUI) == false) {
                            MGEF hiddenMagicEffect = (MGEF)patch.makeCopy(magicEffect, magicEffect.getEDID() + "Hidden");
                            if (hiddenMagicEffect == null) {
                                throw new RecordCopyFailureException("Failed to copy magic effect", magicEffect);
                            }
                            patch.addRecord(hiddenMagicEffect);

                            hiddenMagicEffect.set(MGEF.SpellEffectFlag.HideInUI, true);

                            ref.setMagicRef(hiddenMagicEffect.getForm());
                        }
                    }
                }

                MGEF dummyDescription = (MGEF)patch.makeCopy(DescriptionMagicEffectTemplate, "DummyDescription_" + record.getEditorID());
                if (dummyDescription == null) {
                    throw new RecordCopyFailureException("Failed to copy magic effect", dummyDescription);
                }

                if (isWeapon) {
                    dummyDescription.setCastType(CastType.FireAndForget);
                    dummyDescription.setDeliveryType(DeliveryType.Touch);
                }

                dummyDescription.setDescription(record.getDescription());

                //newEnchantment.getMagicEffects().add(new MagicEffectRef(dummyDescription.getForm()));
                
                patch.addRecord(dummyDescription);
            }
            else {
                //enchantment.setWornRestrictions(restrictions.getForm());
                
                //patch.addRecord(enchantment);
            }
            */
        }
        else {
            /*
            if (!templateObjects.containsKey(record.getTemplate())) {
                //COBJ recipe = (COBJ)patch.makeCopy(CraftingRemoveEnchantmentTemplate, "RemoveEnchantment" + edid);
                //if (recipe == null) {
                //    throw new RecordCopyFailureException("Failed to copy recipe", CraftingRemoveEnchantmentTemplate);
                //}

                //recipe.setResultFormID(record.getTemplate());

                FLST requirement = new FLST("TmpUsers" + RecordHandler.inst().get(record.getTemplate()).getEDID());

                //recipe.getConditions().get(0).setReference(requirement.getForm());
                //recipe.getConditions().add(new Condition());
                
                //patch.addRecord(recipe);
                
                templateObjects.put(record.getTemplate(), requirement);
            }
            templateObjects.get(record.getTemplate()).addFormEntry(record.getFormID());
*/
        }
    }
}
