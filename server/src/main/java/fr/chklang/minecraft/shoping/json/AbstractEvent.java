package fr.chklang.minecraft.shoping.json;

public class AbstractEvent<T> {

	public final T content;

	public AbstractEvent(T pContent) {
		super();
		this.content = pContent;
	}
	
}
