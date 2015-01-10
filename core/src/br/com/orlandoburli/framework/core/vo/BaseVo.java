package br.com.orlandoburli.framework.core.vo;

import java.io.Serializable;

public abstract class BaseVo implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean isNew;

	public BaseVo() {
		this.setNew(true);
	}

	public boolean isNew() {
		return this.isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public String getVoName() {
		return this.getClass().getSimpleName();
	}
}