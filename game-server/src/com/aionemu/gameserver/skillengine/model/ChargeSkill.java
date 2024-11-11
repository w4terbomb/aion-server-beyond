package com.aionemu.gameserver.skillengine.model;

import com.aionemu.gameserver.configs.main.SecurityConfig;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.properties.Properties.CastState;

/**
 * @author Cheatkiller
 */
public class ChargeSkill extends Skill {

	private final int motionId;

	public ChargeSkill(SkillTemplate skillTemplate, Player effector, int skillLevel, int motionId, Creature firstTarget) {
		super(skillTemplate, effector, skillLevel, firstTarget, null);
		this.motionId = motionId;
	}

	public int getMotionId() {
		return motionId;
	}

	@Override
	public boolean useSkill() {
		if (!canUseSkill(CastState.CAST_END)) {
			effector.getController().cancelCurrentSkill(null);
			return false;
		}
		effector.getObserveController().notifyBoostSkillCostObservers(this);
		effector.getObserveController().notifyStartSkillCastObservers(this);
		effector.setCasting(this);
		effector.getObserveController().attach(moveListener);
		// motion boost state from the charge starting time must not get lost
		if (effector instanceof Player player && player.isHitTimeBoosted(player.getCastingSkill().getCastStartTime()))
			player.setHitTimeBoost(System.currentTimeMillis() + 100, player.getHitTimeBoostCastSpeed());
		updateCastDurationAndSpeed();
		updateHitTime(SecurityConfig.CHECK_ANIMATIONS);
		endCast();
		return true;
	}
}
