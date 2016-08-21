package mysql5;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.dao.CommandsAccessDAO;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;

import javolution.util.FastTable;

/**
 * @author ViAl
 */
public class MySQL5CommandsAccessDAO extends CommandsAccessDAO {

	private static final Logger log = LoggerFactory.getLogger(MySQL5CommandsAccessDAO.class);

	private static final String LOAD_QUERY = "SELECT * FROM commands_access";
	private static final String INSERT_QUERY = "INSERT INTO commands_access(player_id, command) VALUES (?,?)";
	private static final String DELETE_QUERY = "DELETE FROM commands_access WHERE player_id = ? AND command = ?";
	private static final String DELETE_ALL_QUERY = "DELETE FROM commands_access WHERE player_id = ?";

	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion) {
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}

	@Override
	public Map<Integer, List<String>> loadAccesses() {
		Map<Integer, List<String>> accesses = new HashMap<>();
		try (Connection conn = DatabaseFactory.getConnection();
			PreparedStatement stmt = conn.prepareStatement(LOAD_QUERY);
			ResultSet rset = stmt.executeQuery()) {
			while (rset.next()) {
				int playerId = rset.getInt("player_id");
				String command = rset.getString("command");
				if (!accesses.containsKey(playerId)) {
					accesses.put(playerId, new FastTable<String>());
				}
				accesses.get(playerId).add(command);
			}
		} catch (Exception e) {
			log.error("Error while loading commands accesses.", e);
		}
		return accesses;
	}

	@Override
	public void addAccess(int playerId, String commandName) {
		try (Connection conn = DatabaseFactory.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_QUERY)) {
			stmt.setInt(1, playerId);
			stmt.setString(2, commandName);
			stmt.executeUpdate();
		} catch (Exception e) {
			log.error("Error while adding access on command " + commandName + " to player " + playerId, e);
		}
	}

	@Override
	public void removeAccess(int playerId, String commandName) {
		try (Connection conn = DatabaseFactory.getConnection(); PreparedStatement stmt = conn.prepareStatement(DELETE_QUERY)) {
			stmt.setInt(1, playerId);
			stmt.setString(2, commandName);
			stmt.executeUpdate();
		} catch (Exception e) {
			log.error("Error while removing access on command " + commandName + " from player " + playerId, e);
		}
	}

	@Override
	public void removeAllAccesses(int playerId) {
		try (Connection conn = DatabaseFactory.getConnection(); PreparedStatement stmt = conn.prepareStatement(DELETE_ALL_QUERY)) {
			stmt.setInt(1, playerId);
			stmt.executeUpdate();
		} catch (Exception e) {
			log.error("Error while removing all accesses from player " + playerId, e);
		}
	}

}
