package fr.chklang.minecraft.shoping.servlets;

import java.util.Map;

import fr.chklang.minecraft.shoping.helpers.LoginHelper.PlayerConnected;
import fr.chklang.minecraft.shoping.json.AbstractEvent;
import fr.chklang.minecraft.shoping.json.AbstractMessage;
import fr.chklang.minecraft.shoping.json.AbstractResponse;

public interface IConnexion {

	Map<String, Object> getTempDatas();
	String getToken();
	void setToken(String pToken);
	PlayerConnected getPlayer();
	void setPlayer(PlayerConnected pPlayer);
	void send(AbstractMessage<?> pMessage);
	void send(AbstractResponse<?> pMessage);
	void send(AbstractEvent<?> pMessage);
	PlayerConnected getPlayerConnected();
	void setPlayerConnected(PlayerConnected pPlayerConnected);
}
