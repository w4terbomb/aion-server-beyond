package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CHAT_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.ChatUtil;
import com.aionemu.gameserver.world.World;

/**
 * @author prix
 * @modified Neon
 */
public class CM_CHAT_PLAYER_INFO extends AionClientPacket {

	private String playerName;

	public CM_CHAT_PLAYER_INFO(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		playerName = readS();
	}

	@Override
	protected void runImpl() {
		Player target = World.getInstance().findPlayer(ChatUtil.getRealAdminName(playerName));
		if (target == null) {
			sendPacket(SM_SYSTEM_MESSAGE.STR_NO_SUCH_USER(playerName));
			return;
		}
		if (!getConnection().getActivePlayer().getKnownList().knowns(target))
			sendPacket(new SM_CHAT_WINDOW(target, false));
	}
}
