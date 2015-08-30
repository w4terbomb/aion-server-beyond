package com.aionemu.gameserver.model.siege;


/**
 * @author ViAl
 *
 */
public enum GloryPointsRewards {

	Divine_Fortress_1(1011, 1, 12, 200, 100),
	Divine_Fortress_2(1011, 2, 30, 150, 75),
	Divine_Fortress_3(1011, 3, 50, 100, 50),
	Divine_Fortress_4(1011, 4, 200, 100, 50),

	KROTAN_1(1221, 1, 12, 200, 100),
	KROTAN_2(1221, 2, 30, 150, 75),
	KROTAN_3(1221, 3, 50, 150, 50),
	KROTAN_4(1221, 4, 200, 100, 50),
	
	KYSIS_1(1231, 1, 12, 200, 100),
	KYSIS_2(1231, 2, 30, 150, 75),
	KYSIS_3(1231, 3, 50, 150, 50),
	KYSIS_4(1231, 4, 200, 100, 50),
	
	MIREN_1(1241, 1, 12, 200, 100),
	MIREN_2(1241, 2, 30, 150, 75),
	MIREN_3(1241, 3, 50, 150, 50),
	MIREN_4(1241, 4, 200, 100, 50),	

	Temple_of_Scales_1(2011, 1, 12, 200, 100),
	Temple_of_Scales_2(2011, 2, 30, 150, 75),
	Temple_of_Scales_3(2011, 3, 50, 100, 50),
	Temple_of_Scales_4(2011, 4, 200, 100, 50),
	
	Altar_of_Avarice_1(2021, 1, 12, 200, 100),
	Altar_of_Avarice_2(2021, 2, 30, 150, 75),
	Altar_of_Avarice_3(2021, 3, 50, 100, 50),
	Altar_of_Avarice_4(2021, 4, 200, 100, 50),
	
	Citadel_1(3011, 1, 12, 200, 100),
	Citadel_2(3011, 2, 30, 150, 75),
	Citadel_3(3011, 3, 50, 100, 50),
	Citadel_4(3011, 4, 200, 100, 50),
	
	Crimson_Temple_1(3021, 1, 12, 200, 100),
	Crimson_Temple_2(3021, 2, 30, 150, 75),
	Crimson_Temple_3(3021, 3, 50, 100, 50),
	Crimson_Temple_4(3021, 4, 200, 100, 50),
	
	SILLUS_1(5011, 1, 12, 300, 100),
	SILLUS_2(5011, 2, 30, 250, 75),
	SILLUS_3(5011, 3, 50, 200, 50),
	SILLUS_4(5011, 4, 200, 100, 50),
	
	SILONA_1(6011, 1, 12, 300, 100),
	SILONA_2(6011, 2, 30, 250, 75),
	SILONA_3(6011, 3, 50, 200, 50),
	SILONA_4(6011, 4, 200, 100, 50),
	
	PRADETH_1(6021, 1, 12, 300, 100),
	PRADETH_2(6021, 2, 30, 250, 75),
	PRADETH_3(6021, 3, 50, 200, 50),
	PRADETH_4(6021, 4, 200, 100, 50),
	
	WEALHTHEOW_1(7011, 1, 12, 300, 100),
	WEALHTHEOW_2(7011, 2, 30, 250, 75),
	WEALHTHEOW_3(7011, 3, 50, 200, 50),
	WEALHTHEOW_4(7011, 4, 200, 100, 50);

	
	private int siegeId;
	private int winPlace;
	private int playersCount;
	private int gpForWin;
	private int gpForLost;
	
	/**
	 * @param siegeId
	 * @param winPlace
	 * @param gpForWin
	 * @param gpForLost
	 */
	private GloryPointsRewards(int siegeId, int winPlace, int playersCount, int gpForWin, int gpForLost) {
		this.siegeId = siegeId;
		this.winPlace = winPlace;
		this.playersCount = playersCount;
		this.gpForWin = gpForWin;
		this.gpForLost = gpForLost;
	}
	
	public int getSiegeId() {
		return siegeId;
	}

	public int getWinPlace() {
		return winPlace;
	}
	
	public int getPlayersCount() {
		return playersCount;
	}

	public int getGpForWin() {
		return gpForWin;
	}

	public int getGpForLost() {
		return gpForLost;
	}

	public static boolean hasRewardForSiege(int siegeId) {
		for(GloryPointsRewards reward : values()) {
			if(reward.getSiegeId() == siegeId)
				return true;
		}
		return false;
	}
	
	public static GloryPointsRewards getReward(int siegeId, int winPlace) {
		for(GloryPointsRewards reward : values()) {
			if(reward.getSiegeId() == siegeId && reward.getWinPlace() == winPlace)
				return reward;
		}
		return null;
	}
}
