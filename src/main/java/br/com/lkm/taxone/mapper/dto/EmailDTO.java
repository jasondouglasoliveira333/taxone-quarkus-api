package br.com.lkm.taxone.mapper.dto;

import br.com.lkm.taxone.mapper.enums.EmailType;

public class EmailDTO {
	private Integer id;
	private String email;
	private EmailType type;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public EmailType getType() {
		return type;
	}
	public void setType(EmailType type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "EmailDTO [id=" + id + ", email=" + email + ", type=" + type + "]";
	}

	
}
