package mysql5;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerEmotionListDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.emotion.Emotion;
import com.aionemu.gameserver.model.gameobjects.player.emotion.EmotionList;

/**
 * @author Mr. Poke
 */
public class MySQL5PlayerEmotionListDAO extends PlayerEmotionListDAO {

	/** Logger */
	private static final Logger log = LoggerFactory.getLogger(PlayerEmotionListDAO.class);
	public static final String INSERT_QUERY = "INSERT INTO `player_emotions` (`player_id`, `emotion`, `remaining`) VALUES (?,?,?)";
	public static final String SELECT_QUERY = "SELECT `emotion`, `remaining` FROM `player_emotions` WHERE `player_id`=?";
	public static final String DELETE_QUERY = "DELETE FROM `player_emotions` WHERE `player_id`=? AND `emotion`=?";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion) {
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}

	@Override
	public void loadEmotions(Player player) {
		EmotionList emotions = new EmotionList(player);
		try {
			try (Connection con = DatabaseFactory.getConnection(); PreparedStatement stmt = con.prepareStatement(SELECT_QUERY)) {
				stmt.setInt(1, player.getObjectId());
				try (ResultSet rset = stmt.executeQuery()) {
					while (rset.next()) {
						int emotionId = rset.getInt("emotion");
						int remaining = rset.getInt("remaining");
						emotions.add(emotionId, remaining, false);
					}
				}
			}
		} catch (Exception e) {
			log.error("Could not restore emotionId for playerObjId: " + player.getObjectId() + " from DB: " + e.getMessage(), e);
		}
		player.setEmotions(emotions);
	}

	@Override
	public void insertEmotion(Player player, Emotion emotion) {
		try {
			try (Connection con = DatabaseFactory.getConnection(); PreparedStatement stmt = con.prepareStatement(INSERT_QUERY)) {
				stmt.setInt(1, player.getObjectId());
				stmt.setInt(2, emotion.getId());
				stmt.setInt(3, emotion.getExpireTime());
				stmt.execute();
			}
		} catch (Exception e) {
			log.error("Could not store emotionId for player " + player.getObjectId() + " from DB: " + e.getMessage(), e);
		}
	}

	@Override
	public void deleteEmotion(int playerId, int emotionId) {
		try {
			try (Connection con = DatabaseFactory.getConnection(); PreparedStatement stmt = con.prepareStatement(DELETE_QUERY)) {
				stmt.setInt(1, playerId);
				stmt.setInt(2, emotionId);
				stmt.execute();
			}
		} catch (Exception e) {
			log.error("Could not delete title for player " + playerId + " from DB: " + e.getMessage(), e);
		}
	}
}
