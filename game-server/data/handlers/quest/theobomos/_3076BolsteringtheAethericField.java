package quest.theobomos;

import static com.aionemu.gameserver.model.DialogAction.*;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.handlers.AbstractQuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Altaress
 */
public class _3076BolsteringtheAethericField extends AbstractQuestHandler {

	public _3076BolsteringtheAethericField() {
		super(3076);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(798155).addOnQuestStart(questId);
		qe.registerQuestNpc(798155).addOnTalkEvent(questId);
		qe.registerQuestNpc(278556).addOnTalkEvent(questId);
		qe.registerQuestNpc(278503).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (targetId == 798155) {
			if (qs == null || qs.isStartable()) {
				if (env.getDialogActionId() == QUEST_SELECT)
					return sendQuestDialog(env, 1011);
				else
					return sendQuestStartDialog(env);
			} else if (qs.getStatus() == QuestStatus.START) {
				if (env.getDialogActionId() == QUEST_SELECT)
					return sendQuestDialog(env, 2375);
				else if (env.getDialogActionId() == SELECT_QUEST_REWARD) {
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				} else
					return sendQuestEndDialog(env);
			} else if (qs.getStatus() == QuestStatus.REWARD) {
				return sendQuestEndDialog(env);
			}
		} else if (targetId == 278503) {
			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				if (env.getDialogActionId() == QUEST_SELECT)
					return sendQuestDialog(env, 1352);
				else if (env.getDialogActionId() == SETPRO1) {
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				} else
					return sendQuestStartDialog(env);
			}
		} else if (targetId == 278556) {
			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				if (env.getDialogActionId() == QUEST_SELECT)
					return sendQuestDialog(env, 1693);
				else if (env.getDialogActionId() == SETPRO2) {
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				} else
					return sendQuestStartDialog(env);
			}
		}
		return false;
	}
}
