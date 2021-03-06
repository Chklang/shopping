package fr.chklang.minecraft.shoping.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import fr.chklang.minecraft.shoping.helpers.LoginHelper;
import fr.chklang.minecraft.shoping.helpers.LoginHelper.PlayerConnected;
import fr.chklang.minecraft.shoping.json.AbstractEvent;
import fr.chklang.minecraft.shoping.json.AbstractMessage;
import fr.chklang.minecraft.shoping.json.AbstractResponse;
import fr.chklang.minecraft.shoping.json.JsonHelper;

public class GeneralWebSocketAdapter extends WebSocketAdapter implements IConnexion {
	
	private final Map<String, Object> tempDatas = new HashMap<>();
	
	private String token;

	private LoginHelper.PlayerConnected playerConnected;
	
	private Long idShopSubscripted;

	@Override
	public void onWebSocketConnect(Session sess) {
		super.onWebSocketConnect(sess);
		LoginHelper.notConnected.add(this);
		System.out.println("Socket Connected: " + sess + ", this = " + this.toString());
	}

	@Override
	public void onWebSocketText(String message) {
		AbstractMessage<?> lMessage = JsonHelper.fromJson(message, AbstractMessage.class);
		try {
			lMessage.execute(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		super.onWebSocketClose(statusCode, reason);
		this.disconnect();
	}

	@Override
	public void onWebSocketError(Throwable cause) {
		super.onWebSocketError(cause);
		this.disconnect();
	}
	
	private void disconnect() {
		LoginHelper.connectedPlayers.forEach((pKey, pPlayerConnected) -> {
			pPlayerConnected.connexions.remove(GeneralWebSocketAdapter.this);
		});
		LoginHelper.connectedPlayersByKeyLogin.forEach((pKey, pPlayerConnected) -> {
			pPlayerConnected.connexions.remove(GeneralWebSocketAdapter.this);
		});
	}

	@Override
	public Map<String, Object> getTempDatas() {
		return this.tempDatas;
	}

	@Override
	public void setPlayer(PlayerConnected pPlayer) {
		this.playerConnected = pPlayer;
	}
	
	@Override
	public PlayerConnected getPlayer() {
		return this.playerConnected;
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
	public void send(AbstractEvent<?> pMessage) {
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
	public PlayerConnected getPlayerConnected() {
		return this.playerConnected;
	}

	@Override
	public void setPlayerConnected(PlayerConnected pPlayerConnected) {
		this.playerConnected = pPlayerConnected;
	}

	@Override
	public void subscribeEventsShop(long pShopId) {
		this.idShopSubscripted = pShopId;
	}

	@Override
	public void unsubscribeEventsShop() {
		this.idShopSubscripted = null;
	}

	public Long getIdShopSubscripted() {
		return this.idShopSubscripted;
	}

}
