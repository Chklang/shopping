package fr.chklang.minecraft.shoping.model;

public abstract class AbstractModel<T> {
	
	public boolean isExistsIntoDB = false;
	
	public AbstractModel() {
		
	}

	public abstract T save();
	
	public abstract void delete();
}
