package fr.chklang.minecraft.shoping.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import fr.chklang.minecraft.shoping.json.login.LoginCheckConnexionMessage;
import fr.chklang.minecraft.shoping.json.login.LoginGetTokenMessage;
import fr.chklang.minecraft.shoping.json.login.LoginSendTokenMessage;
import fr.chklang.minecraft.shoping.servlets.IConnexion;

@JsonTypeInfo(use = Id.NAME, include=As.PROPERTY, property="type")
@JsonSubTypes({
	@JsonSubTypes.Type(name = "LOGIN_GET_TOKEN", value = LoginGetTokenMessage.class),
	@JsonSubTypes.Type(name = "LOGIN_SEND_TOKEN", value = LoginSendTokenMessage.class),
	@JsonSubTypes.Type(name = "LOGIN_CHECK", value = LoginCheckConnexionMessage.class)
})
public abstract class AbstractMessage<T> {

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