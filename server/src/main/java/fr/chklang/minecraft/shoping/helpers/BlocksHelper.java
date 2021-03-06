package fr.chklang.minecraft.shoping.helpers;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class BlocksHelper {
	private static BlocksHelper INSTANCE;
	private Map<Integer, Element> mapElements = new HashMap<>();
	private Set<Element> elements = new TreeSet<>(new ComparatorElement());
	
	private static class ComparatorElement implements Comparator<Element> {
		public int compare(Element o1, Element o2) {
			return o1.id - o2.id;
		}
	}
	
	public static class Element {
		public int id;
		public String name;
		public String nameDetails;
		public double price;
		private Map<Short, Element> mapSubElements = new HashMap<>();
		public Set<Element> subElements = new TreeSet<Element>(new ComparatorElement());
		@Override
		public String toString() {
			return "Element [id=" + this.id + ", name=" + this.name + ", price=" + this.price + ", mapSubElements="
					+ this.mapSubElements + ", subElements=" + this.subElements + "], " + super.toString();
		}
	}

	private static BlocksHelper getInstance() {
		if (INSTANCE == null) {
			synchronized (BlocksHelper.class) {
				if (INSTANCE == null) {
					INSTANCE = new BlocksHelper();
				}
			}
		}
		return INSTANCE;
	}
	
	private BlocksHelper() {
		String lBasePricesStream = null;
		try {
			lBasePricesStream = IOUtils.toString(this.getClass().getResourceAsStream("/baseprices.json"), "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		JSONArray lTab = new JSONArray(lBasePricesStream);
		lTab.forEach((pElement) -> {
			JSONObject lObject = (JSONObject) pElement;

			if (lObject.isNull("price") || lObject.isNull("id")) {
				//Ignore all elements without default price
				return;
			}

			int lIdElement = lObject.getInt("id");
			if (!lObject.isNull("subid")) {
				Element lParent = this.mapElements.get(lIdElement);
				if (lParent == null) {
					//Not created yet
					lParent = new Element();
					this.mapElements.put(lIdElement, lParent);
				}
				Element lElement = new Element();
				lElement.id = lObject.getInt("subid");
				lElement.name = lObject.getString("tr");
				lElement.nameDetails = lObject.getString("detailstr");
				lElement.price = lObject.getDouble("price");
				lParent.mapSubElements.put(Integer.valueOf(lElement.id).shortValue(), lElement);
				lParent.subElements.add(lElement);
			} else {
				Element lElement = this.mapElements.get(lIdElement);
				if (lElement == null) {
					lElement = new Element();
					this.mapElements.put(lIdElement, lElement);
					this.elements.add(lElement);
				}
				lElement.id = lIdElement;
				lElement.name = lObject.getString("tr");
				lElement.nameDetails = lObject.getString("detailstr");
				lElement.price = lObject.getDouble("price");
			}
		});
	}
	
	public static Element getElement(int pId) {
		return getElement(pId, null);
	}
	public static Element getElement(int pId, int pSubIdItem) {
		return getElement(pId, Short.valueOf((short) pSubIdItem));
	}
	public static Element getElement(int pId, Short pSubIdItem) {
		Element lElement = getInstance().mapElements.get(pId);
		if (pSubIdItem != null && pSubIdItem != 0) {
			lElement = lElement.mapSubElements.get(pSubIdItem.shortValue());
		}
		return lElement;
	}
	public static Set<Element> getElements() {
		return getInstance().elements;
	}
}
