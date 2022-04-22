package com.aionemu.gameserver.network.aion;

import java.util.HashMap;
import java.util.Map;

import com.aionemu.gameserver.network.aion.serverpackets.*;

/**
 * This class is holding opcodes for all server packets. It's used only to have all opcodes in one place
 *
 * @author Luno, alexa026, ATracer, avol, orz, cura
 */
public class ServerPacketsOpcodes {

	private static Map<Class<? extends AionServerPacket>, Integer> opcodes = new HashMap<>();

	static {
		addPacketOpcode(0, SM_VERSION_CHECK.class); // [S_VERSION_CHECK]
		addPacketOpcode(1, SM_STATS_INFO.class); // [S_STATUS]
		addPacketOpcode(2, SM_GM_SHOW_PLAYER_STATUS.class); // [S_STATUS_OTHER]
		addPacketOpcode(3, SM_STATUPDATE_HP.class); // [S_HIT_POINT]
		addPacketOpcode(4, SM_STATUPDATE_MP.class); // [S_MANA_POINT]
		addPacketOpcode(5, SM_ATTACK_STATUS.class); // [S_HIT_POINT_OTHER]
		addPacketOpcode(6, SM_STATUPDATE_DP.class); // [S_DP]
		addPacketOpcode(7, SM_DP_INFO.class); // [S_DP_USER]
		addPacketOpcode(8, SM_STATUPDATE_EXP.class); // [S_EXP]
		// addPacketOpcode(9, ); // [S_LOGIN_CHECK]
		addPacketOpcode(10, SM_NPC_ASSEMBLER.class); // [S_CUTSCENE_NPC_INFO]
		addPacketOpcode(11, SM_LEGION_UPDATE_NICKNAME.class); // [S_CHANGE_GUILD_MEMBER_NICKNAME]
		addPacketOpcode(12, SM_LEGION_TABS.class); // [S_GUILD_HISTORY]
		addPacketOpcode(13, SM_ENTER_WORLD_CHECK.class); // [S_ENTER_WORLD_CHECK]
		addPacketOpcode(14, SM_NPC_INFO.class); // [S_PUT_NPC]
		addPacketOpcode(15, SM_PLAYER_SPAWN.class); // [S_WORLD]
		// addPacketOpcode(16, ); // [S_DUMMY_PACKET]
		addPacketOpcode(17, SM_GATHERABLE_INFO.class); // [S_PUT_OBJECT]
		// addPacketOpcode(18, ); // [S_PUT_VEHICLE]
		addPacketOpcode(19, SM_GM_SEARCH.class); // [S_BUILDER_RESULT]
		addPacketOpcode(20, SM_TELEPORT_LOC.class); // [S_REQUEST_TELEPORT]
		addPacketOpcode(21, SM_PLAYER_MOVE.class); // [S_BLINK]
		addPacketOpcode(22, SM_DELETE.class); // [S_REMOVE_OBJECT]
		addPacketOpcode(23, SM_LOGIN_QUEUE.class); // [S_WAIT_LIST]
		addPacketOpcode(24, SM_MESSAGE.class); // [S_MESSAGE]
		addPacketOpcode(25, SM_SYSTEM_MESSAGE.class); // [S_MESSAGE_CODE]
		addPacketOpcode(26, SM_INVENTORY_INFO.class); // [S_LOAD_INVENTORY]
		addPacketOpcode(27, SM_INVENTORY_ADD_ITEM.class); // [S_ADD_INVENTORY]
		addPacketOpcode(28, SM_DELETE_ITEM.class); // [S_REMOVE_INVENTORY]
		addPacketOpcode(29, SM_INVENTORY_UPDATE_ITEM.class); // [S_CHANGE_ITEM_DESC]
		addPacketOpcode(30, SM_UI_SETTINGS.class); // [S_LOAD_CLIENT_SETTINGS]
		addPacketOpcode(31, SM_PLAYER_STANCE.class); // [S_CHANGE_STANCE]
		addPacketOpcode(32, SM_PLAYER_INFO.class); // [S_PUT_USER]
		addPacketOpcode(33, SM_CASTSPELL.class); // [S_USE_SKILL]
		addPacketOpcode(34, SM_GATHER_ANIMATION.class); // [S_GATHER_OTHER]
		addPacketOpcode(35, SM_GATHER_UPDATE.class); // [S_GATHER]
		addPacketOpcode(36, SM_UPDATE_PLAYER_APPEARANCE.class); // [S_WIELD]
		addPacketOpcode(37, SM_EMOTION.class); // [S_ACTION]
		addPacketOpcode(38, SM_GAME_TIME.class); // [S_TIME]
		addPacketOpcode(39, SM_TIME_CHECK.class); // [S_SYNC_TIME]
		addPacketOpcode(40, SM_LOOKATOBJECT.class); // [S_NPC_CHANGED_TARGET]
		addPacketOpcode(41, SM_TARGET_SELECTED.class); // [S_TARGET_INFO]
		addPacketOpcode(42, SM_SKILL_CANCEL.class); // [S_SKILL_CANCELED]
		addPacketOpcode(43, SM_CASTSPELL_RESULT.class); // [S_SKILL_SUCCEDED]
		addPacketOpcode(44, SM_SKILL_LIST.class); // [S_ADD_SKILL]
		addPacketOpcode(45, SM_SKILL_REMOVE.class); // [S_DELETE_SKILL]
		addPacketOpcode(46, SM_SKILL_ACTIVATION.class); // [S_TOGGLE_SKILL_ON_OFF]
		// addPacketOpcode(47, ); // [S_ADD_MAINTAIN_SKILL]
		// addPacketOpcode(48, ); // [S_DELETE_MAINTAIN_SKILL]
		addPacketOpcode(49, SM_ABNORMAL_STATE.class); // [S_ABNORMAL_STATUS]
		addPacketOpcode(50, SM_ABNORMAL_EFFECT.class); // [S_ABNORMAL_STATUS_OTHER]
		addPacketOpcode(51, SM_SKILL_COOLDOWN.class); // [S_LOAD_SKILL_COOLTIME]
		addPacketOpcode(52, SM_QUESTION_WINDOW.class); // [S_ASK]
		addPacketOpcode(53, SM_CLOSE_QUESTION_WINDOW.class); // [S_CANCEL_ASK]
		addPacketOpcode(54, SM_ATTACK.class); // [S_ATTACK]
		addPacketOpcode(55, SM_MOVE.class); // [S_MOVE_NEW]
		// addPacketOpcode(56, ); // [S_MOVE_OBJECT]
		addPacketOpcode(57, SM_HEADING_UPDATE.class); // [S_CHANGE_DIRECTION]
		addPacketOpcode(58, SM_TRANSFORM.class); // [S_POLYMORPH]
		addPacketOpcode(59, SM_GM_SHOW_PLAYER_SKILLS.class); // [S_SKILL_OTHER]
		addPacketOpcode(60, SM_DIALOG_WINDOW.class); // [S_NPC_HTML_MESSAGE]
		addPacketOpcode(61, SM_HOUSE_UPDATE.class); // [S_PUT_BUILDINGS]
		addPacketOpcode(62, SM_SELL_ITEM.class); // [S_STORE_PURCHASE_INFO]
		addPacketOpcode(63, SM_GM_SHOW_LEGION_INFO.class); // [S_GUILD_OTHER_INFO]
		addPacketOpcode(64, SM_GM_BOOKMARK_ADD.class); // [S_ADD_BOOKMARK]
		addPacketOpcode(65, SM_VIEW_PLAYER_DETAILS.class); // [S_ITEM_LIST]
		addPacketOpcode(66, SM_GM_SHOW_LEGION_MEMBERLIST.class); // [S_GUILD_OTHER_MEMBER_INFO]
		addPacketOpcode(67, SM_WEATHER.class); // [S_WEATHER]
		addPacketOpcode(68, SM_PLAYER_STATE.class); // [S_INVISIBLE_LEVEL]
		// addPacketOpcode(69, ); // [S_RECALLED_BY_OTHER] SM_SUMMON_TELEPORT_REQUEST some teleport/summon dialog. response when accepting/declining the dialog is CM_SUMMON_TELEPORT_RESPONSE (opcode 195). first D in SM and CM packets is identical (dialog ID?) //fsc 69 cshh unk name skill_id time_seconds
		addPacketOpcode(70, SM_ACTION_ANIMATION.class); // [S_EFFECT]
		addPacketOpcode(71, SM_QUEST_LIST.class); // [S_LOAD_WORKINGQUEST]
		addPacketOpcode(72, SM_KEY.class); // [S_KEY]
		addPacketOpcode(73, SM_SUMMON_PANEL_REMOVE.class); // [S_RESET_SKILL_COOLING_TIME]
		addPacketOpcode(74, SM_EXCHANGE_REQUEST.class); // [S_XCHG_START]
		addPacketOpcode(75, SM_EXCHANGE_ADD_ITEM.class); // [S_ADD_XCHG]
		// addPacketOpcode(76, ); // [S_REMOVE_XCHG]
		addPacketOpcode(77, SM_EXCHANGE_ADD_KINAH.class); // [S_XCHG_GOLD]
		addPacketOpcode(78, SM_EXCHANGE_CONFIRMATION.class); // [S_XCHG_RESULT]
		addPacketOpcode(79, SM_EMOTION_LIST.class); // [S_ADDREMOVE_SOCIAL]
		// addPacketOpcode(80, ); // [S_CHECK_MESSAGE] client answers with C_CHECK_MESSAGE (opcode 190)
		addPacketOpcode(81, SM_TARGET_UPDATE.class); // [S_USER_CHANGED_TARGET]
		addPacketOpcode(82, SM_HOUSE_EDIT.class); // [S_HOUSING_OBJECT_CMD]
		addPacketOpcode(83, SM_PLASTIC_SURGERY.class); // [S_EDIT_CHARACTER]
		addPacketOpcode(84, SM_CONQUEROR_PROTECTOR.class); // [S_SERIAL_KILLER_LIST]
		addPacketOpcode(85, SM_INFLUENCE_RATIO.class); // [S_ABYSS_NEXT_PVP_CHANGE_TIME]
		addPacketOpcode(86, SM_FORTRESS_STATUS.class); // [S_ABYSS_CHANGE_NEXT_PVP_STATUS]
		addPacketOpcode(87, SM_CAPTCHA.class); // [S_CAPTCHA]
		addPacketOpcode(88, SM_RENAME.class); // [S_ADDED_SERVICE_CHANGE]
		addPacketOpcode(89, SM_SHOW_NPC_ON_MAP.class); // [S_FIND_NPC_POS_RESULT]
		addPacketOpcode(90, SM_GROUP_INFO.class); // [S_PARTY_INFO]
		addPacketOpcode(91, SM_GROUP_MEMBER_INFO.class); // [S_PARTY_MEMBER_INFO]
		addPacketOpcode(92, SM_RIDE_ROBOT.class); // [S_RIDE_ROBOT]
		// addPacketOpcode(93, ); // [S_UNUSED__05]
		// addPacketOpcode(94, ); // [S_UNUSED__06]
		// addPacketOpcode(95, ); // [S_UNUSED__07]
		// addPacketOpcode(96, ); // [S_UNUSED__14]
		// addPacketOpcode(97, ); // [S_GGAUTH_CHECK_QUERY]
		addPacketOpcode(98, SM_QUIT_RESPONSE.class); // [S_ASK_QUIT_RESULT]
		addPacketOpcode(99, SM_CHAT_WINDOW.class); // [S_ASK_INFO_RESULT] 2.1
		// addPacketOpcode(100, ); // [S_FATIGUE_INFO]
		addPacketOpcode(101, SM_PET.class); // [S_FUNCTIONAL_PET] 2.7
		// addPacketOpcode(102, ); // [S_QUERY_NUMBER]
		addPacketOpcode(103, SM_ITEM_COOLDOWN.class); // [S_LOAD_ITEM_COOLTIME] 2.7
		addPacketOpcode(104, SM_UPDATE_NOTE.class); // [S_TODAY_WORDS]
		addPacketOpcode(105, SM_PLAY_MOVIE.class); // [S_PLAY_CUTSCENE]
		// addPacketOpcode(106, ); // [S_GET_ON_VEHICLE]
		// addPacketOpcode(107, ); // [S_GET_OFF_VEHICLE]
		// addPacketOpcode(108, ); // [S_HOUSING_OBJECT_LIST]
		// addPacketOpcode(109, ); // [S_KICK] sends error message boxes like "Authorization error" (//fsc 109 c 1) or "Unknown error" (//fsc 109 c 2). clicking ok closes the client
		addPacketOpcode(110, SM_LEGION_INFO.class); // [S_GUILD_INFO] 2.7
		addPacketOpcode(111, SM_LEGION_ADD_MEMBER.class); // [S_ADD_GUILD_MEMBER]
		addPacketOpcode(112, SM_LEGION_LEAVE_MEMBER.class); // [S_DELETE_GUILD_MEMBER]
		addPacketOpcode(113, SM_LEGION_UPDATE_MEMBER.class); // [S_CHANGE_GUILD_MEMBER_INFO]
		addPacketOpcode(114, SM_LEGION_UPDATE_TITLE.class); // [S_CHANGE_GUILD_OTHER]
		addPacketOpcode(115, SM_ATTACK_RESPONSE.class); // [S_ATTACK_RESULT]
		addPacketOpcode(116, SM_HOUSE_REGISTRY.class); // [S_HOUSING_CONTAINER_LIST]
		// addPacketOpcode(117, ); // [S_DYNCODE_DATA]
		// addPacketOpcode(118, ); // [S_SNDC_CHECK_MESSAGE]
		addPacketOpcode(119, SM_LEGION_UPDATE_SELF_INTRO.class); // [S_CHANGE_GUILD_MEMBER_INTRO]
		// addPacketOpcode(120, SM_RIFT_STATUS.class); // [S_WANTED_LOGIN] 1.9
		addPacketOpcode(121, SM_INSTANCE_SCORE.class); // [S_INSTANT_DUNGEON_INFO]
		addPacketOpcode(122, SM_AUTO_GROUP.class); // [S_MATCHMAKER_INFO]
		addPacketOpcode(123, SM_QUEST_COMPLETED_LIST.class); // [S_LOAD_FINISHEDQUEST]
		addPacketOpcode(124, SM_QUEST_ACTION.class); // [S_QUEST]
		addPacketOpcode(125, SM_GAMEGUARD.class); // [S_NCGUARD]
		// addPacketOpcode(126, SM_BUY_LIST.class); // [S_UNUSED_NEW_2] 1.5.4
		addPacketOpcode(127, SM_NEARBY_QUESTS.class); // [S_UPDATE_ZONE_QUEST]
		addPacketOpcode(128, SM_PING_RESPONSE.class); // [S_PING]
		// addPacketOpcode(129, ); // [S_SHOP_RESULT]
		addPacketOpcode(130, SM_CUBE_UPDATE.class); // [S_EVENT]
		addPacketOpcode(131, SM_HOUSE_SCRIPTS.class); // [S_HOUSE_SCRIPT]
		addPacketOpcode(132, SM_FRIEND_LIST.class); // [S_BUDDY_LIST]
		// addPacketOpcode(133, ); // [S_BOOK_LIST]
		addPacketOpcode(134, SM_PRIVATE_STORE.class); // [S_SHOP_SELL_LIST]
		addPacketOpcode(135, SM_GROUP_LOOT.class); // [S_GROUP_ITEM_DIST]
		addPacketOpcode(136, SM_ABYSS_RANK_UPDATE.class); // [S_ETC_STATUS]
		addPacketOpcode(137, SM_MAY_LOGIN_INTO_GAME.class); // [S_SA_ACCOUNT_ITEM_NOTI]
		addPacketOpcode(138, SM_ABYSS_RANKING_PLAYERS.class); // [S_ABYSS_RANKER_INFOS]
		addPacketOpcode(139, SM_ABYSS_RANKING_LEGIONS.class); // [S_ABYSS_GUILD_INFOS]
		addPacketOpcode(140, SM_INSTANCE_STAGE_INFO.class); // [S_WORLD_SCENE_STATUS]
		addPacketOpcode(141, SM_INSTANCE_INFO.class); // [S_INSTANCE_DUNGEON_COOLTIMES]
		addPacketOpcode(142, SM_PONG.class); // [S_ALIVE]
		// addPacketOpcode(143, ); // [S_DEBUG_PUT_BEACON]
		addPacketOpcode(144, SM_KISK_UPDATE.class); // [S_PLACEABLE_BINDSTONE_INFO]
		addPacketOpcode(145, SM_PRIVATE_STORE_NAME.class); // [S_PERSONAL_SHOP]
		addPacketOpcode(146, SM_BROKER_SERVICE.class); // [S_VENDOR]
		addPacketOpcode(147, SM_INSTANCE_COUNT_INFO.class); // [S_ENTER_WORLD_NOTIFY]
		addPacketOpcode(148, SM_MOTION.class); // [S_CUSTOM_ANIM]
		// addPacketOpcode(149, SM_BROKER_SETTLED_LIST.class); // [S_SHOPAGENT2] Systemfehler später erneut versuchen ??
		addPacketOpcode(150, SM_UNK_3_5_1.class); // [S_RESULT_PASSPORT]
		addPacketOpcode(151, SM_TRADE_IN_LIST.class); // [S_TRADE_IN]
		addPacketOpcode(152, SM_SECURITY_TOKEN.class); // [S_REP_WEB_SESSIONKEY]
		addPacketOpcode(153, SM_SUMMON_PANEL.class); // [S_ADD_PET]
		addPacketOpcode(154, SM_SUMMON_OWNER_REMOVE.class); // [S_REMOVE_PET]
		addPacketOpcode(155, SM_SUMMON_UPDATE.class); // [S_CHANGE_PET_STATUS]
		addPacketOpcode(156, SM_TRANSFORM_IN_SUMMON.class); // [S_CHANGE_MASTER]
		addPacketOpcode(157, SM_LEGION_MEMBERLIST.class); // [S_GUILD_MEMBER_INFO]
		addPacketOpcode(158, SM_LEGION_EDIT.class); // [S_CHANGE_GUILD_INFO]
		addPacketOpcode(159, SM_TOLL_INFO.class); // [S_SHOP_POINT_INFO]
		// addPacketOpcode(160, ); // [S_CHANGE_NPC_STATUS]
		addPacketOpcode(161, SM_MAIL_SERVICE.class); // [S_MAIL]
		addPacketOpcode(162, SM_SUMMON_USESKILL.class); // [S_ALLOW_PET_USE_SKILL]
		addPacketOpcode(163, SM_WINDSTREAM.class); // [S_WIND_PATH_RESULT]
		addPacketOpcode(164, SM_WINDSTREAM_ANNOUNCE.class); // [S_WIND_STATE_INFO]
		addPacketOpcode(165, SM_RECIPE_COOLDOWN.class); // [S_LOAD_GATHERCOMBINE_COOLTIME]
		addPacketOpcode(166, SM_FIND_GROUP.class); // [S_PARTY_MATCH]
		addPacketOpcode(167, SM_REPURCHASE.class); // [S_USER_SELL_HISTORY_LIST]
		addPacketOpcode(168, SM_WAREHOUSE_INFO.class); // [S_LOAD_WAREHOUSE]
		addPacketOpcode(169, SM_WAREHOUSE_ADD_ITEM.class); // [S_ADD_WAREHOUSE]
		addPacketOpcode(170, SM_DELETE_WAREHOUSE_ITEM.class); // [S_REMOVE_WAREHOUSE]
		addPacketOpcode(171, SM_WAREHOUSE_UPDATE_ITEM.class); // [S_CHANGE_WAREHOUSE_ITEM_DESC]
		addPacketOpcode(172, SM_IN_GAME_SHOP_CATEGORY_LIST.class); // [S_SHOP_CATEGORY_INFO]
		addPacketOpcode(173, SM_IN_GAME_SHOP_LIST.class); // [S_SHOP_GOODS_LIST]
		addPacketOpcode(174, SM_IN_GAME_SHOP_ITEM.class); // [S_SHOP_GOODS_INFO]
		addPacketOpcode(175, SM_ICON_INFO.class); // [S_CONDITIONAL_BONUS_ATTR]
		addPacketOpcode(176, SM_TITLE_INFO.class); // [S_TITLE]
		addPacketOpcode(177, SM_CHARACTER_SELECT.class); // [S_2ND_PASSWORD]
		addPacketOpcode(178, SM_GROUP_DATA_EXCHANGE.class); // [S_CLIENT_BROADCAST]
		// addPacketOpcode(179, SM_BROKER_REGISTERED_LIST.class); // [S_FATIGUE_KOREA]
		addPacketOpcode(180, SM_CRAFT_ANIMATION.class); // [S_COMBINE_OTHER]
		addPacketOpcode(181, SM_CRAFT_UPDATE.class); // [S_COMBINE]
		addPacketOpcode(182, SM_ASCENSION_MORPH.class); // [S_PLAY_MODE]
		addPacketOpcode(183, SM_ITEM_USAGE_ANIMATION.class); // [S_USE_ITEM]
		addPacketOpcode(184, SM_CUSTOM_SETTINGS.class); // [S_CHANGE_FLAG]
		addPacketOpcode(185, SM_DUEL.class); // [S_DUEL]
		// addPacketOpcode(186, ); // [S_CLIENTSIDE_NPC_BLINK] client answers with C_CLIENTSIDE_NPC_BLINK (opcode 204). first D in CM and SM is identical (//fsc 186 d 1)
		addPacketOpcode(187, SM_PET_EMOTE.class); // [S_FUNCTIONAL_PET_MOVE]
		// addPacketOpcode(188, ); // [S_RECONNECT_OTHER_SERVER] destroy your ui & get kicked!
		// addPacketOpcode(189, ); // [S_LOAD_PVP_ENV]
		// addPacketOpcode(190, ); // [S_CHANGE_PVP_ENV]
		addPacketOpcode(191, SM_QUESTIONNAIRE.class); // [S_POLL_CONTENTS]
		// addPacketOpcode(192, ); // [S_GM_COMMENT] format: ddsdhs ?
		addPacketOpcode(193, SM_DIE.class); // [S_RESURRECT_INFO]
		addPacketOpcode(194, SM_RESURRECT.class); // [S_RESURRECT_BY_OTHER]
		addPacketOpcode(195, SM_FORCED_MOVE.class); // [S_MOVEBACK]
		addPacketOpcode(196, SM_TELEPORT_MAP.class); // [S_ROUTEMAP_INFO]
		addPacketOpcode(197, SM_USE_OBJECT.class); // [S_GAUGE]
		// addPacketOpcode(198, ); // [S_SHOW_NPC_MOTION] format: d - oid, cdd - smth related to SM_NPC_INFO
		addPacketOpcode(199, SM_L2AUTH_LOGIN_CHECK.class); // [S_L2AUTH_LOGIN_CHECK]
		addPacketOpcode(200, SM_CHARACTER_LIST.class); // [S_CHARACTER_LIST]
		addPacketOpcode(201, SM_CREATE_CHARACTER.class); // [S_CREATE_CHARACTER]
		addPacketOpcode(202, SM_DELETE_CHARACTER.class); // [S_DELETE_CHARACTER]
		addPacketOpcode(203, SM_RESTORE_CHARACTER.class); // [S_RESTORE_CHARACTER]
		addPacketOpcode(204, SM_TARGET_IMMOBILIZE.class); // [S_FORCE_BLINK]
		addPacketOpcode(205, SM_LOOT_STATUS.class); // [S_LOOT]
		addPacketOpcode(206, SM_LOOT_ITEMLIST.class); // [S_LOOT_ITEMLIST]
		addPacketOpcode(207, SM_RECIPE_LIST.class); // [S_RECIPE_LIST]
		addPacketOpcode(208, SM_MANTRA_EFFECT.class); // [S_SKILL_ACTIVATED]
		addPacketOpcode(209, SM_SIEGE_LOCATION_INFO.class); // [S_ABYSS_INFO]
		addPacketOpcode(210, SM_SIEGE_LOCATION_STATE.class); // [S_CHANGE_ABYSS_PVP_STATUS]
		addPacketOpcode(211, SM_PLAYER_SEARCH.class); // [S_SEARCH_USER_RESULT]
		// addPacketOpcode(212, ); // [S_GUILD_EMBLEM_UPLOAD_RESULT] TODO: Legion Wappen format: c 0 = successful 1 = already uploaded
		addPacketOpcode(213, SM_LEGION_SEND_EMBLEM.class); // [S_GUILD_EMBLEM_IMG_BEGIN]
		addPacketOpcode(214, SM_LEGION_SEND_EMBLEM_DATA.class); // [S_GUILD_EMBLEM_IMG_DATA]
		addPacketOpcode(215, SM_LEGION_UPDATE_EMBLEM.class); // [S_GUILD_EMBLEM_UPDATED]
		// addPacketOpcode(216, ); // [S_SKILL_PENALTY_STATUS] skill related //fsc 216 cccdd 1 0 0 15000 1000 (first d: icon timer, last d: skill lock timer)
		addPacketOpcode(217, SM_PLAYER_REGION.class); // [S_SKILL_PENALTY_STATUS_OTHER]
		addPacketOpcode(218, SM_SHIELD_EFFECT.class); // [S_ABYSS_SHIELD_INFO]
		// addPacketOpcode(219, ); // [S_SPECTATOR_MODE] format: d 5 -> switches UI to something unk and client responds with opcode 182 packet, 6 switches back to normal
		addPacketOpcode(220, SM_ABYSS_ARTIFACT_INFO3.class); // [S_ARTIFACT_INFO]
		addPacketOpcode(221, SM_HOUSE_TELEPORT.class); // [S_RETURN_TO_HOUSEGATE_INFO]
		addPacketOpcode(222, SM_FRIEND_RESPONSE.class); // [S_BUDDY_RESULT]
		addPacketOpcode(223, SM_BLOCK_RESPONSE.class); // [S_BLOCK_RESULT]
		addPacketOpcode(224, SM_BLOCK_LIST.class); // [S_BLOCK_LIST]
		addPacketOpcode(225, SM_FRIEND_NOTIFY.class); // [S_NOTIFY_BUDDY]
		addPacketOpcode(226, SM_TOWNS_LIST.class); // [S_TOWN_INFO_LIST]
		addPacketOpcode(227, SM_FRIEND_STATUS.class); // [S_CUR_STATUS]
		// addPacketOpcode(228, SM_VIRTUAL_AUTH.class); // [S_VIRTUAL_AUTH] 1.5.0 - client answers with CM_MAC_ADDRESS and CM_L2AUTH_LOGIN_CHECK
		addPacketOpcode(229, SM_CHANNEL_INFO.class); // [S_CHANGE_CHANNEL]
		addPacketOpcode(230, SM_CHAT_INIT.class); // [S_SIGN_CLIENT]
		addPacketOpcode(231, SM_MACRO_LIST.class); // [S_LOAD_MACRO]
		addPacketOpcode(232, SM_MACRO_RESULT.class); // [S_MACRO_RESULT]
		addPacketOpcode(233, SM_NICKNAME_CHECK_RESPONSE.class); // [S_EXIST_RESULT]
		// addPacketOpcode(234, ); // [S_EXTRA_ITEM_CHANGE_CONTEXT]
		addPacketOpcode(235, SM_BIND_POINT_INFO.class); // [S_RESURRECT_LOC_INFO]
		addPacketOpcode(236, SM_RIFT_ANNOUNCE.class); // [S_WORLD_INFO]
		addPacketOpcode(237, SM_ABYSS_RANK.class); // [S_ABYSS_POINT]
		addPacketOpcode(238, SM_ACCOUNT_PROPERTIES.class); // [S_BUILDER_LEVEL]
		// addPacketOpcode(239, ); // [S_PETITION_STATUS] petition/support - //fsc 239 cdhsdccd 1 0 42 TestNo. 1 127 100 5
		addPacketOpcode(240, SM_FRIEND_UPDATE.class); // [S_BUDDY_DATA]
		addPacketOpcode(241, SM_LEARN_RECIPE.class); // [S_ADD_RECIPE]
		addPacketOpcode(242, SM_RECIPE_DELETE.class); // [S_REMOVE_RECIPE]
		addPacketOpcode(243, SM_FORTRESS_INFO.class); // [S_CHANGE_ABYSS_TELEPORTER_STATUS]
		addPacketOpcode(244, SM_FLY_TIME.class); // [S_FLIGHT_POINT]
		addPacketOpcode(245, SM_ALLIANCE_INFO.class); // [S_ALLIANCE_INFO]
		addPacketOpcode(246, SM_ALLIANCE_MEMBER_INFO.class); // [S_ALLIANCE_MEMBER_INFO]
		addPacketOpcode(247, SM_LEAVE_GROUP_MEMBER.class); // [S_GROUP_INFO]
		// addPacketOpcode(248, ); // [S_GROUP_MEMBER_INFO]
		addPacketOpcode(249, SM_SHOW_BRAND.class); // [S_TACTICS_SIGN]
		addPacketOpcode(250, SM_ALLIANCE_READY_CHECK.class); // [S_GROUP_READY]
		// addPacketOpcode(251, ); // [S_CHAR_BM_PACK_LIST] first c or h must be size or type since nonzero leads to a client crash, because of incorrect following data
		addPacketOpcode(252, SM_PRICES.class); // [S_TAX_INFO]
		addPacketOpcode(253, SM_TRADELIST.class); // [S_STORE_SALE_INFO]
		// addPacketOpcode(254, ); // [S_INVINCIBLE_TIME]
		addPacketOpcode(255, SM_RECONNECT_KEY.class); // [S_RECONNECT_KEY]
		addPacketOpcode(256, SM_HOUSE_BIDS.class); // [S_AUCTION_LIST]
		// addPacketOpcode(257, ); // [S_AUCTION_REGISTER] TODO: Format: d "Unknown Error d"
		// addPacketOpcode(258, ); // [S_AUCTION_CANCEL]
		addPacketOpcode(259, SM_RECEIVE_BIDS.class); // [S_AUCTION_BET]
		// addPacketOpcode(260, ); // [S_WEB_NOTI]
		// addPacketOpcode(261, ); // [S_RESULT_PASSPORT_FIRST] FastTrack not available
		addPacketOpcode(262, SM_HOUSE_PAY_RENT.class); // [S_HOUSING_CHARGE_FEE]
		addPacketOpcode(263, SM_HOUSE_OWNER_INFO.class); // [S_HOUSE_INFO]
		addPacketOpcode(264, SM_OBJECT_USE_UPDATE.class); // [S_USE_HOUSING_OBJECT_RESULT]
		// addPacketOpcode(265, ); // [S_VITAL_POINT]
		addPacketOpcode(266, SM_PACKAGE_INFO_NOTIFY.class); // [S_BM_PACK_LIST]
		// addPacketOpcode(267, ); // [S_SERVER_VERTEX_LIST]
		addPacketOpcode(268, SM_HOUSE_OBJECT.class); // [S_SEE_HOUSING_OBJECT]
		addPacketOpcode(269, SM_DELETE_HOUSE_OBJECT.class); // [S_DONTSEE_HOUSING_OBJECT]
		addPacketOpcode(270, SM_HOUSE_OBJECTS.class); // [S_HOUSING_MY_OBJECT_LIST]
		addPacketOpcode(271, SM_HOUSE_RENDER.class); // [S_SEE_HOUSE]
		addPacketOpcode(272, SM_DELETE_HOUSE.class); // [S_DONTSEE_HOUSE]
		// addPacketOpcode(273, ); // [S_HOUSING_REFRESH_TOKEN_RES]
		addPacketOpcode(274, SM_GF_WEBSHOP_TOKEN_RESPONSE.class); // [S_GF_WEBSHOP_TOKEN_RES]
		addPacketOpcode(275, SM_HOUSE_ACQUIRE.class); // [S_CHANGE_GUILD_MEMBER_HOUSE]
		addPacketOpcode(276, SM_STATS_STATUS_UNK.class); // [S_SERVER_AVAILABLE_NOTIFY]
		// addPacketOpcode(277, ); // [S_LITE_CLIENT_ERROR] download parts, "client is restricted, you have to download part 1 first"
		// addPacketOpcode(278, ); // [S_NPC_EXT_INFO]
		addPacketOpcode(279, SM_MARK_FRIENDLIST.class); // [S_OFFLINE_BUDDY_LIST]
		addPacketOpcode(280, SM_CHALLENGE_LIST.class); // [S_CHALLENGE_TASK]
		// addPacketOpcode(281, ); // [S_MGSERVER_CONNECT_INFO]
		// addPacketOpcode(282, ); // [S_USER_EXPERIENCE_LV] shows buff icon + symbols next to players name //fsc 282 dd playerObjId iconId (2 = Reward for new user, 3 = reward for returning user, 10 benefits for special user)
		// addPacketOpcode(283, SM_DISPUTE_LAND.class); // [S_SPVP_STATUS]
		addPacketOpcode(284, SM_FIRST_SHOW_DECOMPOSABLE.class); // [S_DISASSEMBLY_ITEMLIST]
		addPacketOpcode(285, SM_MEGAPHONE.class); // [S_MEGAPHONE]
		addPacketOpcode(286, SM_SECONDARY_SHOW_DECOMPOSABLE.class); // [S_SELECT_DISASSEMBLY_ITEM_RESULT]
		// addPacketOpcode(287, ); // [S_RUN_EVENT_MC_MACRO]
		addPacketOpcode(288, SM_TUNE_RESULT.class); // [S_REIDENTIFY_PREVIEW]
		addPacketOpcode(289, SM_UNWRAP_ITEM.class); // [S_UNPACK_ITEM]
		addPacketOpcode(290, SM_QUEST_REPEAT.class); // [S_RESET_REPEAT_QUEST]
		// addPacketOpcode(291, SM_UNK_4_5.class); // [S_PROTOCOL_MAX]
		addPacketOpcode(292, SM_AFTER_TIME_CHECK_4_7_5.class);
		addPacketOpcode(293, SM_AFTER_SIEGE_LOCINFO_475.class);
		// addPacketOpcode(294, );
		// addPacketOpcode(295, );
		addPacketOpcode(296, SM_BIND_POINT_TELEPORT.class);
		// addPacketOpcode(297, );
		addPacketOpcode(298, SM_UPGRADE_ARCADE.class);
		addPacketOpcode(299, SM_ATREIAN_PASSPORT.class);
		// addPacketOpcode(300, );
		// addPacketOpcode(301, );
		addPacketOpcode(302, SM_LEGION_DOMINION_RANK.class);
		addPacketOpcode(303, SM_LEGION_DOMINION_LOC_INFO.class);
	}

	static int getOpcode(Class<? extends AionServerPacket> packetClass) {
		Integer opcode = opcodes.get(packetClass);
		if (opcode == null)
			throw new IllegalArgumentException("There is no opcode for " + packetClass + " defined.");

		return opcode;
	}

	private static void addPacketOpcode(int opcode, Class<? extends AionServerPacket> packetClass) {
		if (opcode < 0)
			return;

		if (opcodes.values().contains(opcode))
			throw new IllegalArgumentException(String.format("There already exists another packet with id 0x%02X", opcode));

		opcodes.put(packetClass, opcode);
	}

}
