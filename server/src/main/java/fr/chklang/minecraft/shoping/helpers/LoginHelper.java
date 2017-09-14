package fr.chklang.minecraft.shoping.helpers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

import fr.chklang.minecraft.shoping.Position;
import fr.chklang.minecraft.shoping.servlets.IConnexion;

public class LoginHelper {

	public static final Map<UUID, PlayerConnected> connectedPlayers = new HashMap<>();

	public static final Map<String, PlayerConnected> connectedPlayersByKeyLogin = new HashMap<>();
	
	public static class PlayerConnected {
		public Set<IConnexion> connexions = new HashSet<>();
		public Player player;
		public Position position;
	}
}
