package fr.chklang.minecraft.shoping.servlets;

import java.io.IOException;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import fr.chklang.minecraft.shoping.db.DBManager;
import fr.chklang.minecraft.shoping.helpers.BlocksHelper;

public class ElementsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/* ------------------------------------------------------------ */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String lPath = request.getRequestURI();
		Pattern lPattern = Pattern.compile("\\/elements\\/shops\\/([0-9]+)");
		Matcher lMatcher = lPattern.matcher(lPath);
		if (lMatcher.find()) {
			int lShopId = Integer.parseInt(lMatcher.group(1));
			this.getShop(request, response, lShopId);
		} else {
			this.getElements(request, response);
		}
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

}
