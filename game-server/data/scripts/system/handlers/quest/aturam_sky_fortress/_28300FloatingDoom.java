package quest.aturam_sky_fortress;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author zhkchi
 * @modified Pad
 */
public class _28300FloatingDoom extends QuestHandler {

	private final static int questId = 28300;

	public _28300FloatingDoom() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(804719).addOnQuestStart(questId);
		qe.registerQuestNpc(804719).addOnTalkEvent(questId);
		qe.registerQuestNpc(804821).addOnTalkEvent(questId);
		qe.registerQuestNpc(799530).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 804719) {
				switch (dialog) {
					case QUEST_SELECT:
						return sendQuestDialog(env, 4762);
					default:
						return sendQuestStartDialog(env);
				}
			}
		} else if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
			if (targetId == 804821) {
				switch (dialog) {
					case QUEST_SELECT:
						return sendQuestDialog(env, 1011);
					case SELECT_ACTION_1013:
						return sendQuestDialog(env, 1013);
					case SETPRO1:
						qs.setQuestVarById(0, 1);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return closeDialogWindow(env);
				}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 799530) {
				switch (dialog) {
					case USE_OBJECT:
						return sendQuestDialog(env, 10002);
					case SELECT_QUEST_REWARD:
						return sendQuestDialog(env, 5);
					default:
						return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}

}
