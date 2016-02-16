/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enchantingtweaks;

import enchantingtweaks.exceptions.RecordNotFoundException;
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
    public String getDescription();
    public void setDescription(String description);
    public KeywordSet getKeywords();
    public FormID getEnchantment();
    public void setEnchantment(FormID enchantment);
    public FormID getTemplate();
    public EnchantableObject copy() throws RecordNotFoundException;
}
