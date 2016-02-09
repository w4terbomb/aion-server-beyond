package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.antihack.AntiHackService;

public class CM_GAMEGUARD extends AionClientPacket {

	private int size;

	public CM_GAMEGUARD(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		size = readD();
		readB(size);

	}

	@Override
	protected void runImpl() {
		AntiHackService.checkAionBin(size, getConnection());
	}
}
