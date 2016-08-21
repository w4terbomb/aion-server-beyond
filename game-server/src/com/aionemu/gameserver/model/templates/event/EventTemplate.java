package com.aionemu.gameserver.model.templates.event;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.SpawnsData2;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.Guides.GuideTemplate;
import com.aionemu.gameserver.model.templates.spawns.Spawn;
import com.aionemu.gameserver.model.templates.spawns.SpawnMap;
import com.aionemu.gameserver.model.templates.spawns.SpawnSpotTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemAddType;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.item.ItemService.ItemUpdatePredicate;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

import javolution.util.FastTable;

/**
 * @author Rolandas
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EventTemplate")
public class EventTemplate {

	private static Logger log = LoggerFactory.getLogger(EventTemplate.class);

	@XmlElement(name = "event_drops", required = false)
	protected EventDrops eventDrops;

	@XmlElement(name = "quests", required = false)
	protected EventQuestList quests;

	@XmlElement(name = "spawns", required = false)
	protected SpawnsData2 spawns;

	@XmlElement(name = "inventory_drop", required = false)
	protected InventoryDrop inventoryDrop;

	@XmlList
	@XmlElement(name = "surveys", required = false)
	protected List<String> surveys;

	@XmlAttribute(name = "name", required = true)
	protected String name;

	@XmlAttribute(name = "start", required = true)
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar startDate;

	@XmlAttribute(name = "end", required = true)
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar endDate;

	@XmlAttribute(name = "theme", required = false)
	private String theme;

	@XmlTransient
	protected List<VisibleObject> spawnedObjects;

	@XmlTransient
	private Future<?> invDropTask = null;

	public String getName() {
		return name;
	}

	public EventDrops getEventDrops() {
		return eventDrops;
	}

	public ZonedDateTime getStartDate() {
		return startDate.toGregorianCalendar().toZonedDateTime();
	}

	public ZonedDateTime getEndDate() {
		return endDate.toGregorianCalendar().toZonedDateTime();
	}

	public List<Integer> getStartableQuests() {
		if (quests == null)
			return new FastTable<>();
		return quests.getStartableQuests();
	}

	public List<Integer> getMaintainableQuests() {
		if (quests == null)
			return new FastTable<>();
		return quests.getMaintainQuests();
	}

	public boolean isActive() {
		ZonedDateTime now = ZonedDateTime.now(GSConfig.TIME_ZONE.toZoneId());
		return getStartDate().isBefore(now) && getEndDate().isAfter(now);
	}

	public boolean isExpired() {
		return !isActive();
	}

	@XmlTransient
	volatile boolean isStarted = false;

	public void setStarted() {
		isStarted = true;
	}

	public boolean isStarted() {
		return isStarted;
	}

	public void start() {
		if (isStarted)
			return;

		if (spawns != null && spawns.size() > 0) {
			if (spawnedObjects == null)
				spawnedObjects = new FastTable<>();
			for (SpawnMap map : spawns.getTemplates()) {
				DataManager.SPAWNS_DATA2.addNewSpawnMap(map);
				Collection<Integer> instanceIds = World.getInstance().getWorldMap(map.getMapId()).getAvailableInstanceIds();
				for (Integer instanceId : instanceIds) {
					int spawnCount = 0;
					for (Spawn spawn : map.getSpawns()) {
						spawn.setEventTemplate(this);
						for (SpawnSpotTemplate spot : spawn.getSpawnSpotTemplates()) {
							SpawnTemplate t = SpawnEngine.addNewSpawn(map.getMapId(), spawn.getNpcId(), spot.getX(), spot.getY(), spot.getZ(), spot.getHeading(),
								spawn.getRespawnTime());
							t.setEventTemplate(this);
							SpawnEngine.spawnObject(t, instanceId);
							spawnCount++;
						}
					}
					log.info("Spawned event objects in " + map.getMapId() + " [" + instanceId + "] : " + spawnCount + " (" + this.getName() + ")");
				}
			}
			DataManager.SPAWNS_DATA2.afterUnmarshal(null, null);
			DataManager.SPAWNS_DATA2.clearTemplates();
		}

		if (inventoryDrop != null) {
			invDropTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

				@Override
				public void run() {
					World.getInstance().forEachPlayer(new Visitor<Player>() {

						@Override
						public void visit(Player player) {
							if (player.isOnline() && player.getCommonData().getLevel() >= inventoryDrop.getStartLevel())
								// TODO: check the exact type in retail
								ItemService.addItem(player, inventoryDrop.getDropItem(), 1, true, new ItemUpdatePredicate(ItemAddType.ITEM_COLLECT, ItemUpdateType.INC_CASH_ITEM));
						}
					});
				}
			}, 0, inventoryDrop.getInterval() * 60000);
		}

		if (surveys != null) {
			for (String survey : surveys) {
				GuideTemplate template = DataManager.GUIDE_HTML_DATA.getTemplateByTitle(survey);
				if (template != null)
					template.setActivated(true);
			}
		}

		isStarted = true;
	}

	public void stop() {
		if (!isStarted)
			return;

		if (spawnedObjects != null) {
			for (VisibleObject o : spawnedObjects) {
				if (o.isSpawned())
					o.getController().onDelete();
			}
			DataManager.SPAWNS_DATA2.removeEventSpawnObjects(spawnedObjects);
			log.info("Despawned " + spawnedObjects.size() + " event objects (" + this.getName() + ")");
			spawnedObjects.clear();
			spawnedObjects = null;
		}

		if (invDropTask != null) {
			invDropTask.cancel(false);
			invDropTask = null;
		}

		if (surveys != null) {
			for (String survey : surveys) {
				GuideTemplate template = DataManager.GUIDE_HTML_DATA.getTemplateByTitle(survey);
				if (template != null)
					template.setActivated(false);
			}
		}

		isStarted = false;
	}

	public void addSpawnedObject(VisibleObject object) {
		if (spawnedObjects == null)
			spawnedObjects = new FastTable<>();
		spawnedObjects.add(object);
	}

	/**
	 * @return the theme name
	 */
	public String getTheme() {
		if (theme != null)
			return theme.toLowerCase();
		return theme;
	}

}
