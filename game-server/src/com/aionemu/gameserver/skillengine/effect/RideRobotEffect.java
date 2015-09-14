package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.enums.EquipType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_RIDE_ROBOT;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Rolandas, Cheatkiller
 */
public class RideRobotEffect extends EffectTemplate {

	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
	}

	@Override
	public void startEffect(final Effect effect) {
		Player player = (Player) effect.getEffected();
		final Item key = player.getEquipment().getMainHandWeapon();
		ItemTemplate template = DataManager.ITEM_DATA.getItemTemplate(key.getItemSkinTemplate().getTemplateId());
		PacketSendUtility.broadcastPacketAndReceive(player, new SM_RIDE_ROBOT(player.getObjectId(), template.getRobotId()));
		player.setRobotId(template.getRobotId());

		ActionObserver observer = new ActionObserver(ObserverType.UNEQUIP) {

			@Override
			public void unequip(Item item, Player owner) {
				if (item.getEquipmentType() == EquipType.WEAPON) {
					effect.endEffect();
				}
			}
		};
		player.getObserveController().addObserver(observer);
		effect.setActionObserver(observer, position);
	}

	@Override
	public void endEffect(Effect effect) {
		Player player = (Player) effect.getEffected();
		player.setRobotId(0);
		PacketSendUtility.broadcastPacketAndReceive(player, new SM_RIDE_ROBOT(player.getObjectId(), player.getRobotId()));
		for (Effect ef : player.getEffectController().getAbnormalEffects()) {
			if (ef.getSkillTemplate().getRideRobotCondition() != null)
				ef.endEffect();
		}
		ActionObserver observer = effect.getActionObserver(position);
		if (observer != null)
			effect.getEffected().getObserveController().removeObserver(observer);
	}
}
