package admincommands;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.cache.HTMLCache;
import com.aionemu.gameserver.configs.main.EventsConfig;
import com.aionemu.gameserver.dao.HeadhuntingDAO;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.event.Headhunter;
import com.aionemu.gameserver.model.gameobjects.LetterType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.templates.rewards.RewardItem;
import com.aionemu.gameserver.services.HTMLService;
import com.aionemu.gameserver.services.PvpService;
import com.aionemu.gameserver.services.mail.SystemMailService;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

import javolution.util.FastMap;
import javolution.util.FastTable;

/**
 * Created on 01.06.2016
 * A command that handles analysis and rewarding for seasonal head hunting events.
 * Take care when using the reward parameter, it will clean all references including the database tables.
 * 
 * @author Estrayl
 * @since AION 4.8
 */
public class Headhunting extends AdminCommand {

	private static final Logger log = LoggerFactory.getLogger("HEADHUNTING_LOG");

	/**
	 * Contains the rewards for different rankings (key value).
	 */
	private final Map<Integer, List<RewardItem>> rewards = new FastMap<>();

	/**
	 * Contains a sorted descending list of {@link Headhunter} by kills per {@link PlayerClass}.
	 */
	private Map<Race, Map<PlayerClass, List<Headhunter>>> results;

	public Headhunting() {
		super("headhunting");
		setParamInfo(
			"<analyze> - Analyzes the season.",
			"<clear> - Clears the analayzed results",
			"<show> <rewards|results> - Shows the registered rewards or analyzed results",
			"<reward> - Executes the reward algorithm and clears all references (cached headhunters and database entries)."
		);

		/* Initialize seasonal headhunting rewards */
		rewards.put(1, new FastTable<>());
		rewards.put(2, new FastTable<>());
		rewards.put(3, new FastTable<>());
		rewards.put(4, new FastTable<>()); // equivalent for consolation prize

		rewards.get(1).add(new RewardItem(186000242, 50)); // Ceramium Medal
		rewards.get(1).add(new RewardItem(186000051, 30)); // Major Ancient Crown
		rewards.get(1).add(new RewardItem(188051531, 3)); // Arena Ticket Supply Box

		rewards.get(2).add(new RewardItem(186000242, 20)); // Ceramium Medal
		rewards.get(2).add(new RewardItem(186000051, 15)); // Major Ancient Crown
		rewards.get(2).add(new RewardItem(188051531, 2)); // Arena Ticket Supply Box

		rewards.get(3).add(new RewardItem(186000242, 10)); // Ceramium Medal
		rewards.get(3).add(new RewardItem(186000051, 5)); // Major Ancient Crown
		rewards.get(3).add(new RewardItem(188051531, 1)); // Arena Ticket Supply Box

		rewards.get(4).add(new RewardItem(186000051, 1)); // Major Ancient Crown
	}

	@Override
	protected void execute(Player admin, String... params) {
		if (params.length == 0) {
			sendInfo(admin);
			return;
		}
		switch (params[0]) {
			case "analyze":
				if (analyzeSeason(admin))
					sendInfo(admin, "Season successfully analyzed.");
				break;
			case "clear":
				if (results != null) {
					results.clear();
					results = null;
					sendInfo(admin, "Results successfully cleared.");
				}
				break;
			case "show":
				if (params.length == 1)
					sendInfo(admin);
				else if (params[1].equalsIgnoreCase("rewards"))
					showRewards(admin);
				else if (params[1].equalsIgnoreCase("results"))
					showResults(admin);
				break;
			case "reward":
				rewardPlayers(admin);
				break;
			default:
				sendInfo(admin);
		}
	}

	private boolean analyzeSeason(Player admin) {
		if (results != null) {
			sendInfo(admin, "Season is still ascertained. Use <show> paramater to get a survey with the final information "
				+ "or use <reward> to execute the reward progress.");
			return false;
		}
		final Map<Integer, Headhunter> headhunters = PvpService.getInstance().getAllHeadhunters();
		results = new EnumMap<>(Race.class);
		results.put(Race.ASMODIANS, new EnumMap<>(PlayerClass.class));
		results.put(Race.ELYOS, new EnumMap<>(PlayerClass.class));
		for (Headhunter hunter : headhunters.values()) {
			PlayerCommonData pcd = DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonData(hunter.getHunterId());
			PlayerClass pc = pcd.getPlayerClass();
			Race race = pcd.getRace();
			results.get(race).putIfAbsent(pc, new FastTable<>());
			List<Headhunter> hunterz = results.get(race).get(pc);
			hunterz.add(hunter);
			Collections.sort(hunterz);
		}
		return true;
	}

	private void showResults(Player admin) {
		if (results == null) {
			sendInfo(admin, "There are no results available! The headhunting season needs to be analyzed before rewarding.");
			return;
		} else if (results.isEmpty()) {
			results = null;
			sendInfo(admin, "No players participated, yet. Empty reward map was cleared.");
			return;
		}
		StringBuilder builder = new StringBuilder();
		for (Race race : results.keySet()) {
			Map<PlayerClass, List<Headhunter>> raceMap = results.get(race);
			builder.append("<hr><center>" + race + "</center>");
			for (PlayerClass pc : raceMap.keySet()) {
				builder.append("<br><br>" + pc.toString() + "<br>");
				List<Headhunter> hunterz = raceMap.get(pc);
				for (int pos = 0; pos < hunterz.size(); pos++) {
					final Headhunter hunter = hunterz.get(pos);
					if (pos > 2 && hunter.getKills() < EventsConfig.HEADHUNTING_CONSOLATION_PRIZE_KILLS)
						break;

					String name = DAOManager.getDAO(PlayerDAO.class).getPlayerNameByObjId(hunter.getHunterId());
					builder.append("<br>" + (pos + 1) + ". " + name + " Kills: " + hunter.getKills());
				}
			}
		}
		HTMLService.showHTML(admin, HTMLCache.getInstance().getHTML("headhunting.xhtml").replace("HUNTERZ", builder));
	}

	private void showRewards(Player admin) {
		if (rewards.isEmpty()) {
			sendInfo(admin, "There are no rewards available!");
			return;
		}
		StringBuilder builder = new StringBuilder();
		builder.append("<hr><center>Rewards</center><br><hr>");
		for (Integer rank : rewards.keySet()) {
			builder.append("<br><br><br>Rank: " + rank + "<br>");
			List<RewardItem> items = rewards.get(rank);
			for (RewardItem item : items)
				builder.append("<br>" + item.getCount() + "x " + DataManager.ITEM_DATA.getItemTemplate(item.getId()).getName());
		}
		HTMLService.showHTML(admin, HTMLCache.getInstance().getHTML("headhunting.xhtml").replace("HUNTERZ", builder));
	}

	/**
	 * Rewards the analyzed players, clears all references and archives the final results.
	 */
	private void rewardPlayers(Player admin) {
		if (EventsConfig.ENABLE_HEADHUNTING) {
			sendInfo(admin, "Season is still active! You have to disable headhunting to reward participants.");
			return;
		} else if (results == null) {
			sendInfo(admin, "No results available! The headhunting season needs to be analyzed before rewarding.");
			return;
		} else if (results.isEmpty()) {
			results = null;
			sendInfo(admin, "No participation in this season. Empty reward map was cleared.");
			return;
		}
		int rewardedPlayers = 0;
		int sentMails = 0;
		for (Race race : results.keySet()) {
			Map<PlayerClass, List<Headhunter>> raceMap = results.get(race);
			for (PlayerClass pc : raceMap.keySet()) {
				List<Headhunter> hunterz = raceMap.get(pc);
				for (int pos = 0; pos < hunterz.size(); pos++) {
					final Headhunter hunter = hunterz.get(pos);
					List<RewardItem> items;
					if (pos < 3)
						items = rewards.get(pos + 1);
					else if (hunter.getKills() >= EventsConfig.HEADHUNTING_CONSOLATION_PRIZE_KILLS)
						items = rewards.get(4);
					else
						break;

					String name = DAOManager.getDAO(PlayerDAO.class).getPlayerNameByObjId(hunter.getHunterId());
					String rank;
					switch (pos) {
						case 0:
							rank = "1st";
							break;
						case 1:
							rank = "2nd";
							break;
						case 2:
							rank = "3rd";
							break;
						default:
							rank = "consolation";
					}
					for (RewardItem item : items) {
						if (SystemMailService.getInstance().sendMail("Headhunting Corp", name, "Rewards",
							"We congratulate you for reaching the " + rank + " rank in this season with a total of " + hunter.getKills() + " kills.", item.getId(),
							item.getCount(), 0, LetterType.BLACKCLOUD)) {
							sentMails++;
						} else {
							log.error("Failed to send reward mail to player " + name + " for rank " + rank + ". (ItemId: " + item.getId() + " Count: "
								+ item.getCount() + ").");
						}
					}
					log.info("[Race: " + race + "] [PlayerClass: " + pc + "] [Rank: " + rank + "] [RewardedPlayer: " + name + "] [Kills: " + hunter.getKills()
						+ "]");
					rewardedPlayers++;
				}
			}
		}
		sendInfo(admin, "Successfully rewarded " + rewardedPlayers + " Players and sent " + sentMails + " Mails.");
		PvpService.getInstance().finalizeHeadhuntingSeason();
		DAOManager.getDAO(HeadhuntingDAO.class).clearTables();
		results.clear();
		results = null;
		sendInfo(admin, "Successfully cleared all references for this season and finished archiving.");
	}
}
