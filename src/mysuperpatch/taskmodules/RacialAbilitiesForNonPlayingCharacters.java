/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mysuperpatch.taskmodules;

import mysuperpatch.data.Keywords;
import mysuperpatch.data.Records;
import skyproc.FormID;
import skyproc.RACE;
import skyproc.SPEL;
import skyproc.SPEL.SPELType;

/**
 *
 * @author Sabrina
 */
public class RacialAbilitiesForNonPlayingCharacters {
    public void process() throws Exception {
        for (RACE race : Records.db().getMergedMod().getRaces()) {
            if(race.getKeywordSet().getKeywordRefs().contains(Keywords.ActorTypeNPC)) {
                for (FormID abilityID : race.getSpells()) {
                    SPEL ability = Records.db().get(abilityID);
                    if (ability != null && (ability.getSpellType() == SPELType.Power || ability.getSpellType() == SPELType.Power)) {
                    }
                }
            }
        }
    }
}
