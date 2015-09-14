package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.configs.main.AntiHackConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.ExchangeService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author -Avol-
 */

public class CM_EXCHANGE_CANCEL extends AionClientPacket {

	public CM_EXCHANGE_CANCEL(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		// 0 bytes
	}

	@Override
	protected void runImpl() {
		final Player activePlayer = getConnection().getActivePlayer();

		if (activePlayer.getPlayerAccount().isHacked() && !AntiHackConfig.HDD_SERIAL_HACKED_ACCOUNTS_ALLOW_EXCHANGE) {
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_L2AUTH_S_KICKED_DOUBLE_LOGIN);
			PacketSendUtility.sendMessage(activePlayer,
				"Account hacking attempt detected. You can't use this function. Please, contact your server support.");
			return;
		}

		ExchangeService.getInstance().cancelExchange(activePlayer);
	}
}
