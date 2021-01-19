package ai.worlds.kaldor;

import java.util.concurrent.TimeUnit;

import com.aionemu.gameserver.ai.AIName;
import com.aionemu.gameserver.ai.poll.AIQuestion;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;

import ai.AggressiveNpcAI;

/**
 * Last modified: March 24th, 2018
 * 
 * @author Ritsu, Estrayl
 */
@AIName("berserk_anoha")
public class BerserkAnohaAI extends AggressiveNpcAI {

	private SiegeRace occupier;

	public BerserkAnohaAI(Npc owner) {
		super(owner);
	}

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		scheduleDespawn();
		occupier = SiegeService.getInstance().getFortress(7011).getRace();
	}

	private void scheduleDespawn() {
		getOwner().getController().addTask(TaskId.DESPAWN, ThreadPoolManager.getInstance().schedule(() -> {
			if (!isDead()) {
				getOwner().getController().delete();
				broadcastAnnounce(SM_SYSTEM_MESSAGE.STR_MSG_ANOHA_DESPAWN());
			}
		}, 60 * 60000)); // 1hour
	}

	@Override
	protected void handleDespawned() {
		Npc flag = getOwner().getPosition().getWorldMapInstance().getNpc(702618); // see AnohasSword AI
		if (flag != null)
			flag.getController().delete();
		super.handleDespawned();
	};

	@Override
	protected void handleDied() {
		getOwner().getController().cancelTask(TaskId.DESPAWN);
		broadcastAnnounce(SM_SYSTEM_MESSAGE.STR_MSG_ANOHA_DIE());
		checkForFactionReward();
		super.handleDied();
	}

	private void checkForFactionReward() {
		Npc ca = (Npc) spawn(occupier == SiegeRace.ASMODIANS ? 804594 : 804595, 785.4833f, 458.4128f, 143.7177f, (byte) 30); // Commander Anoha
		ca.getController().addTask(TaskId.DESPAWN, ThreadPoolManager.getInstance().schedule(() -> ca.getController().delete(), 60, TimeUnit.MINUTES));
	}

	private void broadcastAnnounce(SM_SYSTEM_MESSAGE msg) {
		World.getInstance().forEachPlayer(player -> PacketSendUtility.sendPacket(player, msg));
	}

	@Override
	protected void handleCreatureSee(Creature creature) {
		super.handleCreatureSee(creature);
		if (creature instanceof Player) {
			if (occupier == SiegeRace.ASMODIANS) {
				startQuest((Player) creature, creature.getRace() == Race.ELYOS ? 13818 : 23817);
			} else if (occupier == SiegeRace.ELYOS) {
				startQuest((Player) creature, creature.getRace() == Race.ELYOS ? 13817 : 23818);
			}
		}
	}

	@Override
	public boolean ask(AIQuestion question) {
		switch (question) {
			case SHOULD_DECAY:
			case SHOULD_RESPAWN:
			case SHOULD_LOOT:
				return false;
		}
		return super.ask(question);
	}

	private void startQuest(Player player, int questId) {
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestEnv env = new QuestEnv(null, player, questId);
		if (qs == null || qs.isStartable())
			QuestService.startQuest(env);
	}
}