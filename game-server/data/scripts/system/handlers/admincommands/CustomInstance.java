package admincommands;

import com.aionemu.gameserver.custom.instance.CustomInstanceRankEnum;
import com.aionemu.gameserver.custom.instance.CustomInstanceService;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author Estrayl
 */
public class CustomInstance extends AdminCommand {

	public CustomInstance() {
		super("cinstance", "Utility command for the custom instance.");

		// @formatter:off
		setSyntaxInfo(
			"<removecd> - Removes the custom instance cooldown of selected player.",
			"<getrank> - Gets the current custom instance rank of selected player.",
			"<setrank> [newRank] - Changes the custom instance rank of selected player to given value."
		);
		// @formatter:on
	}

	@Override
	protected void execute(Player player, String... params) {
		if (params.length < 1) {
			sendInfo(player);
			return;
		}

		switch (params[0].toLowerCase()) {
			case "removecd":
				if (player.getTarget() instanceof Player) {
					if (CustomInstanceService.getInstance().updateLastEntry(player.getObjectId(), 0))
						PacketSendUtility.sendMessage(player, "Successfully removed custom instance cooldown for " + player.getTarget().getName());
				} else {
					PacketSendUtility.sendMessage(player, "Please select a player first.");
				}
				break;
			case "getrank":
				if (player.getTarget() instanceof Player) {
					int rank = CustomInstanceService.getInstance().getPlayerRankObject(player.getTarget().getObjectId()).getRank();
					PacketSendUtility.sendMessage(player,
						player.getTarget().getName() + "'s current rank is " + CustomInstanceRankEnum.getRankDescription(rank) + "(" + rank + ").");
				} else {
					PacketSendUtility.sendMessage(player, "Please select a player first.");
				}
				break;
			case "setrank":
				if (params.length < 2) {
					sendInfo(player);
					return;
				}
				setNewRank(player, params[1]);
				break;
			case "getentries":
				if (player.getTarget() instanceof Player)
					PacketSendUtility.sendMessage(player, CustomInstanceService.getInstance().getPlayerModelEntries(player.getTarget().getObjectId()).size()
						+ " entries are currently cached for player " + player.getTarget().getName() + ".");
				break;
		}
	}

	private void setNewRank(Player player, String newRank) {
		int rank;
		try {
			rank = Integer.parseInt(newRank);
		} catch (NumberFormatException e) {
			sendInfo(player, "The new rank have to be a number.");
			return;
		}
		VisibleObject target = player.getTarget();
		if (player.getTarget() instanceof Player) {
			CustomInstanceService.getInstance().changePlayerRank(player.getTarget().getObjectId(), rank);
			PacketSendUtility.sendMessage(player,
				"Changed " + target.getName() + " to " + rank + " which is equivalent to " + CustomInstanceRankEnum.getRankDescription(rank));
		} else {
			sendInfo(player, "Select a player atleast.");
		}

	}
}
