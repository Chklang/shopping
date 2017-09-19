package fr.chklang.minecraft.shoping.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import fr.chklang.minecraft.shoping.json.login.LoginCheckConnexionMessage;
import fr.chklang.minecraft.shoping.json.login.LoginGetTokenMessage;
import fr.chklang.minecraft.shoping.json.login.LoginLogoutMessage;
import fr.chklang.minecraft.shoping.json.login.LoginSendTokenMessage;
import fr.chklang.minecraft.shoping.json.shops.ShopsBuyOrSellMessage;
import fr.chklang.minecraft.shoping.json.shops.ShopsChangeOwnerMessage;
import fr.chklang.minecraft.shoping.json.shops.ShopsGetItemsMessage;
import fr.chklang.minecraft.shoping.json.shops.ShopsGetShopsMessage;
import fr.chklang.minecraft.shoping.json.shops.ShopsSetItemMessage;
import fr.chklang.minecraft.shoping.servlets.IConnexion;

@JsonTypeInfo(use = Id.NAME, include=As.PROPERTY, property="type")
@JsonSubTypes({
	@JsonSubTypes.Type(name = "LOGIN_GET_TOKEN", value = LoginGetTokenMessage.class),
	@JsonSubTypes.Type(name = "LOGIN_SEND_TOKEN", value = LoginSendTokenMessage.class),
	@JsonSubTypes.Type(name = "LOGIN_CHECK", value = LoginCheckConnexionMessage.class),
	@JsonSubTypes.Type(name = "LOGIN_LOGOUT", value = LoginLogoutMessage.class),
	@JsonSubTypes.Type(name = "POSITION_CURRENT", value = PositionMessage.class),
	@JsonSubTypes.Type(name = "SHOPS_GETALL", value = ShopsGetShopsMessage.class),
	@JsonSubTypes.Type(name = "SHOPS_GET_ITEMS", value = ShopsGetItemsMessage.class),
	@JsonSubTypes.Type(name = "SHOPS_SET_ITEM", value = ShopsSetItemMessage.class),
	@JsonSubTypes.Type(name = "SHOPS_BUY_OR_SELL", value = ShopsBuyOrSellMessage.class),
	@JsonSubTypes.Type(name = "SHOPS_CHANGE_OWNER", value = ShopsChangeOwnerMessage.class),
	@JsonSubTypes.Type(name = "PLAYERS_GETALL", value = PlayersGetPlayersMessage.class)
})
public abstract class AbstractMessage<T extends AbstractContent> {

	@JsonProperty(required = false, value = "answerId")
	public String answerId;

	@JsonProperty(required = false, value = "content")
	public T content;

	@Override
	public String toString() {
		return "AbstractMessage [answerId=" + this.answerId + ", content=" + this.content + "]";
	}
	
	public abstract void execute(IConnexion pConnexion);

}