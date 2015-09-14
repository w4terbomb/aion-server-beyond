package quest.bare_truth;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.network.aion.SystemMessageId;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

/**
 * @author Artur
 */
public class _14030RetrievedMemory extends QuestHandler {

	private final static int questId = 14030;
	private final static int[] npcs = { 790001, 700551, 205119, 700552, 203700 };
	private final static int[] mobs = { 211043, 214578, 215396, 215397, 215398, 215399, 215400 };

	public _14030RetrievedMemory() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerOnDie(questId);
		qe.registerOnEnterWorld(questId);
		for (int npc_id : npcs) {
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
		}
		for (int mob : mobs) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;
		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 203700: { // Fasimedes
					switch (env.getDialog()) {
						case QUEST_SELECT: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
						}
						case SETPRO1: {
							return defaultCloseDialog(env, 0, 1); // 1
						}
					}
					break;
				}
				case 790001: { // Pernos
					switch (env.getDialog()) {
						case QUEST_SELECT: {
							if (var == 1) {
								return sendQuestDialog(env, 1352);
							}
						}
							if (var == 3) {
								return sendQuestDialog(env, 2034);
							}
						case SETPRO2: {
							TeleportService2.teleportTo(player, 210060000, 2012.37f, 438.231f, 126.020f, (byte) 7, TeleportAnimation.BEAM_ANIMATION);
							return defaultCloseDialog(env, 1, 2); // 2
						}
						case SETPRO4:
							if ((!giveQuestItem(env, 182215387, 1)))
								return false;
							return defaultCloseDialog(env, 3, 4); // 4
					}
				}
					break;
				case 700551: { // Fissure of Destiny
					if (env.getDialog() == DialogAction.USE_OBJECT && var == 4) {
						WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(310120000);
						InstanceService.registerPlayerWithInstance(newInstance, player);
						TeleportService2.teleportTo(player, 310120000, newInstance.getInstanceId(), 52, 174, 229);
						return true;
					}
					break;
				}
				case 205119: { // Hermione
					switch (env.getDialog()) {
						case QUEST_SELECT: {
							if (var == 4) {
								return sendQuestDialog(env, 2375);
							}
						}
						case SETPRO5: {
							if (var == 4) {
								player.setState(CreatureState.FLIGHT_TELEPORT);
								player.unsetState(CreatureState.ACTIVE);
								player.setFlightTeleportId(1001);
								PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, 1001, 0));
								changeQuestStep(env, 4, 5, false);
								return true;
							}
						}
					}
					break;
				}
				case 700552: { // Artifact of Memory
					if (env.getDialog() == DialogAction.USE_OBJECT && var == 56) {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						TeleportService2.teleportTo(player, 110010000, 1876.29f, 1511f, 812.675f, (byte) 60, TeleportAnimation.BEAM_ANIMATION);
						return useQuestObject(env, 56, 56, false, 0, 0, 0, 182215387, 1, 0, false); // 56
					}
					break;
				}

			}
		}

		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203700) { // Fasimedes
				switch (env.getDialog()) {
					case USE_OBJECT: {
						return sendQuestDialog(env, 3739);
					}
					case SELECT_QUEST_REWARD: {
						return sendQuestDialog(env, 5);
					}
					default: {
						return sendQuestEndDialog(env);
					}
				}

			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var >= 2 && var < 55) {
				int[] npcIds = { 215396, 215397, 215398, 215399, 211043, 214578 };
				if (var == 2) {
					return defaultOnKillEvent(env, 214578, 2, 3); // 3
				}
				if (var == 54)
					QuestService.addNewSpawn(310120000, player.getInstanceId(), 215400, 240f, 257f, 208.53946f, (byte) 68);
				return defaultOnKillEvent(env, npcIds, 2, 55); // 2 - 55
			} else if (var == 55) {
				return defaultOnKillEvent(env, 215400, 55, 56); // 56
			}
		}
		return false;
	}

	@Override
	public boolean onDieEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var > 4) {
				changeQuestStep(env, var, 4, false); // 4
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1, DataManager.QUEST_DATA.getQuestById(questId)
					.getName()));
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onEnterWorldEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (player.getWorldId() != 310120000) {
			if (qs != null && qs.getStatus() == QuestStatus.START) {
				int var = qs.getQuestVarById(0);
				if (var > 4) {
					changeQuestStep(env, var, 5, false); // 5
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1, DataManager.QUEST_DATA.getQuestById(questId)
						.getName()));
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env);
	}
}
