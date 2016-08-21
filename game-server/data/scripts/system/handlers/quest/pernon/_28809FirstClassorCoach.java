package quest.pernon;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author Pad
 */
public class _28809FirstClassorCoach extends QuestHandler {

	private static final int questId = 28809;

	public _28809FirstClassorCoach() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(830169).addOnQuestStart(questId);
		qe.registerQuestNpc(830169).addOnTalkEvent(questId);
		qe.registerQuestNpc(830408).addOnTalkEvent(questId);
		qe.registerQuestNpc(830417).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 830169) {
				switch (dialog) {
					case QUEST_SELECT:
						return sendQuestDialog(env, 1011);
					case QUEST_ACCEPT_1:
					case QUEST_ACCEPT_SIMPLE:
						giveQuestItem(env, 190100013, 1);
						return sendQuestStartDialog(env);
				}
			}
		} else if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 830408:
					switch (dialog) {
						case QUEST_SELECT: {
							return sendQuestDialog(env, 1352);
						}
						case SETPRO1: {
							return defaultCloseDialog(env, 0, 1);
						}
					}
					return false;
				case 830417:
					switch (dialog) {
						case QUEST_SELECT: {
							return sendQuestDialog(env, 1693);
						}
						case SETPRO2: {
							return defaultCloseDialog(env, 1, 2);
						}
					}
					return false;
				case 830169:
					switch (dialog) {
						case QUEST_SELECT: {
							return sendQuestDialog(env, 2375);
						}
						case SELECT_QUEST_REWARD: {
							changeQuestStep(env, 2, 2, true);
							return sendQuestEndDialog(env);
						}
					}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 830169) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}
