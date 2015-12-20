package com.aionemu.gameserver.ai2.manager;

import java.util.Collections;
import java.util.List;

import com.aionemu.gameserver.ai2.AI2Logger;
import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.skill.NpcSkillEntry;
import com.aionemu.gameserver.model.skill.NpcSkillList;
import com.aionemu.gameserver.model.templates.npcskill.NpcSkillTemplate;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.skillengine.model.SkillType;
import com.aionemu.gameserver.skillengine.properties.FirstTargetAttribute;
import com.aionemu.gameserver.skillengine.properties.Properties;
import com.aionemu.gameserver.skillengine.properties.TargetRangeAttribute;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 * @reworked Yeats
 */
public class SkillAttackManager {

	/**
	 * @param npcAI
	 * @param delay
	 */
	public static void performAttack(NpcAI2 npcAI, int delay) {
		if (npcAI.getOwner().getObjectTemplate().getAttackRange() == 0) {
			if (npcAI.getOwner().getTarget() != null
				&& !MathUtil.isInRange(npcAI.getOwner(), npcAI.getOwner().getTarget(), npcAI.getOwner().getAggroRange())) {
				npcAI.onGeneralEvent(AIEventType.TARGET_TOOFAR);
				npcAI.getOwner().getController().abortCast();
				return;
			}
		}
		if (npcAI.setSubStateIfNot(AISubState.CAST)) {
			if (delay > 0) {
				ThreadPoolManager.getInstance().schedule(new SkillAction(npcAI), delay);
			} else {
				skillAction(npcAI);
			}
		}
	}

	/**
	 * @param npcAI
	 */
	protected static void skillAction(NpcAI2 npcAI) {
		Creature target = (Creature) npcAI.getOwner().getTarget();
		if (npcAI.getOwner().getObjectTemplate().getAttackRange() == 0) {
			if (npcAI.getOwner().getTarget() != null
				&& !MathUtil.isInRange(npcAI.getOwner(), npcAI.getOwner().getTarget(), npcAI.getOwner().getAggroRange())) {
				npcAI.onGeneralEvent(AIEventType.TARGET_TOOFAR);
				npcAI.getOwner().getController().abortCast();
				return;
			}
		}
		if (target != null && !target.getLifeStats().isAlreadyDead()) {
			final int skillId = npcAI.getSkillId();
			final int skillLevel = npcAI.getSkillLevel();

			SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
			if (npcAI.isLogging()) {
				AI2Logger.info(npcAI, "Using skill " + skillId + " level: " + skillLevel + " duration: " + template.getDuration());
			}
			switch (template.getSubType()) {
				case BUFF:
					switch (template.getProperties().getFirstTarget()) {
						case ME:
							if (npcAI.getOwner().getEffectController().isAbnormalPresentBySkillId(skillId)) {
								afterUseSkill(npcAI);
								return;
							}
							break;
						default:
							if (target.getEffectController().isAbnormalPresentBySkillId(skillId)) {
								afterUseSkill(npcAI);
								return;
							}
					}
					break;
			}
			if ((template.getType() == SkillType.MAGICAL && npcAI.getOwner().getEffectController().isAbnormalSet(AbnormalState.SILENCE))
				|| (template.getType() == SkillType.PHYSICAL && npcAI.getOwner().getEffectController().isAbnormalSet(AbnormalState.BIND))
				|| (npcAI.getOwner().getEffectController().isInAnyAbnormalState(AbnormalState.CANT_ATTACK_STATE))
				|| (npcAI.getOwner().getTransformModel().isActive() && npcAI.getOwner().getTransformModel().getBanUseSkills() == 1)) {
				afterUseSkill(npcAI);
			} else {
				if (template.getProperties().getFirstTarget() == FirstTargetAttribute.ME) {
					npcAI.getOwner().setTarget(npcAI.getOwner());
				} else {
					NpcSkillEntry lastSkill = npcAI.getOwner().getGameStats().getLastSkill();
					if (lastSkill != null) {
						NpcSkillTemplate temp = lastSkill.getTemplate();
						if (temp != null) {
							switch (temp.getTarget()) {
								case ME:
									if(!npcAI.getOwner().getTarget().equals(npcAI.getOwner())) {
										npcAI.getOwner().setTarget(npcAI.getOwner());
									}
									break;
								case MOST_HATED:
									Creature target2 = npcAI.getOwner().getAggroList().getMostHated();
									if (target2 != null) {
										if (!npcAI.getOwner().getTarget().equals(target2)) {
											npcAI.getOwner().setTarget(target2);
										}
									}
									break;
									default:
										break;
							}
						}
					}
				}
				boolean success = npcAI.getOwner().getController().useSkill(skillId, skillLevel);
				if (!success) {
					afterUseSkill(npcAI);
				}
			}
		} else {
			npcAI.setSubStateIfNot(AISubState.NONE);
			npcAI.onGeneralEvent(AIEventType.TARGET_GIVEUP);
		}

	}

	/**
	 * @param npcAI
	 */
	public static void afterUseSkill(NpcAI2 npcAI) {
		npcAI.setSubStateIfNot(AISubState.NONE);
		npcAI.onGeneralEvent(AIEventType.ATTACK_COMPLETE);
	}

	/**
	 * @param npcAI
	 * @return
	 */
	public static NpcSkillEntry chooseNextSkill(NpcAI2 npcAI) {
		if (npcAI.isInSubState(AISubState.CAST)) {
			return null;
		}

		Npc owner = npcAI.getOwner();
		NpcSkillList skillList = owner.getSkillList();
		if (skillList == null || skillList.size() == 0) {
			return null;
		}

		if (owner.getGameStats().canUseNextSkill()) {
			NpcSkillEntry lastSkill = owner.getGameStats().getLastSkill() ;
			if (lastSkill != null && lastSkill.hasChain() && lastSkill.canUseNextChain(owner)) {
				List<NpcSkillEntry> chainSkills = skillList.getChainSkills(lastSkill);
				if (chainSkills != null && !chainSkills.isEmpty()) {
					if (chainSkills.size() > 1)
						Collections.shuffle(chainSkills);
					for (NpcSkillEntry entry : chainSkills) {
						if (entry != null && isReady(owner, entry)) {
							if (targetTooFar(owner, entry)) {
								owner.getGameStats().setNextSkillTime(5000);
								return null;
							}
							owner.getGameStats().setLastSkill(entry);
							owner.getGameStats().setNextSkillTime(entry.getNextSkillTime());
							entry.setLastTimeUsed();
							return entry;
						}
					}
				}
			}
			
			List<Integer> priorities = skillList.getPriorities();
			if (priorities != null && !priorities.isEmpty()) {
				
				for (int index = priorities.size()-1; index >= 0; index--) {
					List<NpcSkillEntry> skillsByPriority = skillList.getSkillsByPriority(priorities.get(index));
					if (skillsByPriority != null && !skillsByPriority.isEmpty()) {
						if (skillsByPriority.size() > 2)
							Collections.shuffle(skillsByPriority);
						
						for (NpcSkillEntry entry : skillsByPriority) {
							
							if (entry != null && entry.getChainId() == 0 && isReady(owner, entry)) {
								if (targetTooFar(owner, entry)) {
									owner.getGameStats().setNextSkillTime(5000);
									return null;
								}
								owner.getGameStats().setLastSkill(entry);
								owner.getGameStats().setNextSkillTime(entry.getNextSkillTime());
								entry.setLastTimeUsed();
								return entry;
							}
							
						}
					}
				}
	
			}
		}
		return null;
	}
	
	//Check for Bind/Silence/Fear/stun etc debuffs on npc
	private static boolean isReady(Npc owner, NpcSkillEntry entry) {
		if (entry.isReady(owner.getLifeStats().getHpPercentage(), System.currentTimeMillis() - owner.getGameStats().getFightStartingTime())) {
			if (entry.conditionReady(owner)) {
				SkillTemplate template = entry.getSkillTemplate();
				if ((template.getType() == SkillType.MAGICAL && owner.getEffectController().isAbnormalSet(AbnormalState.SILENCE))
					|| (template.getType() == SkillType.PHYSICAL && owner.getEffectController().isAbnormalSet(AbnormalState.BIND))
					|| (owner.getEffectController().isInAnyAbnormalState(AbnormalState.CANT_ATTACK_STATE))
					|| (owner.getTransformModel().isActive() && owner.getTransformModel().getBanUseSkills() == 1)) {
					return false;
				} else {
					return true;
				}
			}
		}
		return false;
	}
	
	private static boolean targetTooFar(Npc owner, NpcSkillEntry entry) {
		if (owner.getTarget() != null) {
			if (((Creature)owner.getTarget()).getLifeStats().isAlreadyDead() || !owner.canSee((Creature)owner.getTarget())) {
				return true;
			}
			SkillTemplate template = entry.getSkillTemplate();
			Properties prop = template.getProperties();
			if (prop.getFirstTarget() != FirstTargetAttribute.ME && prop.getTargetType() != TargetRangeAttribute.AREA) {
				float collision = owner.getObjectTemplate().getBoundRadius().getCollision() < 1f ? 1f : owner.getObjectTemplate().getBoundRadius().getCollision();
				if (!MathUtil.isIn3dRange(owner, owner.getTarget(), prop.getFirstTargetRange() + collision)) {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}
	
	private final static class SkillAction implements Runnable {

		private NpcAI2 npcAI;

		SkillAction(NpcAI2 npcAI) {
			this.npcAI = npcAI;
		}

		@Override
		public void run() {
			skillAction(npcAI);
			npcAI = null;
		}
	}
}
