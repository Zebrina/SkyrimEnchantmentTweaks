/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enchantingtweaks;

import skyproc.FormID;
import skyproc.KeywordSet;
import skyproc.MajorRecord;

/**
 *
 * @author Sabrina
 */
public interface EnchantableObject {
    public MajorRecord get();
    public FormID getFormID();
    public String getEditorID();
    public String getName();
    public String getDescription();
    public void setDescription(String description);
    public int getValue();
    public KeywordSet getKeywords();
    public FormID getEnchantment();
    public FormID getBaseEnchantment() throws Exception;
    public void setEnchantment(FormID enchantment);
    public FormID getTemplate();
    public EnchantableObject getTemplateRecord() throws Exception;
    public EnchantableObject getCopy() throws Exception;
}
