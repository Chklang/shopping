package fr.chklang.minecraft.shoping.helpers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.chklang.minecraft.shoping.servlets.IConnexion;

public class LoginHelper {

	public static final Map<String, PlayerConnected> connectedPlayers = new HashMap<>();
	
	public static class PlayerConnected {
		public Set<IConnexion> connexions = new HashSet<>();
		public String player;
	}
}
