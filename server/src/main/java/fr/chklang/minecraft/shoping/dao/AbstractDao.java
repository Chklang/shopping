package fr.chklang.minecraft.shoping.dao;

import java.util.List;

import fr.chklang.minecraft.shoping.model.AbstractModel;

public abstract class AbstractDao<T extends AbstractModel, Pk> {

	private final Class<? extends T> clazz;

	public AbstractDao(Class<? extends T> pClass) {
		this.clazz = pClass;
	}

	public abstract T get(Pk pKey);

	public abstract List<T> getAll();
}
