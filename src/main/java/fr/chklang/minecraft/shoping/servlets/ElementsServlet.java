package fr.chklang.minecraft.shoping.servlets;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import fr.chklang.minecraft.shoping.db.DBManager;
import fr.chklang.minecraft.shoping.helpers.BlocksHelper;

public class ElementsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Pattern patternShops = Pattern.compile("\\/rest\\/elements\\/shops\\/([0-9]+)");
	private Pattern patternElements = Pattern.compile("\\/rest\\/elements");
	private Pattern patternGetcode = Pattern.compile("\\/rest\\/login\\/getcode");
	private Pattern patternSendcode = Pattern.compile("\\/rest\\/login\\/sendcode");
	private Pattern patternTestLogin = Pattern.compile("\\/rest\\/login");

	/* ------------------------------------------------------------ */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String lPath = request.getRequestURI();
		Matcher lMatcher = patternShops.matcher(lPath);
		if (lMatcher.find()) {
			int lShopId = Integer.parseInt(lMatcher.group(1));
			this.getShop(request, response, lShopId);
			return;
		}
		lMatcher = patternElements.matcher(lPath);
		if (lMatcher.find()) {
			this.getElements(request, response);
			return;
		}
		lMatcher = patternGetcode.matcher(lPath);
		if (lMatcher.find()) {
			this.loginGetCode(request, response);
			return;
		}
		lMatcher = patternSendcode.matcher(lPath);
		if (lMatcher.find()) {
			this.loginSendCode(request, response);
			return;
		}
		lMatcher = patternTestLogin.matcher(lPath);
		if (lMatcher.find()) {
			this.loginTest(request, response);
			return;
		}
		response.setStatus(404);
		return;
	}
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}
	
	private void getShop(HttpServletRequest request, HttpServletResponse response, int pIdShop) throws IOException {
		PrintStream lPrintStream = new PrintStream(response.getOutputStream());
		if (DBManager.getInstance().isShopExists(pIdShop)) {
			lPrintStream.print("Shop exists");
		} else {
			lPrintStream.print("Shop not exists");
		}
		lPrintStream.close();
	}
	
	private void getElements(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintStream lPrintStream = new PrintStream(response.getOutputStream());
		try {
			final JSONArray lResult = new JSONArray();
			BlocksHelper.getElements().forEach((BlocksHelper.Element pElement) -> {
				JSONObject lJsonObject = new JSONObject();
				lJsonObject.put("name", pElement.name);
				lJsonObject.put("id", pElement.id);
				lJsonObject.put("price", pElement.price);
				JSONArray lSubElements = new JSONArray();
				lJsonObject.put("subElements", lSubElements);
				pElement.subElements.forEach((BlocksHelper.Element pSubElement) -> {
					JSONObject lSubJsonObject = new JSONObject();
					lSubJsonObject.put("name", pElement.name);
					lSubJsonObject.put("id", pElement.id);
					lSubJsonObject.put("price", pElement.price);
					lSubElements.put(lSubJsonObject);
				});
				lResult.put(lJsonObject);
			});
			lPrintStream.print(lResult);
		} finally {
			lPrintStream.close();
		}
	}
	
	private static class LoginUUID {
		public String pseudo;
		public String uuid;
		public String key;
		public LoginUUID(String pseudo, String uuid, String key) {
			super();
			this.pseudo = pseudo;
			this.uuid = uuid;
			this.key = key;
		}
	}
	
	private Map<String, LoginUUID> uuidsWaiting = new HashMap<>();
	private Map<String, String> connexions = new HashMap<>();
	
	private void loginGetCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String lContent = IOUtils.toString(request.getInputStream(), "UTF-8");
		JSONObject lContentJson = new JSONObject(lContent);
		if (lContentJson.isNull("pseudo")) {
			response.setStatus(500);
			PrintStream lPrintStream = new PrintStream(response.getOutputStream());
			lPrintStream.print("Pseudo value missing!");
			lPrintStream.close();
			return;
		}
		String lPseudo = lContentJson.getString("pseudo");
		String lUuid = null;
		do {
			lUuid = UUID.randomUUID().toString().replace("-", "");
		} while (uuidsWaiting.containsKey(lUuid));
		String lKey = UUID.randomUUID().toString().replace("-", "").substring(0, 5).toUpperCase();
		//TODO Transform for tests
		lKey = "AZERTY";
		LoginUUID lLoginUUID = new LoginUUID(lPseudo, lUuid, lKey);
		uuidsWaiting.put(lUuid, lLoginUUID);
		response.setStatus(200);
		PrintStream lPrintStream = new PrintStream(response.getOutputStream());
		JSONObject lResponse = new JSONObject();
		lResponse.put("playerFound", true);
		lResponse.put("codeRequest", lUuid);
		lPrintStream.print(lResponse.toString());
		lPrintStream.close();
	}
	
	private void loginSendCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String lContent = IOUtils.toString(request.getInputStream(), "UTF-8");
		JSONObject lContentJson = new JSONObject(lContent);
		if (lContentJson.isNull("token")) {
			response.setStatus(500);
			PrintStream lPrintStream = new PrintStream(response.getOutputStream());
			lPrintStream.print("Token value missing!");
			lPrintStream.close();
			return;
		}
		if (lContentJson.isNull("key")) {
			response.setStatus(500);
			PrintStream lPrintStream = new PrintStream(response.getOutputStream());
			lPrintStream.print("Key value missing!");
			lPrintStream.close();
			return;
		}
		String lToken = lContentJson.getString("token");
		String lKey = lContentJson.getString("key");
		if (!uuidsWaiting.containsKey(lToken)) {
			response.setStatus(200);
			PrintStream lPrintStream = new PrintStream(response.getOutputStream());
			JSONObject lResponse = new JSONObject();
			lResponse.put("tokenFound", false);
			lPrintStream.print(lResponse.toString());
			lPrintStream.close();
			return;
		}
		LoginUUID lLoginUUID = uuidsWaiting.get(lToken);
		if (!lLoginUUID.key.equalsIgnoreCase(lKey)) {
			response.setStatus(200);
			PrintStream lPrintStream = new PrintStream(response.getOutputStream());
			JSONObject lResponse = new JSONObject();
			lResponse.put("tokenFound", false);
			lPrintStream.print(lResponse.toString());
			lPrintStream.close();
			return;
		}
		//Connexion OK
		connexions.put(lToken, lLoginUUID.pseudo);
		response.setStatus(200);
		PrintStream lPrintStream = new PrintStream(response.getOutputStream());
		JSONObject lResponse = new JSONObject();
		lResponse.put("tokenFound", true);
		lResponse.put("token", lToken);
		lPrintStream.print(lResponse.toString());
	}
	
	private void loginTest(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String lContent = IOUtils.toString(request.getInputStream(), "UTF-8");
		JSONObject lContentJson = new JSONObject(lContent);
		if (lContentJson.isNull("token")) {
			response.setStatus(500);
			PrintStream lPrintStream = new PrintStream(response.getOutputStream());
			lPrintStream.print("Token value missing!");
			lPrintStream.close();
			return;
		}
		String lToken = lContentJson.getString("token");
		if (!connexions.containsKey(lToken)) {
			response.setStatus(500);
			PrintStream lPrintStream = new PrintStream(response.getOutputStream());
			lPrintStream.print("Token not found!");
			lPrintStream.close();
			return;
		}
		String lPseudo = connexions.get(lToken);
		response.setStatus(200);
		PrintStream lPrintStream = new PrintStream(response.getOutputStream());
		lPrintStream.print(lPseudo);
		lPrintStream.close();
	}

}
