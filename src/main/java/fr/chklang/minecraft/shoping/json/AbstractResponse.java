package fr.chklang.minecraft.shoping.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class AbstractResponse<T> {

	@JsonProperty(value = "isReply")
	public Boolean isReply = true;

	@JsonProperty(required = true, value = "answerId")
	public final String answerId;

	@JsonProperty(required = false, value = "content")
	public T content;

	public AbstractResponse(AbstractMessage<?> pOrigin) {
		this.answerId = pOrigin.answerId;
	}

}
