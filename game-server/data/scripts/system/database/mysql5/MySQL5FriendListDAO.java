package mysql5;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.FriendListDAO;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.model.gameobjects.player.Friend;
import com.aionemu.gameserver.model.gameobjects.player.FriendList;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.world.World;

/**
 * @author Ben
 */
public class MySQL5FriendListDAO extends FriendListDAO {

	private static final Logger log = LoggerFactory.getLogger(MySQL5FriendListDAO.class);
	public static final String LOAD_QUERY = "SELECT * FROM `friends` WHERE `player`=?";
	public static final String ADD_QUERY = "INSERT INTO `friends` (`player`,`friend`) VALUES (?, ?)";
	public static final String DEL_QUERY = "DELETE FROM friends WHERE player = ? AND friend = ?";
	public static final String SET_MEMO_QUERY = "UPDATE friends SET memo=? WHERE player=? AND friend=?";

	@Override
	public FriendList load(final Player player) {
		final List<Friend> friends = new ArrayList<Friend>();
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(LOAD_QUERY);
			stmt.setInt(1, player.getObjectId());
			ResultSet rset = stmt.executeQuery();
			PlayerDAO dao = DAOManager.getDAO(PlayerDAO.class);
			while (rset.next()) {
				int objId = rset.getInt("friend");
				Player temp;
				PlayerCommonData pcd;
				if ((temp = World.getInstance().findPlayer(objId)) != null) {
					pcd = temp.getCommonData();
				} else {
					pcd = dao.loadPlayerCommonData(objId);
				}
				if (pcd != null) {
					Friend friend = new Friend(pcd, rset.getString("memo"));
					friends.add(friend);
				}
			}
		} catch (Exception e) {
			log.error("Could not restore FriendList data for player: " + player.getObjectId() + " from DB: " + e.getMessage(), e);
		} finally {
			DatabaseFactory.close(con);
		}

		return new FriendList(player, friends);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addFriends(final Player player, final Player friend) {
		return DB.insertUpdate(ADD_QUERY, new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement ps) throws SQLException {
				ps.setInt(1, player.getObjectId());
				ps.setInt(2, friend.getObjectId());
				ps.addBatch();

				ps.setInt(1, friend.getObjectId());
				ps.setInt(2, player.getObjectId());
				ps.addBatch();

				ps.executeBatch();
			}
		});

	}

	@Override
	public boolean delFriends(final int playerOid, final int friendOid) {
		return DB.insertUpdate(DEL_QUERY, new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement ps) throws SQLException {
				ps.setInt(1, playerOid);
				ps.setInt(2, friendOid);
				ps.addBatch();

				ps.setInt(1, friendOid);
				ps.setInt(2, playerOid);
				ps.addBatch();

				ps.executeBatch();
			}
		});
	}

	@Override
	public boolean setFriendMemo(final int playerOid, final int friendOid, final String memo) {
		return DB.insertUpdate(SET_MEMO_QUERY, new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException {
				stmt.setString(1, memo);
				stmt.setInt(2, playerOid);
				stmt.setInt(3, friendOid);
				stmt.execute();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supports(String s, int i, int i1) {
		return MySQL5DAOUtils.supports(s, i, i1);
	}
}
