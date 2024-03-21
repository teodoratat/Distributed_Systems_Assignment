package com.bezkoder.spring.login.payload.response;

import java.util.List;
import java.util.UUID;

public class UserInfoResponse {
	private UUID id;
	private String username;
	private List<String> roles;

	private String jwt;

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}

	public UserInfoResponse(UUID id, String username, List<String> roles, String jwt) {
		this.id = id;
		this.username = username;
		this.roles = roles;
		this.jwt=jwt;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getRoles() {
		return roles;
	}
}
