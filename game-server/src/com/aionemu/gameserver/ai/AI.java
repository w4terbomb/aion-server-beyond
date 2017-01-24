package com.aionemu.gameserver.ai;

import com.aionemu.gameserver.ai.event.AIEventType;
import com.aionemu.gameserver.ai.poll.AIQuestion;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skill.NpcSkillEntry;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.templates.item.ItemAttackType;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * @author ATracer
 */
public interface AI {

	void onCreatureEvent(AIEventType event, Creature creature);

	void onCustomEvent(int eventId, Object... args);

	void onGeneralEvent(AIEventType event);

	/**
	 * If already handled dialog return true.
	 */
	boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex);

	void think();

	boolean canThink();

	AIState getState();

	AISubState getSubState();

	String getName();

	/**
	 * Ask AI instance for the answer to the specified question.
	 * 
	 * @param question
	 * @return The answer, true or false.
	 */
	boolean ask(AIQuestion question);

	boolean isLogging();

	/**
	 * @param attacker
	 * @param damage
	 *          - The calculated damage from given attacker
	 * @param effect
	 *          - The effect which caused the damage (may be null)
	 * @return The effectively received damage
	 */
	int modifyDamage(Creature attacker, int damage, Effect effect);

	/**
	 * @param damage
	 *          - The calculated damage output of this creature
	 * @return The effective damage output of this creature
	 */
	int modifyOwnerDamage(int damage);

	/**
	 * Used to manipulate any game stat of the owner.
	 * 
	 * @param stat
	 */
	void modifyOwnerStat(Stat2 stat);

	ItemAttackType modifyAttackType(ItemAttackType type);

	int modifyAggroRange(int value);

	void onStartUseSkill(NpcSkillEntry startingSkill);

	void onEndUseSkill(NpcSkillEntry usedSkill);
}