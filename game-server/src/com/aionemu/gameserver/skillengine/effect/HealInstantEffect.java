package com.aionemu.gameserver.skillengine.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.HealType;

/**
 * @author ATracer
 * @modified vlog, Sippolo, kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HealInstantEffect")
public class HealInstantEffect extends AbstractHealEffect {

	@Override
	public void applyEffect(Effect effect) {
		super.applyEffect(effect, HealType.HP);
	}

	@Override
	public void calculate(Effect effect) {
		super.calculate(effect, HealType.HP);
	}

	@Override
	protected int getCurrentStatValue(Effect effect) {
		return effect.getEffected().getLifeStats().getCurrentHp();
	}

	@Override
	protected int getMaxStatValue(Effect effect) {
		return effect.getEffected().getGameStats().getMaxHp().getCurrent();
	}
}
