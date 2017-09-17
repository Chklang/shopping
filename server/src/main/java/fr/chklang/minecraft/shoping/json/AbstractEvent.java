package fr.chklang.minecraft.shoping.json;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import fr.chklang.minecraft.shoping.json.events.PlayerJoinEventMessage;
import fr.chklang.minecraft.shoping.json.events.ShopUpdateEvent;

@JsonTypeInfo(use = Id.NAME, include=As.PROPERTY, property="type")
@JsonSubTypes({
	@JsonSubTypes.Type(name = "PLAYER_EVENT", value = PlayerJoinEventMessage.class),
	@JsonSubTypes.Type(name = "SHOP_EVENT", value = ShopUpdateEvent.class)
})
public class AbstractEvent<T> {

	public final T content;

	public AbstractEvent(T pContent) {
		super();
		this.content = pContent;
	}
	
}
