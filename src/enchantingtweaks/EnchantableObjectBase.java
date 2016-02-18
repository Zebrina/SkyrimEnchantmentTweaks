/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enchantingtweaks;

import enchantingtweaks.data.RecordHandler;
import skyproc.ENCH;
import skyproc.FormID;

/**
 *
 * @author Sabrina
 */
public abstract class EnchantableObjectBase implements EnchantableObject {
    @Override
    public FormID getBaseEnchantment() throws Exception {
        return getEnchantment().isNull() ? FormID.NULL : RecordHandler.inst().<ENCH>get(getEnchantment()).getBaseEnchantment();
    }
}
