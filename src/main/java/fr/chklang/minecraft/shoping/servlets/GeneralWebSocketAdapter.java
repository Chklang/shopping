package fr.chklang.minecraft.shoping.servlets;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

public class GeneralWebSocketAdapter extends WebSocketAdapter {

	private RemoteEndpoint remote;

	@Override
	public void onWebSocketConnect(Session sess) {
		super.onWebSocketConnect(sess);
		System.out.println("Socket Connected: " + sess + ", this = " + this.toString());
		this.remote = sess.getRemote();
	}

	@Override
	public void onWebSocketText(String message) {
		super.onWebSocketText(message);
		System.out.println("Received TEXT message: " + message);
		try {
			this.remote.sendString("Bien reçu chef! Je transmets le message : " + message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		super.onWebSocketClose(statusCode, reason);
		System.out.println("Socket Closed: [" + statusCode + "] " + reason);
	}

	@Override
	public void onWebSocketError(Throwable cause) {
		super.onWebSocketError(cause);
		cause.printStackTrace(System.err);
	}
}
