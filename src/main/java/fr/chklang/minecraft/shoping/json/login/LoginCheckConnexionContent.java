package fr.chklang.minecraft.shoping.json.login;

import com.fasterxml.jackson.annotation.JsonProperty;

import fr.chklang.minecraft.shoping.json.AbstractContent;

public class LoginCheckConnexionContent extends AbstractContent {

	@JsonProperty("token")
	public String token;
}
