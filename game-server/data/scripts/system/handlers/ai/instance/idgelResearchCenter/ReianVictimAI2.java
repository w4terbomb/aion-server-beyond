package ai.instance.idgelResearchCenter;

import ai.AggressiveNpcAI2;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * @author Ritsu
 */
@AIName("reianvictim")
public class ReianVictimAI2 extends AggressiveNpcAI2 {

	@Override
	public int modifyDamage(Creature creature, int damage) {
		return 1;
	}

}
