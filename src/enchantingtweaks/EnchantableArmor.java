/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enchantingtweaks;

import enchantingtweaks.data.RecordHandler;
import enchantingtweaks.exceptions.RecordNullException;
import skyproc.ARMO;
import skyproc.FormID;
import skyproc.KeywordSet;
import skyproc.MajorRecord;
import skyproc.ScriptRef;

/**
 *
 * @author Sabrina
 */
public class EnchantableArmor extends EnchantableObjectBase {
    private final ARMO armor;

    EnchantableArmor(ARMO armor) throws RecordNullException {
        if (armor == null) {
            throw new RecordNullException();
        }
        
        this.armor = armor;
    }

    @Override
    public MajorRecord get() {
        return armor;
    }
    @Override
    public FormID getFormID() {
        return armor.getForm();
    }
    @Override
    public String getEditorID() {
        return armor.getEDID();
    }
    @Override
    public String getName() {
        return armor.getName();
    }
    @Override
    public String getDescription() {
        return armor.getDescription() == null || armor.getDescription().isEmpty() || armor.getDescription().equalsIgnoreCase("<NO TEXT>") ? "" : armor.getDescription();
    }
    @Override
    public void setDescription(String description) {
        armor.setDescription(description);
    }
    @Override
    public int getValue() {
        return armor.getValue();
    }
    @Override
    public KeywordSet getKeywords() {
        return armor.getKeywordSet();
    }
    @Override
    public FormID getEnchantment() {
        return armor.getEnchantment();
    }
    @Override
    public void setEnchantment(FormID enchantment) {
        armor.setEnchantment(enchantment);
    }
    @Override
    public FormID getTemplate() {
        return armor.getTemplate();
    }
    @Override
    public EnchantableObject getTemplateRecord() throws Exception {
        return new EnchantableArmor(RecordHandler.inst().get(armor.getTemplate()));
    }
    @Override
    public EnchantableObject getCopy() throws Exception {
        ARMO newArmor = RecordHandler.inst().getCopyWithSuffix(armor.getForm(), "NoEnch");

        newArmor.setEnchantment(FormID.NULL);
        newArmor.setDescription("");
        newArmor.getKeywordSet().removeKeywordRef(RecordHandler.inst().getFormID("MagicDisallowEnchanting"));
        
        /*
        ScriptRef script = new ScriptRef("UnenchantedObjectScript");
        script.setProperty("EnchantedForm", armor.getForm());
        
        newArmor.getScriptPackage().addScript(script);
        */

        return new EnchantableArmor(newArmor);
    }
}
