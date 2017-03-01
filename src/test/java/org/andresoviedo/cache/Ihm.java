package org.andresoviedo.cache;

import java.util.Map;

public class Ihm {

	private Long id;
	private String domaine;
	private String nom;
	private Map<String, IhmProperty> properties;

	public Long getId() {
		return this.id;
	}

	public String getDomaine() {
		return this.domaine;
	}

	public String getNom() {
		return this.nom;
	}

	public Map<String, IhmProperty> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, IhmProperty> properties) {
		this.properties = properties;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setDomaine(String domaine) {
		this.domaine = domaine;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

}
