package enchantingtweaks;

import enchantingtweaks.data.Records;
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
import enchantingtweaks.taskmodules.*;

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
        GRUP_TYPE.COBJ,
	GRUP_TYPE.ENCH,
        GRUP_TYPE.FLST,
        GRUP_TYPE.GLOB,
	GRUP_TYPE.MGEF,
        GRUP_TYPE.NPC_,
        GRUP_TYPE.PERK,
        GRUP_TYPE.RACE,
	GRUP_TYPE.WEAP,
    };
    public static String myPatchName = "EnchantingTweaks";
    public static String myPatchModName = "EnchantingTweaks-EnchantmentRemoval";
    public static String authorName = "Zebrina";
    public static String version = "1.0";
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
        /*
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
        */
        
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
	return new GRUP_TYPE[] {
            GRUP_TYPE.COBJ,
        };
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
        out.setDescription(descriptionToShowInSUM);
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
	return true;
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
        NPC_ player = Records.db().get("Player");
        
        player.addPerk(new FormID("01C2D5", "EnchantingTweaks.esp"), 1);
        
        Records.db().addRecordToPatch(player);
        
        // Manual fixes for some scripts which add perks and abilities
        
        ResolveScriptedRecord scriptResolver = new ResolveScriptedRecord();
        
        
        // -----------------------------------------------------------
        
        MakeEnchantmentRemovalConstructibleObject enchantmentRemovalEnabler = new MakeEnchantmentRemovalConstructibleObject();
        for (WEAP weapon : Records.db().getMergedMod().getWeapons()) {
            enchantmentRemovalEnabler.process(weapon);
        }
        for (ARMO armor : Records.db().getMergedMod().getArmors()) {
            enchantmentRemovalEnabler.process(armor);
        }
        
        AttachUnenchantedRecordScript unenchantedScriptAttacher = new AttachUnenchantedRecordScript();
        for (COBJ cobj : Records.db().getPatchMod().getConstructibleObjects()) {
            unenchantedScriptAttacher.process(cobj);
        }
        
        MakeUnenchantedTemperingConstructibleObject makeTempering = new MakeUnenchantedTemperingConstructibleObject();
        for (COBJ cobj : Records.db().getMergedMod().getConstructibleObjects()) {
            if (makeTempering.isConstructibleObjectTempering(cobj) && unenchantedScriptAttacher.getScriptAttachedEnchantedRecords().containsKey(cobj.getResultFormID())) {
                makeTempering.process(cobj, unenchantedScriptAttacher.getScriptAttachedEnchantedRecords().get(cobj.getResultFormID()));
            }
        }
        
        // Let the master mod know this patch exists.
        GLOB patchLoaded = Records.db().get("__EnchTw_SkyProcPatchLoaded");
        patchLoaded.setValue(true);
        Records.db().addRecordToPatch(patchLoaded);
        
        // Store the minute (since 1970) this patch was created.
        GLOB patchDate = Records.db().get("__EnchTw_SkyProcPatchDate");
        patchDate.setValue((float)(System.currentTimeMillis() / (60 * 1000)));
        Records.db().addRecordToPatch(patchDate);
    }
}
