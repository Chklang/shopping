package fr.chklang.minecraft.shoping.json;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import fr.chklang.minecraft.shoping.json.events.MoneyEvent;
import fr.chklang.minecraft.shoping.json.events.PlayerInventoryEvent;
import fr.chklang.minecraft.shoping.json.events.PlayerJoinEventMessage;
import fr.chklang.minecraft.shoping.json.events.ShopItemUpdateEvent;
import fr.chklang.minecraft.shoping.json.events.ShopUpdateEvent;

@JsonTypeInfo(use = Id.NAME, include=As.PROPERTY, property="type")
@JsonSubTypes({
	@JsonSubTypes.Type(name = "PLAYER_EVENT", value = PlayerJoinEventMessage.class),
	@JsonSubTypes.Type(name = "MONEY_EVENT", value = MoneyEvent.class),
	@JsonSubTypes.Type(name = "SHOP_EVENT", value = ShopUpdateEvent.class),
	@JsonSubTypes.Type(name = "SHOP_ITEM_EVENT", value = ShopItemUpdateEvent.class),
	@JsonSubTypes.Type(name = "PLAYER_INVENTORY", value = PlayerInventoryEvent.class)
})
public class AbstractEvent<T> {

	public final T content;

	public AbstractEvent(T pContent) {
		super();
		this.content = pContent;
	}
	
}
