package fr.chklang.minecraft.shoping.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import fr.chklang.minecraft.shoping.Position;
import fr.chklang.minecraft.shoping.helpers.LoginHelper;
import fr.chklang.minecraft.shoping.json.AbstractMessage;
import fr.chklang.minecraft.shoping.json.AbstractResponse;
import fr.chklang.minecraft.shoping.json.JsonHelper;
import fr.chklang.minecraft.shoping.json.PositionMessage;

public class GeneralWebSocketAdapter extends WebSocketAdapter implements IConnexion {
	
	private final Map<String, Object> tempDatas = new HashMap<>();
	
	private String token;
	private Position lastPosition = new Position();

	@Override
	public void onWebSocketConnect(Session sess) {
		super.onWebSocketConnect(sess);
		System.out.println("Socket Connected: " + sess + ", this = " + this.toString());
	}

	@Override
	public void onWebSocketText(String message) {
		AbstractMessage<?> lMessage = JsonHelper.fromJson(message, AbstractMessage.class);
		lMessage.execute(this);
	}

	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		super.onWebSocketClose(statusCode, reason);
		LoginHelper.connectedPlayers.forEach((pKey, pPlayerConnected) -> {
			pPlayerConnected.connexions.remove(GeneralWebSocketAdapter.this);
		});
	}

	@Override
	public void onWebSocketError(Throwable cause) {
		super.onWebSocketError(cause);
		cause.printStackTrace(System.err);
	}

	@Override
	public Map<String, Object> getTempDatas() {
		return this.tempDatas;
	}

	@Override
	public Player getPlayer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void send(AbstractMessage<?> pMessage) {
		try {
			this.getRemote().sendString(JsonHelper.toJson(pMessage));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void send(AbstractResponse<?> pMessage) {
		try {
			this.getRemote().sendString(JsonHelper.toJson(pMessage));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getToken() {
		return this.token;
	}

	@Override
	public void setToken(String pToken) {
		this.token = pToken;
	}

	@Override
	public Position getPosition() {
		return this.lastPosition;
	}

	@Override
	public void setPosition(Position pPosition) {
		this.lastPosition = pPosition;
		this.send(new PositionMessage(pPosition.x, pPosition.y, pPosition.z));
	}
}
