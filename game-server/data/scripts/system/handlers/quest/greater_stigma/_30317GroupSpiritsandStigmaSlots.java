package quest.greater_stigma;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Gigi
 */
public class _30317GroupSpiritsandStigmaSlots extends QuestHandler {

	private final static int questId = 30317;

	public _30317GroupSpiritsandStigmaSlots() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(799208).addOnQuestStart(questId);
		qe.registerQuestNpc(799208).addOnTalkEvent(questId);
		qe.registerQuestNpc(799506).addOnTalkEvent(questId);
		qe.registerQuestNpc(799322).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 799208) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 4762);
				else
					return sendQuestStartDialog(env);
			}
		}

		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);

		if (qs != null && qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 799322: {
					switch (env.getDialog()) {
						case QUEST_SELECT:
							return sendQuestDialog(env, 1011);
						case SETPRO1:
							QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 799506, player.getX(), player.getY(), player.getZ(),
								player.getHeading());
							qs.setQuestVarById(0, 1);
							updateQuestStatus(env);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
					}
				}
				case 799208:
					switch (env.getDialog()) {
						case QUEST_SELECT:
							long itemCount1 = player.getInventory().getItemCountByItemId(182209718);
							long itemCount2 = player.getInventory().getItemCountByItemId(182209719);
							if (var == 2) {
								if (itemCount1 > 0 && itemCount2 > 0) {
									removeQuestItem(env, 182209718, 1);
									removeQuestItem(env, 182209719, 1);
									qs.setStatus(QuestStatus.REWARD);
									updateQuestStatus(env);
									return sendQuestDialog(env, 1693);
								} else
									return sendQuestDialog(env, 10001);
							}
					}
				case 799506:
					switch (env.getDialog()) {
						case QUEST_SELECT:
							if (var == 1)
								return sendQuestDialog(env, 1352);
						case SETPRO2:
							env.getVisibleObject().getController().onDelete();
							qs.setQuestVarById(0, 2);
							updateQuestStatus(env);
							return true;
					}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 799208) {
				if (env.getDialogId() == DialogAction.CHECK_USER_HAS_QUEST_ITEM.id())
					return sendQuestDialog(env, 5);
				else
					return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}
