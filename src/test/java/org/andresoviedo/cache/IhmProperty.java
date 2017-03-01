package org.andresoviedo.cache;

public class IhmProperty {

	private Long id;
	private String nom;
	private String valeur;


	public IhmProperty(Long id, String nom, String valeur) {
		this.id = id;
		this.nom = nom;
		this.valeur = valeur;
	}

	public Long getId() {
		return this.id;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getValeur() {
		return valeur;
	}

	public void setValeur(String valeur) {
		this.valeur = valeur;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
