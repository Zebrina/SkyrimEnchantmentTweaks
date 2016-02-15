/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enchantingtweaks;

import data.RecordHandler;
import enchantingtweaks.exceptions.RecordCopyFailureException;
import enchantingtweaks.exceptions.RecordNotFoundException;
import java.util.HashMap;
import skyproc.*;
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
        ENCH enchantment = (ENCH)merger.getMajor(record.getEnchantment(), GRUP_TYPE.ENCH);
        if (enchantment == null) {
            throw new RecordNotFoundException("Enchantment not found", record.getEnchantment());
        }

        if (record.getTemplate() == null || record.getTemplate().isNull()) {
            T unenchantedRecord = (T)record.copy();

            // Remove enchantment.
            unenchantedRecord.setEnchantment(FormID.NULL);

            KYWD uniqueKeyword = RecordHandler.inst().getCopy("Keyword", "Unique", record.getEditorID());//(KYWD)patch.makeCopy(KeywordTemplate, "UniqueEnchantment_" + record.getEditorID());

            // Add unique keyword.
            unenchantedRecord.getKeywords().add(uniqueKeyword.getForm());

            FLST restrictions = RecordHandler.inst().getCopy("FormList", "WornRestrictions", record.getEditorID());

            restrictions.addFormEntry(uniqueKeyword.getForm());

            COBJ recipe = RecordHandler.inst().getCopy("CraftingRecipe", "RemoveEnchantment", record.getEditorID());

            //recipe.setResultFormID(record.getFormID());

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
            if (!templateObjects.containsKey(record.getTemplate())) {
                //COBJ recipe = (COBJ)patch.makeCopy(CraftingRemoveEnchantmentTemplate, "RemoveEnchantment" + edid);
                //if (recipe == null) {
                //    throw new RecordCopyFailureException("Failed to copy recipe", CraftingRemoveEnchantmentTemplate);
                //}

                //recipe.setResultFormID(record.getTemplate());

                FLST requirement = RecordHandler.inst().getCopy("FormList", "Template", RecordHandler.inst().get(record.getTemplate()).getEDID());
                if (requirement == null) {
                    throw new RecordCopyFailureException("Failed to copy formlist", RecordHandler.inst().get(record.getTemplate()));
                }

                //recipe.getConditions().get(0).setReference(requirement.getForm());
                //recipe.getConditions().add(new Condition());
                
                //patch.addRecord(recipe);
                
                templateObjects.put(record.getTemplate(), requirement);
            }
            templateObjects.get(record.getTemplate()).addFormEntry(record.getFormID());
        }
    }
}
