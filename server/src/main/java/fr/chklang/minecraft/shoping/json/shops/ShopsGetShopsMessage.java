package fr.chklang.minecraft.shoping.json.shops;

import java.util.ArrayList;
import java.util.List;

import fr.chklang.minecraft.shoping.json.AbstractMessage;
import fr.chklang.minecraft.shoping.json.AbstractResponse;
import fr.chklang.minecraft.shoping.model.Shop;
import fr.chklang.minecraft.shoping.servlets.IConnexion;

public class ShopsGetShopsMessage extends AbstractMessage<ShopsGetShopsContent> {

	@Override
	public void execute(IConnexion pConnexion) {
		Response lResponse = new Response(this);
		Shop.DAO.getAll().forEach((pShop) -> {
			Long lIdOwner = null;
			if (pShop.getOwner() != null) {
				lIdOwner = pShop.getOwner().getId();
			}
			lResponse.content.shops.add(new ResponseContentElement(pShop.getId(), pShop.getName(), pShop.getX_min(), pShop.getX_max(), pShop.getY_min(), pShop.getY_max(), pShop.getZ_min(),
					pShop.getZ_max(), lIdOwner, pShop.getBaseMargin(), pShop.getSpace()));
		});
		pConnexion.send(lResponse);
		return;
	}

	public static class Response extends AbstractResponse<ResponseContent> {

		public Response(AbstractMessage<?> pOrigin) {
			super(pOrigin);
			this.content = new ResponseContent();
		}
	}

	public static class ResponseContent {
		public final List<ResponseContentElement> shops = new ArrayList<>();
	}

	public static class ResponseContentElement {
		public final long idShop;

		public final String name;

		public final long x_min;
		public final long x_max;
		public final long y_min;
		public final long y_max;
		public final long z_min;
		public final long z_max;

		public final Long idOwner;
		public final double baseMargin;
		public final long space;

		public ResponseContentElement(long pIdShop, String pName, long pX_min, long pX_max, long pY_min, long pY_max, long pZ_min, long pZ_max, Long pIdOwner, double pBaseMargin, long pSpace) {
			super();
			this.idShop = pIdShop;
			this.name = pName;
			this.x_min = pX_min;
			this.x_max = pX_max;
			this.y_min = pY_min;
			this.y_max = pY_max;
			this.z_min = pZ_min;
			this.z_max = pZ_max;
			this.idOwner = pIdOwner;
			this.baseMargin = pBaseMargin;
			this.space = pSpace;
		}
	}

}
