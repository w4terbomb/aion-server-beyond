package com.aionemu.gameserver.questEngine.handlers;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.CreatureType;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.DialogPage;
import com.aionemu.gameserver.model.EmotionId;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.QuestStateList;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.quest.CollectItem;
import com.aionemu.gameserver.model.templates.quest.CollectItems;
import com.aionemu.gameserver.model.templates.quest.FinishedQuestCond;
import com.aionemu.gameserver.model.templates.quest.QuestDrop;
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.model.templates.quest.QuestNpc;
import com.aionemu.gameserver.model.templates.quest.QuestWorkItems;
import com.aionemu.gameserver.model.templates.quest.XMLStartCondition;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CUSTOM_SETTINGS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_NPC_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION.ActionType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestActionType;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.task.QuestTasks;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemAddType;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * @author MrPoke
 * @modified vlog, Majka
 */
public abstract class QuestHandler extends AbstractQuestHandler implements ConstantSpawnHandler {

	private static final Logger log = LoggerFactory.getLogger(QuestHandler.class);
	protected final int questId;
	protected QuestEngine qe;
	protected List<QuestItems> workItems;
	protected HashSet<Integer> actionItems;
	protected HashSet<Integer> constantSpawns;

	/** Create a new QuestHandler object */
	protected QuestHandler(int questId) {
		this.questId = questId;
		this.qe = QuestEngine.getInstance();
		loadWorkItems();
		loadActionItems();
		onWorkItemsLoaded();
	}

	private void loadWorkItems() {
		QuestTemplate template = DataManager.QUEST_DATA.getQuestById(questId);
		if (template == null)
			return; // Some artificial quests have dummy questIds
		QuestWorkItems qwi = DataManager.QUEST_DATA.getQuestById(questId).getQuestWorkItems();
		if (qwi == null)
			return;
		workItems = qwi.getQuestWorkItem();
	}

	private void loadActionItems() {
		QuestTemplate template = DataManager.QUEST_DATA.getQuestById(questId);
		if (template == null)
			return; // Some artificial quests have dummy questIds
		List<QuestDrop> qDrop = DataManager.QUEST_DATA.getQuestById(questId).getQuestDrop();
		if (qDrop == null)
			return;
		for (QuestDrop drop : qDrop) {
			if (drop.getNpcId() / 100000 != 7)
				continue;
			if (actionItems == null)
				actionItems = new HashSet<>();
			actionItems.add(drop.getNpcId());
		}
	}

	public Set<Integer> getActionItems() {
		if (actionItems == null)
			return Collections.emptySet();
		return Collections.unmodifiableSet(actionItems);
	}

	/**
	 * Override it to clear them if not used or log details
	 */
	protected void onWorkItemsLoaded() {

	}

	@Override
	public boolean onCanAct(QuestEnv env, QuestActionType questEventType, Object... objects) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(env.getQuestId());
		if (qs == null || qs.getStatus() != QuestStatus.START)
			return false;
		if (questEventType == QuestActionType.ACTION_ITEM_USE && actionItems != null) {
			QuestTemplate template = DataManager.QUEST_DATA.getQuestById(env.getQuestId());
			int droppedItem = 0;
			int dropCount = 0;
			for (QuestDrop drop : template.getQuestDrop()) {
				if (drop.getNpcId() == env.getTargetId()) {
					droppedItem = drop.getItemId();
					break;
				}
			}
			CollectItems collectItems = template.getCollectItems();
			if (collectItems != null && droppedItem != 0) {
				for (CollectItem item : collectItems.getCollectItem()) {
					if (item.getItemId() == droppedItem) {
						dropCount = item.getCount();
						break;
					}
				}
				if (dropCount != 0) {
					long currentCount = player.getInventory().getItemCountByItemId(droppedItem);
					if (currentCount >= dropCount)
						return false;
				}
			}
		}
		return true;
	}

	/** Update the status of the quest in player's journal */
	public void updateQuestStatus(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(ActionType.UPDATE, qs));
		if (qs.getStatus() == QuestStatus.COMPLETE || qs.getStatus() == QuestStatus.REWARD)
			player.getController().updateNearbyQuests();
	}

	public void changeQuestStep(QuestEnv env, int oldStep, int newStep) {
		changeQuestStep(env, oldStep, newStep, false, 0);
	}

	public void changeQuestStep(QuestEnv env, int step, int nextStep, boolean reward) {
		changeQuestStep(env, step, nextStep, reward, 0);
	}

	/** Change the quest step to the next step or set quest status to reward */
	public void changeQuestStep(QuestEnv env, int step, int nextStep, boolean reward, int varNum) {
		QuestState qs = env.getPlayer().getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getQuestVarById(varNum) == step) {
			if (reward) { // ignore nextStep
				qs.setStatus(QuestStatus.REWARD);
			} else { // quest can be rolled back if nextStep < step
				if (nextStep != step) {
					if (step > nextStep && qs.getStatus() == QuestStatus.START)
						PacketSendUtility.sendPacket(env.getPlayer(),
							SM_SYSTEM_MESSAGE.STR_QUEST_SYSTEMMSG_GIVEUP(DataManager.QUEST_DATA.getQuestById(questId).getNameId()));
					qs.setQuestVarById(varNum, nextStep);
				}
			}
			if (reward || nextStep != step) {
				updateQuestStatus(env);
			}
		}
	}

	/** Send dialog to the player */
	public boolean sendQuestDialog(QuestEnv env, int dialogId) {
		switch (DialogPage.getPageByAction(dialogId)) {
			case SELECT_QUEST_REWARD_WINDOW1:
			case SELECT_QUEST_REWARD_WINDOW2:
			case SELECT_QUEST_REWARD_WINDOW3:
			case SELECT_QUEST_REWARD_WINDOW4:
			case SELECT_QUEST_REWARD_WINDOW5:
			case SELECT_QUEST_REWARD_WINDOW6:
			case SELECT_QUEST_REWARD_WINDOW7:
			case SELECT_QUEST_REWARD_WINDOW8:
			case SELECT_QUEST_REWARD_WINDOW9:
			case SELECT_QUEST_REWARD_WINDOW10:
				QuestState qs = env.getPlayer().getQuestStateList().getQuestState(questId);
				if (qs == null || qs.getStatus() != QuestStatus.REWARD) // reward packet exploitation fix
					return false;
		}
		sendDialogPacket(env, dialogId);
		return true;
	}

	public boolean sendQuestSelectionDialog(QuestEnv env) {
		sendQuestSelectionPacket(env, 10);
		return true;
	}

	public boolean closeDialogWindow(QuestEnv env) {
		sendQuestSelectionPacket(env, 0);
		return true;
	}

	public boolean sendQuestStartDialog(QuestEnv env) {
		return sendQuestStartDialog(env, 0, 0);
	}

	/** Send default start quest dialog and start it (give the item on start) */
	public boolean sendQuestStartDialog(QuestEnv env, int itemId, int itemCount) {
		switch (env.getDialog()) {
			case ASK_QUEST_ACCEPT:
				return sendQuestDialog(env, 4);
			case QUEST_ACCEPT_1:
				if (itemId != 0 && itemCount != 0) {
					if (!env.getPlayer().getInventory().isFullSpecialCube()) {
						if (QuestService.startQuest(env)) {
							giveQuestItem(env, itemId, itemCount);
							return sendQuestDialog(env, 1003);
						}
					}
				} else {
					if (QuestService.startQuest(env)) {
						if (env.getVisibleObject() == null || env.getVisibleObject() instanceof Player)
							return closeDialogWindow(env);
						else
							return sendQuestDialog(env, 1003);
					}
				}
				break;
			case QUEST_ACCEPT_SIMPLE:
				if (itemId != 0 && itemCount != 0) {
					if (!env.getPlayer().getInventory().isFullSpecialCube()) {
						if (QuestService.startQuest(env)) {
							giveQuestItem(env, itemId, itemCount);
							return closeDialogWindow(env);
						}
					}
				} else if (QuestService.startQuest(env)) {
					return closeDialogWindow(env);
				}
				break;
			case QUEST_REFUSE_1:
			case QUEST_REFUSE_2:
				return sendQuestDialog(env, 1004);
			case QUEST_REFUSE_SIMPLE:
				return closeDialogWindow(env);
			case FINISH_DIALOG:
				return sendQuestSelectionDialog(env);
		}
		return false;
	}

	/** Remove all quest items and send and finish the quest */
	public boolean sendQuestEndDialog(QuestEnv env, int[] questItemsToRemove) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestStatus status = qs != null ? qs.getStatus() : QuestStatus.NONE;
		for (int itemId : questItemsToRemove)
			removeQuestItem(env, itemId, player.getInventory().getItemCountByItemId(itemId), status);
		return sendQuestEndDialog(env);
	}

	/** Send completion dialog of the quest and finish it. Give the default reward from quest_data.xml */
	public boolean sendQuestEndDialog(QuestEnv env) {
		int rewardGroups = DataManager.QUEST_DATA.getQuestById(env.getQuestId()).getRewards().size();
		// you should explicitly specify the reward group when there are more than 1
		if (rewardGroups > 1 && env.getDialogId() >= DialogAction.SELECTED_QUEST_REWARD1.id()
			&& env.getDialogId() <= DialogAction.SELECTED_QUEST_NOREWARD.id())
			log.warn("Quest handler for quest: " + env.getQuestId() + " possibly rewarded the wrong reward group.");
		return sendQuestEndDialog(env, rewardGroups == 0 ? null : 0);
	}

	/**
	 * Sends reward selection dialog of the quest or finishes it (if selection dialog was active)
	 * 
	 * @param env
	 * @param rewardGroup
	 *          - Which {@code <rewards>} group (from quest_data.xml) to use, null for none.
	 */
	public boolean sendQuestEndDialog(QuestEnv env, Integer rewardGroup) {
		Player player = env.getPlayer();
		int dialogId = env.getDialogId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.REWARD)
			return false; // reward packet exploitation fix (or buggy quest handler)
		if (dialogId >= DialogAction.SELECTED_QUEST_REWARD1.id() && dialogId <= DialogAction.SELECTED_QUEST_NOREWARD.id()) {
			if (QuestService.finishQuest(env, rewardGroup)) {
				Npc npc = (Npc) env.getVisibleObject();
				QuestNpc questNpc = QuestEngine.getInstance().getQuestNpc(npc.getNpcId());
				boolean npcHasActiveQuest = false;
				for (Integer questId : questNpc.getOnTalkEvent()) { // all quest IDs that have registered talk events for this npc
					QuestState qs2 = player.getQuestStateList().getQuestState(questId);
					if (qs2 != null && qs2.getStatus() == QuestStatus.REWARD) { // TODO make sure that this npc is the end npc
						env.setQuestId(questId);
						env.setDialogId(DialogAction.USE_OBJECT.id()); // show default dialog (reward selection for next quest)
						return QuestEngine.getInstance().onDialog(new QuestEnv(npc, player, questId, DialogAction.USE_OBJECT.id()));
					} else if (!npcHasActiveQuest && qs2 != null && qs2.getStatus() == QuestStatus.START) {
						boolean isQuestStartNpc = questNpc.getOnQuestStart().contains(questId);
						if (!isQuestStartNpc
							|| isQuestStartNpc && DataManager.QUEST_DATA.getQuestById(questId).isMission() && qs2.getQuestVars().getQuestVars() == 0)
							npcHasActiveQuest = true; // TODO correct way to make sure that active quest can be continued at this npc
					}
				}
				boolean npcHasNewQuest = false;
				for (Integer questId : questNpc.getOnQuestStart()) { // all quest IDs that are registered to be started at this npc
					if (QuestService.checkStartConditions(player, questId, false)) {
						npcHasNewQuest = true;
						QuestTemplate template = DataManager.QUEST_DATA.getQuestById(questId);
						for (XMLStartCondition startCondition : template.getXMLStartConditions()) {
							List<FinishedQuestCond> finishedQuests = startCondition.getFinishedPreconditions();
							if (finishedQuests != null) {
								for (FinishedQuestCond fcondition : finishedQuests) {
									if (fcondition.getQuestId() == env.getQuestId()) {
										env.setQuestId(questId);
										env.setDialogId(DialogAction.QUEST_SELECT.id());
										return QuestEngine.getInstance().onDialog(env); // show start dialog of follow-up quest
									}
								}
							}
						}
					}
				}
				return npcHasActiveQuest || npcHasNewQuest ? sendQuestSelectionDialog(env) : closeDialogWindow(env);
			}
			return false;
		} else if (dialogId == DialogAction.SELECT_QUEST_REWARD.id() || dialogId == DialogAction.USE_OBJECT.id()) { // show reward selection page
			return sendQuestDialog(env, DialogPage.getRewardPageByIndex(rewardGroup).id());
		}
		return false;
	}

	public boolean defaultCloseDialog(QuestEnv env, int step, int nextStep) {
		return defaultCloseDialog(env, step, nextStep, false, false, 0, 0, 0, 0, 0);
	}

	public boolean defaultCloseDialog(QuestEnv env, int step, int nextStep, int giveItemId, int giveItemCount) {
		return defaultCloseDialog(env, step, nextStep, false, false, 0, giveItemId, giveItemCount, 0, 0);
	}

	public boolean defaultCloseDialog(QuestEnv env, int step, int nextStep, int varNum) {
		return defaultCloseDialog(env, step, nextStep, false, false, 0, 0, 0, 0, 0, varNum);
	}

	public boolean defaultCloseDialog(QuestEnv env, int step, int nextStep, boolean reward, boolean sameNpc) {
		return defaultCloseDialog(env, step, nextStep, reward, sameNpc, 0, 0, 0, 0, 0);
	}

	public boolean defaultCloseDialog(QuestEnv env, int step, int nextStep, boolean reward, boolean sameNpc, int rewardId) {
		return defaultCloseDialog(env, step, nextStep, reward, sameNpc, rewardId, 0, 0, 0, 0);
	}

	public boolean defaultCloseDialog(QuestEnv env, int step, int nextStep, int giveItemId, int giveItemCount, int removeItemId, int removeItemCount) {
		return defaultCloseDialog(env, step, nextStep, false, false, 0, giveItemId, giveItemCount, removeItemId, removeItemCount);
	}

	public boolean defaultCloseDialog(QuestEnv env, int step, int nextStep, boolean reward, boolean sameNpc, int giveItemId, int giveItemCount,
		int removeItemId, int removeItemCount) {
		return defaultCloseDialog(env, step, nextStep, reward, sameNpc, 0, giveItemId, giveItemCount, removeItemId, removeItemCount);
	}

	public boolean defaultCloseDialog(QuestEnv env, int step, int nextStep, boolean reward, boolean sameNpc, int rewardId, int giveItemId,
		int giveItemCount, int removeItemId, int removeItemCount) {
		return defaultCloseDialog(env, step, nextStep, reward, sameNpc, rewardId, giveItemId, giveItemCount, removeItemId, removeItemCount, 0);
	}

	/** Handle on close dialog event, changing the quest status and giving/removing quest items */
	public boolean defaultCloseDialog(QuestEnv env, int step, int nextStep, boolean reward, boolean sameNpc, int rewardId, int giveItemId,
		int giveItemCount, int removeItemId, int removeItemCount, int varNum) {
		QuestState qs = env.getPlayer().getQuestStateList().getQuestState(questId);
		if (qs.getQuestVarById(0) == step) {
			if (giveItemId != 0 && giveItemCount != 0) {
				if (!giveQuestItem(env, giveItemId, giveItemCount)) {
					return false;
				}
			}
			removeQuestItem(env, removeItemId, removeItemCount, qs.getStatus());
			changeQuestStep(env, step, nextStep, reward, varNum);
			if (sameNpc) {
				return sendQuestEndDialog(env, rewardId);
			}
			return closeDialogWindow(env);
		}
		return false;
	}

	public boolean checkQuestItems(QuestEnv env, int step, int nextStep, boolean reward, int checkOkId, int checkFailId) {
		return checkQuestItems(env, step, nextStep, reward, checkOkId, checkFailId, 0, 0);
	}

	/** Check if the player has quest item, listed in the quest_data.xml in his inventory */
	public boolean checkQuestItems(QuestEnv env, int step, int nextStep, boolean reward, int checkOkId, int checkFailId, int giveItemId,
		int giveItemCount) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs.getQuestVarById(0) == step) {
			if (QuestService.collectItemCheck(env, true)) {
				if (giveItemId != 0 && giveItemCount != 0) {
					if (!giveQuestItem(env, giveItemId, giveItemCount)) {
						return false;
					}
				}
				changeQuestStep(env, step, nextStep, reward);
				return sendQuestDialog(env, checkOkId);
			} else {
				return sendQuestDialog(env, checkFailId);
			}
		}
		return false;
	}

	/** Check if the player has quest item (simple version), listed in the quest_data.xml in his inventory */
	public boolean checkQuestItemsSimple(QuestEnv env, int step, int nextStep, boolean reward, int checkOkId, int giveItemId, int giveItemCount) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs.getQuestVarById(0) == step) {
			if (QuestService.collectItemCheck(env, true)) {
				if (giveItemId != 0 && giveItemCount != 0) {
					if (!giveQuestItem(env, giveItemId, giveItemCount)) {
						return false;
					}
				}
				changeQuestStep(env, step, nextStep, reward);
				return sendQuestDialog(env, checkOkId);
			} else
				return closeDialogWindow(env);
		}
		return false;
	}

	/** To use for checking the items, not listed in the collect_items in the quest_data.xml */
	public boolean checkItemExistence(QuestEnv env, int step, int nextStep, boolean reward, int itemId, int itemCount, boolean remove, int checkOkId,
		int checkFailId, int giveItemId, int giveItemCount) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs.getQuestVarById(0) == step) {
			if (checkItemExistence(env, itemId, itemCount, remove)) {
				if (giveItemId != 0 && giveItemCount != 0) {
					if (!giveQuestItem(env, giveItemId, giveItemCount)) {
						return false;
					}
				}
				changeQuestStep(env, step, nextStep, reward);
				return sendQuestDialog(env, checkOkId);
			} else {
				return sendQuestDialog(env, checkFailId);
			}
		}
		return false;
	}

	/** Check, if item exists in the player's inventory and probably remove it */
	public boolean checkItemExistence(QuestEnv env, int itemId, int itemCount, boolean remove) {
		Player player = env.getPlayer();
		if (player.getInventory().getItemCountByItemId(itemId) >= itemCount) {
			if (remove) {
				if (!removeQuestItem(env, itemId, itemCount)) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public void sendEmotion(QuestEnv env, Creature emoteCreature, EmotionId emotion, boolean broadcast) {
		Player player = env.getPlayer();
		int targetId = player.equals(emoteCreature) ? env.getVisibleObject().getObjectId() : player.getObjectId();

		// TODO: fix it, broadcast and direction sometimes do not work when the emoteCreature is NPC
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(emoteCreature, EmotionType.EMOTE, emotion.id(), targetId), broadcast);
	}

	/** Give the quest item to player's inventory */
	public boolean giveQuestItem(QuestEnv env, int itemId, long itemCount) {
		return giveQuestItem(env, itemId, itemCount, ItemAddType.QUEST_WORK_ITEM, ItemUpdateType.INC_ITEM_COLLECT);
	}

	public boolean giveQuestItem(QuestEnv env, int itemId, long itemCount, ItemAddType addType) {
		return giveQuestItem(env, itemId, itemCount, addType, ItemUpdateType.INC_ITEM_COLLECT);
	}

	public boolean giveQuestItem(QuestEnv env, int itemId, long itemCount, ItemAddType addType, ItemUpdateType updateType) {
		Player player = env.getPlayer();
		ItemTemplate item = DataManager.ITEM_DATA.getItemTemplate(itemId);
		if (itemId != 0 && itemCount != 0) {
			long existentItemCount = player.getInventory().getItemCountByItemId(itemId);
			if (existentItemCount < itemCount) {
				long itemsToGive = itemCount - existentItemCount; // some quest work items come from multiple quests, don't add again
				ItemService.addItem(player, itemId, itemsToGive, true, new ItemService.ItemUpdatePredicate(addType, updateType));
				return true;
			} else {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CAN_NOT_GET_LORE_ITEM((new DescriptionId(item.getNameId()))));
				return true;
			}
		}
		return false;
	}

	/** Remove the specified count of this quest item from player's inventory */
	public boolean removeQuestItem(QuestEnv env, int itemId, long itemCount) {
		Player player = env.getPlayer();
		if (itemId != 0 && itemCount > 0) {
			QuestState qs = player.getQuestStateList().getQuestState(questId);
			return player.getInventory().decreaseByItemId(itemId, itemCount, qs == null ? QuestStatus.START : qs.getStatus());
		}
		return false;
	}

	public boolean removeQuestItem(QuestEnv env, int itemId, long itemCount, QuestStatus questStatus) {
		Player player = env.getPlayer();
		if (itemId != 0 && itemCount != 0) {
			return player.getInventory().decreaseByItemId(itemId, itemCount, questStatus);
		}
		return false;
	}

	/** Play movie with given ID */
	public boolean playQuestMovie(QuestEnv env, int MovieId) {
		Player player = env.getPlayer();
		PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, MovieId));
		return false;
	}

	/** For single kill */
	public boolean defaultOnKillEvent(QuestEnv env, int npcId, int startVar, int endVar) {
		int[] mobids = { npcId };
		return defaultOnKillEvent(env, mobids, startVar, endVar);
	}

	/** For multiple kills */
	public boolean defaultOnKillEvent(QuestEnv env, int[] npcIds, int startVar, int endVar) {
		return defaultOnKillEvent(env, npcIds, startVar, endVar, 0);
	}

	/** For single kill on another QuestVar */
	public boolean defaultOnKillEvent(QuestEnv env, int npcId, int startVar, int endVar, int varNum) {
		int[] mobids = { npcId };
		return defaultOnKillEvent(env, mobids, startVar, endVar, varNum);
	}

	/** Handle onKill event */
	public boolean defaultOnKillEvent(QuestEnv env, int[] npcIds, int startVar, int endVar, int varNum) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(varNum);
			int targetId = env.getTargetId();
			for (int id : npcIds) {
				if (targetId == id) {
					if (var >= startVar && var < endVar) {
						qs.setQuestVarById(varNum, var + 1);
						updateQuestStatus(env);
						return true;
					}
				}
			}
		}
		return false;
	}

	/** For single kill and reward status after it */
	public boolean defaultOnKillEvent(QuestEnv env, int npcId, int startVar, boolean reward) {
		int[] mobids = { npcId };
		return (defaultOnKillEvent(env, mobids, startVar, reward, 0));
	}

	/** For single kill on another QuestVar and reward status after it */
	public boolean defaultOnKillEvent(QuestEnv env, int npcId, int startVar, boolean reward, int varNum) {
		int[] mobids = { npcId };
		return (defaultOnKillEvent(env, mobids, startVar, reward, varNum));
	}

	/** For multiple kills and reward status after it */
	public boolean defaultOnKillEvent(QuestEnv env, int[] npcIds, int startVar, boolean reward) {
		return (defaultOnKillEvent(env, npcIds, startVar, reward, 0));
	}

	/** Handle onKill event with reward status */
	public boolean defaultOnKillEvent(QuestEnv env, int[] npcIds, int startVar, boolean reward, int varNum) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(varNum);
			int targetId = env.getTargetId();
			for (int id : npcIds) {
				if (targetId == id) {
					if (var == startVar) {
						if (reward) {
							qs.setStatus(QuestStatus.REWARD);
						} else {
							qs.setQuestVarById(varNum, var + 1);
						}
						updateQuestStatus(env);
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean defaultOnKillRankedEvent(QuestEnv env, int startVar, int endVar, boolean reward) {
		return defaultOnKillRankedEvent(env, startVar, endVar, reward, false);
	}

	public boolean defaultOnKillRankedEvent(QuestEnv env, int startVar, int endVar, boolean reward, boolean isDataDriven) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (isDataDriven) {
				int varKill = qs.getQuestVarById(1);
				if (varKill >= startVar && varKill < (endVar - 1)) {
					changeQuestStep(env, varKill, varKill + 1, false, 1);
					return true;
				} else if (varKill == (endVar - 1)) {
					if (reward)
						qs.setStatus(QuestStatus.REWARD);
					qs.setQuestVar(var + 1);
				}
			} else {
				if (var >= startVar && var < (endVar - 1)) {
					changeQuestStep(env, var, var + 1, false);
					return true;
				} else if (var == (endVar - 1)) {
					if (reward)
						qs.setStatus(QuestStatus.REWARD);
					else
						qs.setQuestVarById(0, var + 1);
				}
			}
			updateQuestStatus(env);
			return true;
		}
		return false;
	}

	public boolean defaultOnKillInZoneEvent(QuestEnv env, int startVar, int endVar, boolean reward) {
		return defaultOnKillRankedEvent(env, startVar, endVar, reward, false);
	}

	public boolean defaultOnKillInZoneEvent(QuestEnv env, int startVar, int endVar, boolean reward, boolean isDataDriven) {
		return defaultOnKillRankedEvent(env, startVar, endVar, reward, isDataDriven);
	}

	public boolean defaultOnUseSkillEvent(QuestEnv env, int startVar, int endVar, int varNum) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(varNum);
			if (var >= startVar && var < endVar) {
				changeQuestStep(env, var, var + 1, false, varNum);
				return true;
			}
		}
		return false;
	}

	/** NPC starts following the player to the target. Use onLostTarget and onReachTarget for further actions. */
	public boolean defaultStartFollowEvent(QuestEnv env, final Npc follower, int targetNpcId, int step, int nextStep) {
		final Player player = env.getPlayer();
		if (!(env.getVisibleObject() instanceof Npc)) {
			return false;
		}
		follower.setNpcType(CreatureType.PEACE);
		follower.getKnownList().forEachPlayer(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, new SM_CUSTOM_SETTINGS(follower.getObjectId(), 0, follower.getType(player).getId(), 0));
			}
		});
		follower.getAi2().onCreatureEvent(AIEventType.FOLLOW_ME, player);
		player.getController().addTask(TaskId.QUEST_FOLLOW, QuestTasks.newFollowingToTargetCheckTask(env, follower, targetNpcId));
		if (step == 0 && nextStep == 0) {
			return true;
		} else {
			return defaultCloseDialog(env, step, nextStep);
		}
	}

	/** NPC starts following the player to the target location. Use onLostTarget and onReachTarget for further actions. */
	public boolean defaultStartFollowEvent(QuestEnv env, Npc follower, float x, float y, float z, int step, int nextStep) {
		final Player player = env.getPlayer();
		if (!(env.getVisibleObject() instanceof Npc)) {
			return false;
		}
		PacketSendUtility.sendPacket(player, new SM_NPC_INFO(follower));
		follower.getAi2().onCreatureEvent(AIEventType.FOLLOW_ME, player);
		player.getController().addTask(TaskId.QUEST_FOLLOW, QuestTasks.newFollowingToTargetCheckTask(env, follower, x, y, z));
		if (step == 0 && nextStep == 0) {
			return true;
		} else {
			return defaultCloseDialog(env, step, nextStep);
		}
	}

	/** NPC stops following the player. Used in both onLostTargetEvent and onReachTargetEvent. */
	public boolean defaultFollowEndEvent(QuestEnv env, int step, int nextStep, boolean reward, int movie) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (qs.getQuestVarById(0) == step) {
				changeQuestStep(env, step, nextStep, reward);
				if (movie != 0)
					playQuestMovie(env, movie);
				return true;
			}
		}
		return false;
	}

	public boolean defaultFollowEndEvent(QuestEnv env, int step, int nextStep, boolean reward) {
		return defaultFollowEndEvent(env, step, nextStep, reward, 0);
	}

	/** Changing quest step on getting item */
	public boolean defaultOnGetItemEvent(QuestEnv env, int step, int nextStep, boolean reward) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (qs.getQuestVarById(0) == step) {
				changeQuestStep(env, step, nextStep, reward);
				return true;
			}
		}
		return false;
	}

	public boolean useQuestObject(QuestEnv env, int step, int nextStep, boolean reward, boolean die) {
		return useQuestObject(env, step, nextStep, reward, 0, 0, 0, 0, 0, 0, die);
	}

	public boolean useQuestObject(QuestEnv env, int step, int nextStep, boolean reward, int varNum, boolean die) {
		return useQuestObject(env, step, nextStep, reward, varNum, 0, 0, 0, 0, 0, die);
	}

	public boolean useQuestObject(QuestEnv env, int step, int nextStep, boolean reward, int varNum) {
		return useQuestObject(env, step, nextStep, reward, varNum, 0, 0, 0, 0, 0, false);
	}

	public boolean useQuestObject(QuestEnv env, int step, int nextStep, boolean reward, int varNum, int addItemId, int addItemCount) {
		return useQuestObject(env, step, nextStep, reward, varNum, addItemId, addItemCount, 0, 0, 0, false);
	}

	public boolean useQuestObject(QuestEnv env, int step, int nextStep, boolean reward, int varNum, int addItemId, int addItemCount, int removeItemId,
		int removeItemCount) {
		return useQuestObject(env, step, nextStep, reward, varNum, addItemId, addItemCount, removeItemId, removeItemCount, 0, false);
	}

	public boolean useQuestObject(QuestEnv env, int step, int nextStep, boolean reward, int varNum, int movieId) {
		return useQuestObject(env, step, nextStep, reward, varNum, 0, 0, 0, 0, movieId, false);
	}

	/** Handle use object event */
	public boolean useQuestObject(QuestEnv env, int step, int nextStep, boolean reward, int varNum, int addItemId, int addItemCount, int removeItemId,
		int removeItemCount, int movieId, boolean dieObject) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null) {
			return false;
		}
		if (qs.getQuestVarById(varNum) == step) {
			if (addItemId != 0 && addItemCount != 0) {
				if (!giveQuestItem(env, addItemId, addItemCount)) {
					return false;
				}
			}
			if (removeItemId != 0 && removeItemCount != 0) {
				removeQuestItem(env, removeItemId, removeItemCount);
			}
			if (movieId != 0) {
				playQuestMovie(env, movieId);
			}
			if (dieObject) {
				Npc npc = (Npc) player.getTarget();
				if (!env.getVisibleObject().equals(npc))
					return false;
				npc.getController().onDie(player);
			}
			changeQuestStep(env, step, nextStep, reward, varNum);
			return true;
		}
		return false;
	}

	public boolean useQuestItem(QuestEnv env, Item item, int step, int nextStep, boolean reward) {
		return useQuestItem(env, item, step, nextStep, reward, 0, 0, 0);
	}

	public boolean useQuestItem(QuestEnv env, Item item, int step, int nextStep, boolean reward, final int addItemId, final int addItemCount) {
		return useQuestItem(env, item, step, nextStep, reward, addItemId, addItemCount, 0);
	}

	public boolean useQuestItem(QuestEnv env, Item item, int step, int nextStep, boolean reward, int movieId) {
		return useQuestItem(env, item, step, nextStep, reward, 0, 0, movieId);
	}

	public boolean useQuestItem(final QuestEnv env, final Item item, final int step, final int nextStep, final boolean reward, final int addItemId,
		final int addItemCount, final int movieId) {
		return useQuestItem(env, item, step, nextStep, reward, addItemId, addItemCount, movieId, 0);
	}

	/** Handle use item event */
	public boolean useQuestItem(final QuestEnv env, final Item item, final int step, final int nextStep, final boolean reward, final int addItemId,
		final int addItemCount, final int movieId, final int varNum) {
		final Player player = env.getPlayer();
		if (player == null) {
			return false;
		}
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null) {
			return false;
		}
		final int itemId = item.getItemId();
		final int objectId = item.getObjectId();

		if (qs.getQuestVarById(varNum) == step) {
			PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), objectId, itemId, 3000, 0, 0), true);
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), objectId, itemId, 0, 1, 0), true);
					removeQuestItem(env, itemId, 1);

					if (addItemId != 0 && addItemCount != 0) {
						if (!giveQuestItem(env, addItemId, addItemCount)) {
							return;
						}
					}
					if (movieId != 0) {
						playQuestMovie(env, movieId);
					}
					changeQuestStep(env, step, nextStep, reward, varNum);
				}
			}, 3000);
			return true;
		}
		return false;
	}

	/**
	 * Starts or locks quest on level up (usually used from campaign quest handlers)
	 * 
	 * @param player
	 *          - Player who wants to start the quest
	 * @param preQuests
	 *          - The quests to be completed before starting this one
	 * @return True if successfully started
	 */
	public boolean defaultOnLevelChangedEvent(Player player, int... preQuests) {
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		// Only null or LOCKED quests can be started
		if (qs != null && qs.getStatus() != QuestStatus.LOCKED)
			return false;

		QuestTemplate template = DataManager.QUEST_DATA.getQuestById(questId);
		int minLvlDiff = template.isMission() ? 2 : 0;
		// Check all player requirements (but allowed diff to quest minLevel = 2)
		if (!QuestService.checkStartConditions(player, questId, false, minLvlDiff, false, false, template.isMission()))
			return false;

		boolean missingRequirement = false;
		for (int id : preQuests) {
			QuestState qs2 = player.getQuestStateList().getQuestState(id);
			if (!missingRequirement && (qs2 == null || qs2.getStatus() != QuestStatus.COMPLETE)) {
				if (qs != null || !template.isMission()) // fast return if its already locked or no campaign quest
					return false;
				missingRequirement = true;
			}
			if (missingRequirement && qs2 != null && qs2.getStatus() == QuestStatus.COMPLETE) {
				QuestService.addOrUpdateQuest(player, questId, QuestStatus.LOCKED);
				return false;
			}
		}
		if (missingRequirement)
			return false;

		// Check the quests, that have to be done before starting this one and other start conditions, listed in quest_data
		for (XMLStartCondition cond : template.getXMLStartConditions()) {
			if (!cond.check(player, false)) {
				if (qs == null && template.isMission())
					QuestService.addOrUpdateQuest(player, questId, QuestStatus.LOCKED);
				return false;
			}
		}

		// Send locked quest if the player is <= 2 levels below quest min level (as specified in the check above)
		if (minLvlDiff > 0 && player.getLevel() < template.getMinlevelPermitted()) {
			if (qs == null && template.isMission())
				QuestService.addOrUpdateQuest(player, questId, QuestStatus.LOCKED);
			return false;
		}

		// All conditions are met, start the quest
		QuestService.addOrUpdateQuest(player, questId, QuestStatus.START);
		return true;
	}

	/**
	 * Starts or locks quest after quest completion (usually used from campaign quest handlers).
	 * 
	 * @param env
	 *          - QuestEnv containing the player and quest which he completed
	 * @param preQuests
	 *          - The quests to be completed before starting this one
	 * @return True if successfully started
	 */
	public boolean defaultOnQuestCompletedEvent(QuestEnv env, int... preQuests) {
		Player player = env.getPlayer();
		int finishedQuestId = env.getQuestId();
		QuestStateList qsl = player.getQuestStateList();
		QuestState qs = qsl.getQuestState(questId);

		// Only null or LOCKED quests can be started
		if (qs != null && qs.getStatus() != QuestStatus.LOCKED)
			return false;

		QuestTemplate template = DataManager.QUEST_DATA.getQuestById(questId);
		int minLvlDiff = template.isMission() ? 15 : 0; // this ensures to add all follow-up quests in locked state
		// Check all player requirements first
		if (!QuestService.checkStartConditions(player, questId, false, minLvlDiff, false, false, template.isMission()))
			return false;

		boolean missingRequirement = false;
		boolean hasFinishedPreQuest = false;
		for (int id : preQuests) {
			QuestState qs2 = qsl.getQuestState(id);
			if (!missingRequirement && (qs2 == null || qs2.getStatus() != QuestStatus.COMPLETE)) {
				if (qs != null || !template.isMission()) // fast return if its already locked or no campaign quest
					return false;
				missingRequirement = true;
			}
			if (finishedQuestId == id)
				hasFinishedPreQuest = true;
			if (missingRequirement && (hasFinishedPreQuest || qs2 != null && qs2.getStatus() == QuestStatus.COMPLETE)) { // if any pre quest is finished
				QuestService.addOrUpdateQuest(player, questId, QuestStatus.LOCKED);
				return false;
			}
		}
		if (missingRequirement)
			return false;

		// Check the quests, that have to be done before starting this one and other start conditions, listed in quest_data
		missingRequirement = false;
		for (XMLStartCondition cond : template.getXMLStartConditions()) {
			if (!cond.check(player, false)) {
				if (qs != null || !template.isMission()) // fast return if its already locked or no campaign quest
					return false;
				else if (hasAnyPreQuestFinished(qsl, cond)) { // recursive check
					QuestService.addOrUpdateQuest(player, questId, QuestStatus.LOCKED);
					return false;
				}
				missingRequirement = true;
			}
		}
		if (missingRequirement)
			return false;

		// Send locked quest if the players level is in the minLvlDiff range (1-15)
		if (minLvlDiff > 0 && player.getLevel() < template.getMinlevelPermitted()) {
			if (qs == null && hasFinishedPreQuest)
				QuestService.addOrUpdateQuest(player, questId, QuestStatus.LOCKED);
			return false;
		}

		// All conditions are met, start the quest
		QuestService.addOrUpdateQuest(player, questId, QuestStatus.START);
		return true;
	}

	/**
	 * Checks recursively if any pre-quest that is required, is completed
	 * 
	 * @param qsl
	 *          - Players {@link QuestStateList}
	 * @param startCondition
	 *          - The XML start condition list
	 * @return True, if any pre-quest of this series is finished
	 */
	private static boolean hasAnyPreQuestFinished(QuestStateList qsl, XMLStartCondition startCondition) {
		for (FinishedQuestCond finishedCond : startCondition.getFinishedPreconditions()) {
			QuestState qs = qsl.getQuestState(finishedCond.getQuestId());
			if (qs != null && qs.getStatus() == QuestStatus.COMPLETE)
				return true;
			QuestTemplate template = DataManager.QUEST_DATA.getQuestById(finishedCond.getQuestId());
			for (XMLStartCondition cond : template.getXMLStartConditions())
				if (hasAnyPreQuestFinished(qsl, cond))
					return true;
		}
		return false;
	}

	/** Start a mission on enter the questZone */
	public boolean defaultOnEnterZoneEvent(QuestEnv env, ZoneName currentZoneName, ZoneName questZoneName) {
		if (questZoneName == currentZoneName) {
			Player player = env.getPlayer();
			if (player == null)
				return false;
			QuestState qs = player.getQuestStateList().getQuestState(questId);
			if (qs == null) {
				env.setQuestId(questId);
				if (QuestService.startQuest(env))
					return true;
			}
		}
		return false;
	}

	public boolean sendQuestRewardDialog(QuestEnv env, int rewardNpcId, int reportDialogId) {
		return sendQuestRewardDialog(env, rewardNpcId, reportDialogId, 0);
	}

	public boolean sendQuestRewardDialog(QuestEnv env, int rewardNpcId, int reportDialogId, int rewardId) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs.getStatus() == QuestStatus.REWARD) {
			if (env.getTargetId() == rewardNpcId) {
				if (env.getDialog() == DialogAction.USE_OBJECT && reportDialogId != 0) {
					return sendQuestDialog(env, reportDialogId);
				} else {
					return sendQuestEndDialog(env, rewardId);
				}
			}
		}
		return false;
	}

	public boolean sendQuestNoneDialog(QuestEnv env, int startNpcId) {
		QuestTemplate template = DataManager.QUEST_DATA.getQuestById(questId);
		return sendQuestNoneDialog(env, template, startNpcId, 1011);
	}

	public boolean sendQuestNoneDialog(QuestEnv env, int startNpcId, int dialogId) {
		QuestTemplate template = DataManager.QUEST_DATA.getQuestById(questId);
		return sendQuestNoneDialog(env, template, startNpcId, dialogId);
	}

	public boolean sendQuestNoneDialog(QuestEnv env, QuestTemplate template, int startNpcId, int dialogId) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs == null || qs.isStartable()) {
			if (env.getTargetId() == startNpcId) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, dialogId);
				} else {
					return sendQuestStartDialog(env);
				}
			}
		}
		return false;
	}

	public boolean sendQuestNoneDialog(QuestEnv env, int startNpcId, int dialogId, int itemId, int itemCout) {
		QuestTemplate template = DataManager.QUEST_DATA.getQuestById(questId);
		return sendQuestNoneDialog(env, template, startNpcId, dialogId, itemId, itemCout);
	}

	public boolean sendQuestNoneDialog(QuestEnv env, int startNpcId, int itemId, int itemCout) {
		QuestTemplate template = DataManager.QUEST_DATA.getQuestById(questId);
		return sendQuestNoneDialog(env, template, startNpcId, 1011, itemId, itemCout);
	}

	public boolean sendQuestNoneDialog(QuestEnv env, QuestTemplate template, int startNpcId, int dialogId, int itemId, int itemCout) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs == null || qs.isStartable()) {
			if (env.getTargetId() == startNpcId) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, dialogId);
				}
				if (itemId != 0 && itemCout != 0) {
					if (env.getDialog() == DialogAction.QUEST_ACCEPT_1) {
						if (giveQuestItem(env, itemId, itemCout)) {
							return sendQuestStartDialog(env);
						} else {
							return true;
						}
					} else {
						return sendQuestStartDialog(env);
					}
				} else {
					return sendQuestStartDialog(env);
				}
			}
		}
		return false;
	}

	public boolean sendItemCollectingStartDialog(QuestEnv env) {
		switch (env.getDialog()) {
			case QUEST_ACCEPT_1:
				QuestService.startQuest(env);
				return sendQuestSelectionDialog(env);
			case QUEST_REFUSE_1:
				return sendQuestSelectionDialog(env);
		}
		return false;
	}

	@Override
	public int getQuestId() {
		return questId;
	}

	private void sendDialogPacket(QuestEnv env, int dialogId) {
		int objId = 0;
		if (env.getVisibleObject() != null) {
			objId = env.getVisibleObject().getObjectId();
		}
		// Not using questId, because some quests may handle events when quests are finished
		// In that case questId must be zero!!! (Kromede entry for example)
		PacketSendUtility.sendPacket(env.getPlayer(), new SM_DIALOG_WINDOW(objId, dialogId, env.getQuestId()));
	}

	private void sendQuestSelectionPacket(QuestEnv env, int dialogId) {
		int objId = 0;
		if (env.getVisibleObject() != null) {
			objId = env.getVisibleObject().getObjectId();
		}
		PacketSendUtility.sendPacket(env.getPlayer(), new SM_DIALOG_WINDOW(objId, dialogId));
	}

	@Override
	public abstract void register();

	@Override
	public HashSet<Integer> getNpcIds() {
		return null;
	}

}
