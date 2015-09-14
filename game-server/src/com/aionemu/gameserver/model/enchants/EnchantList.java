package com.aionemu.gameserver.model.enchants;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.item.enums.ItemGroup;

/**
 *
 * @author xTz
 */
@XmlType(name = "enchant_list")
@XmlAccessorType(XmlAccessType.FIELD)
public class EnchantList {

	@XmlElement(name = "enchant_data", required = true)
	protected List<EnchantTemplateData> enchantDatas;

	@XmlAttribute(name = "item_group", required = true)
	private ItemGroup itemGroup = ItemGroup.NONE;

	public List<EnchantTemplateData> getEnchantDatas() {
		return enchantDatas;
	}

	public ItemGroup getItemGroup() {
		return itemGroup;
	}

}
