package quest.beshmundir;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/**
 * @author vlog
 */
public class _30236NewGreatsword extends QuestHandler {

	private final static int questId = 30236;

	public _30236NewGreatsword() {
		super(questId);
	}

	@Override
	public void register() {
		int[] debilkarims = { 286904, 281419, 215795 };
		qe.registerQuestNpc(799032).addOnQuestStart(questId);
		qe.registerQuestNpc(799032).addOnTalkEvent(questId);
		qe.registerGetingItem(182209634, questId);
		for (int debilkarim : debilkarims) {
			qe.registerQuestNpc(debilkarim).addOnKillEvent(questId);
		}
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 799032) { // Gefeios
				if (player.getInventory().getItemCountByItemId(100900716) >= 1) { // Noble Siel's Supreme Greatsword
					if (dialog == DialogAction.QUEST_SELECT) {
						return sendQuestDialog(env, 4762);
					} else {
						return sendQuestStartDialog(env);
					}
				}
			}
		} else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 799032) { // Gefeios
				if (dialog == DialogAction.USE_OBJECT) {
					if (player.getInventory().getItemCountByItemId(182209634) > 0) {
						return sendQuestDialog(env, 10002);
					}
				} else {
					removeQuestItem(env, 182209634, 1);
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 286904:
				case 281419:
				case 215795:
					if (QuestService.collectItemCheck(env, true)) {
						return giveQuestItem(env, 182209634, 1);
					}
			}
		}
		return false;
	}

	@Override
	public boolean onGetItemEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			changeQuestStep(env, 0, 0, true); // reward
			return true;
		}
		return false;
	}
}
