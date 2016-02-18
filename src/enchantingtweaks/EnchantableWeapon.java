/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enchantingtweaks;

import enchantingtweaks.data.RecordHandler;
import enchantingtweaks.exceptions.RecordNullException;
import skyproc.FormID;
import skyproc.KeywordSet;
import skyproc.MajorRecord;
import skyproc.ScriptRef;
import skyproc.WEAP;

/**
 *
 * @author Sabrina
 */
public class EnchantableWeapon extends EnchantableObjectBase{
    private final WEAP weapon;

    EnchantableWeapon(WEAP weapon) throws RecordNullException {
        if (weapon == null) {
            throw new RecordNullException();
        }
        
        this.weapon = weapon;
    }

    @Override
    public MajorRecord get() {
        return weapon;
    }
    @Override
    public FormID getFormID() {
        return weapon.getForm();
    }
    @Override
    public String getEditorID() {
        return weapon.getEDID();
    }
    @Override
    public String getName() {
        return weapon.getName();
    }
    @Override
    public String getDescription() {
        return weapon.getDescription() == null || weapon.getDescription().isEmpty() || weapon.getDescription().equalsIgnoreCase("<NO TEXT>") ? "" : weapon.getDescription();
    }
    @Override
    public void setDescription(String description) {
        weapon.setDescription(description);
    }
    @Override
    public int getValue() {
        return weapon.getValue();
    }
    @Override
    public KeywordSet getKeywords() {
        return weapon.getKeywordSet();
    }
    @Override
    public FormID getEnchantment() {
        return weapon.getEnchantment();
    }
    @Override
    public void setEnchantment(FormID enchantment) {
        weapon.setEnchantment(enchantment);
    }
    @Override
    public FormID getTemplate() {
        return weapon.getTemplate();
    }
    @Override
    public EnchantableObject getTemplateRecord() throws Exception {
        return new EnchantableWeapon(RecordHandler.inst().get(weapon.getTemplate()));
    }
    @Override
    public EnchantableObject getCopy() throws Exception {
        WEAP newWeapon = RecordHandler.inst().getCopyWithSuffix(weapon.getForm(), "NoEnch");

        newWeapon.setEnchantment(FormID.NULL);
        newWeapon.setEnchantmentCharge(0);
        newWeapon.setDescription("");
        newWeapon.getKeywordSet().removeKeywordRef(RecordHandler.inst().getFormID("MagicDisallowEnchanting"));
        
        /*
        ScriptRef script = new ScriptRef("UnenchantedObjectScript");
        script.setProperty("EnchantedForm", weapon.getForm());
        
        newWeapon.getScriptPackage().addScript(script);
        */

        return new EnchantableWeapon(newWeapon);
    }
}
