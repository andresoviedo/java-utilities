package org.andresoviedo.cache;

import java.util.Map;

import org.springframework.cache.annotation.Cacheable;

public class IhmDao {

	private Map<String, Ihm> store;

	private long queryCount;

	public void setStore(Map<String, Ihm> store) {
		this.store = store;
	}

	@Cacheable("junit")
	public Ihm getIhm(String domaine, String nom) {
		try {
			return store.get(domaine + "." + nom);
		} finally {
			queryCount++;
		}
	}

	public long getQueryExecutionCount() {
		return this.queryCount;
	}

	public void update(Ihm ihm) {
		store.put(ihm.getDomaine() + "." + ihm.getNom(), ihm);
	}

}
