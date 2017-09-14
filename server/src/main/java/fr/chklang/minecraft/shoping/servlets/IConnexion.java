package fr.chklang.minecraft.shoping.servlets;

import java.util.Map;

import org.bukkit.entity.Player;

import fr.chklang.minecraft.shoping.Position;
import fr.chklang.minecraft.shoping.json.AbstractMessage;
import fr.chklang.minecraft.shoping.json.AbstractResponse;

public interface IConnexion {

	Map<String, Object> getTempDatas();
	String getToken();
	void setToken(String pToken);
	Player getPlayer();
	void send(AbstractMessage<?> pMessage);
	void send(AbstractResponse<?> pMessage);
	Position getPosition();
	void setPosition(Position pPosition);
}
