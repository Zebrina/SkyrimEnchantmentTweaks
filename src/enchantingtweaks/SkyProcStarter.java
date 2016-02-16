package enchantingtweaks;

import enchantingtweaks.data.RecordHandler;
import java.awt.Color;
import java.awt.Font;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import lev.gui.LSaveFile;
import skyproc.*;
import skyproc.gui.SPMainMenuPanel;
import skyproc.gui.SUM;
import skyproc.gui.SUMGUI;
import enchantingtweaks.YourSaveFile.Settings;
import enchantingtweaks.exceptions.RecordNotFoundException;
import java.util.Arrays;

/**
 *
 * @author Your Name Here
 */
public class SkyProcStarter implements SUM {

    /*
     * The important functions to change are:
     * - getStandardMenu(), where you set up the GUI
     * - runChangesToPatch(), where you put all the processing code and add records to the output patch.
     */

    /*
     * The types of records you want your patcher to import. Change this to
     * customize the import to what you need.
     */
    GRUP_TYPE[] importRequests = new GRUP_TYPE[]{
	GRUP_TYPE.ARMO,
	GRUP_TYPE.ENCH,
        GRUP_TYPE.FLST,
        GRUP_TYPE.KYWD,
	GRUP_TYPE.MGEF,
	GRUP_TYPE.WEAP,
    };
    public static String myPatchName = "EnchantingTweaks";
    public static String myPatchModName = "EnchantingTweaks-EnchantmentRemoval";
    public static String authorName = "Zebrina";
    public static String version = "0.5";
    public static String welcomeText = "Pick a setting, any setting.";
    public static String descriptionToShowInSUM = "Required by EnchantingTweaks for enchantment removal.";
    public static Color headerColor = new Color(0x9F81F7);  // Purple
    public static Color settingsColor = new Color(0x9F81F7);  // Purple
    public static Font settingsFont = new Font("Serif", Font.BOLD, 15);
    public static SkyProcSave save = new YourSaveFile();

    // Do not write the bulk of your program here
    // Instead, write your patch changes in the "runChangesToPatch" function
    // at the bottom
    public static void main(String[] args) {
        // Ugly fix to prevent boss from running
        boolean noBoss = false;
        for (String arg : args) {
            if (arg.equalsIgnoreCase("-noboss")) {
                noBoss = true;
                break;
            }
        }
        if (!noBoss) {
            args = Arrays.copyOf(args, args.length + 1);
            args[args.length - 1] = "-noboss";
        }
        
	try {
	    SPGlobal.createGlobalLog();
	    SUMGUI.open(new SkyProcStarter(), args);
	} catch (Exception e) {
	    // If a major error happens, print it everywhere and display a message box.
	    System.err.println(e.toString());
	    SPGlobal.logException(e);
	    JOptionPane.showMessageDialog(null, "There was an exception thrown during program execution: '" + e + "'  Check the debug logs or contact the author.");
	    SPGlobal.closeDebug();
	}
    }

    @Override
    public String getName() {
	return myPatchName;
    }

    // This function labels any record types that you "multiply".
    // For example, if you took all the armors in a mod list and made 3 copies,
    // you would put ARMO here.
    // This is to help monitor/prevent issues where multiple SkyProc patchers
    // multiply the same record type to yeild a huge number of records.
    @Override
    public GRUP_TYPE[] dangerousRecordReport() {
	// None
	return new GRUP_TYPE[0];
    }

    @Override
    public GRUP_TYPE[] importRequests() {
	return importRequests;
    }

    @Override
    public boolean importAtStart() {
	return false;
    }

    @Override
    public boolean hasStandardMenu() {
	return true;
    }

    // This is where you add panels to the main menu.
    // First create custom panel classes (as shown by YourFirstSettingsPanel),
    // Then add them here.
    @Override
    public SPMainMenuPanel getStandardMenu() {
	SPMainMenuPanel settingsMenu = new SPMainMenuPanel(getHeaderColor());

	settingsMenu.setWelcomePanel(new WelcomePanel(settingsMenu));
	settingsMenu.addMenu(new OtherSettingsPanel(settingsMenu), false, save, Settings.OTHER_SETTINGS);

	return settingsMenu;
    }

    // Usually false unless you want to make your own GUI
    @Override
    public boolean hasCustomMenu() {
	return false;
    }

    @Override
    public JFrame openCustomMenu() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasLogo() {
	return false;
    }

    @Override
    public URL getLogo() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasSave() {
	return true;
    }

    @Override
    public LSaveFile getSave() {
	return save;
    }

    @Override
    public String getVersion() {
	return version;
    }

    @Override
    public ModListing getListing() {
	return new ModListing(myPatchModName, false);
    }

    @Override
    public Mod getExportPatch() {
	Mod out = new Mod(getListing());
	out.setAuthor(authorName);
	return out;
    }

    @Override
    public Color getHeaderColor() {
	return headerColor;
    }

    // Add any custom checks to determine if a patch is needed.
    // On Automatic Variants, this function would check if any new packages were
    // added or removed.
    @Override
    public boolean needsPatching() {
	return false;
    }

    // This function runs when the program opens to "set things up"
    // It runs right after the save file is loaded, and before the GUI is displayed
    @Override
    public void onStart() throws Exception {
    }

    // This function runs right as the program is about to close.
    @Override
    public void onExit(boolean patchWasGenerated) throws Exception {
    }

    // Add any mods that you REQUIRE to be present in order to patch.
    @Override
    public ArrayList<ModListing> requiredMods() {
        ArrayList<ModListing> requiredMods = new ArrayList<>();
        
        requiredMods.add(new ModListing("EnchantingTweaks.esp"));
        
	return requiredMods;
    }

    @Override
    public String description() {
	return descriptionToShowInSUM;
    }

    // This is where you should write the bulk of your code.
    // Write the changes you would like to make to the patch,
    // but DO NOT export it.  Exporting is handled internally.
    @Override
    public void runChangesToPatch() throws Exception {

	Mod patch = SPGlobal.getGlobalPatch();

	Mod merger = new Mod(getName() + "Merger", false);
	merger.addAsOverrides(SPGlobal.getDB());

	// Write your changes to the patch here.
        
        RecordHandler.inst().initialize(merger, patch);
        
        EnchantableObjectProcessor<EnchantableWeapon> weaponProcessor = new EnchantableObjectProcessor<>(merger, patch);
        if (weaponProcessor == null) {
            SPGlobal.logError("EnchantableObjectProcessor", "weaponProcessor invalid.");
            throw new Exception();
        }
        for (WEAP weapon : merger.getWeapons()) {
            if (weapon.get(MajorRecord.MajorFlags.NonPlayable) || weapon.get(WEAP.WeaponFlag.NonPlayable)) {
                SPGlobal.log("WEAP", "Ignoring non-playable weapon [" + weapon.getEDID() + "]");
            }
            else if (weapon.getEnchantment() == null || weapon.getEnchantment().isNull()) {
                SPGlobal.log("WEAP", "Ignoring unenchanted weapon [" + weapon.getEDID() + "]");
            }
            else if (weapon.getWeight() == 0.0) {
                SPGlobal.log("WEAP", "Ignoring weightless weapon [" + weapon.getEDID() + "]");
            }
            else {
                weaponProcessor.processRecord(new EnchantableWeapon(weapon), true);
            }
        }
        
        EnchantableObjectProcessor<EnchantableArmor> armorProcessor = new EnchantableObjectProcessor<>(merger, patch);
        if (armorProcessor == null) {
            SPGlobal.logError("EnchantableObjectProcessor", "armorProcessor invalid.");
            throw new Exception();
        }
        for (ARMO armor : merger.getArmors()) {
            if (armor.get(MajorRecord.MajorFlags.NonPlayable) || armor.getBodyTemplate().get(BodyTemplate.GeneralFlags.NonPlayable)) {
                SPGlobal.log("ARMO", "Ignoring non-playable armor [" + armor.getEDID() + "]");
            }
            else if (armor.getEnchantment() == null || armor.getEnchantment().isNull()) {
                SPGlobal.log("ARMO", "Ignoring unenchanted armor [" + armor.getEDID() + "]");
            }
            else if (armor.getWeight() == 0.0) {
                SPGlobal.log("ARMO", "Ignoring weightless armor [" + armor.getEDID() + "]");
            }
            else {
                armorProcessor.processRecord(new EnchantableArmor(armor), false);
            }
        }
        
        patch.setAuthor(authorName);
        patch.setDescription(descriptionToShowInSUM);
    }

    class EnchantableWeapon implements EnchantableObject {
        private final WEAP weapon;
        
        EnchantableWeapon(WEAP weapon) {
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
        public String getDescription() {
            return weapon.getDescription();
        }
        @Override
        public void setDescription(String description) {
            weapon.setDescription(description);
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
        public EnchantableObject copy() throws RecordNotFoundException {
            WEAP newWeapon = RecordHandler.inst().getCopyWithPrefix(weapon.getForm(), "NoEnch");
            
            newWeapon.setEnchantment(FormID.NULL);
            newWeapon.setEnchantmentCharge(0);
            newWeapon.setDescription("");
            newWeapon.getKeywordSet().removeKeywordRef(RecordHandler.inst().getFormID("MagicDisallowEnchanting"));
            
            return new EnchantableWeapon(newWeapon);
        }
    }
    
    class EnchantableArmor implements EnchantableObject {
        
        private final ARMO armor;
        
        EnchantableArmor(ARMO armor) {
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
        public String getDescription() {
            return armor.getDescription();
        }
        @Override
        public void setDescription(String description) {
            armor.setDescription(description);
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
        public EnchantableObject copy() throws RecordNotFoundException {
            ARMO newArmor = RecordHandler.inst().getCopyWithPrefix(armor.getForm(), "NoEnch");
            
            newArmor.setEnchantment(FormID.NULL);
            newArmor.setDescription("");
            newArmor.getKeywordSet().removeKeywordRef(RecordHandler.inst().getFormID("MagicDisallowEnchanting"));
            
            return new EnchantableArmor(newArmor);
        }
    }
}
