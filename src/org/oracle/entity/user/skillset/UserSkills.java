package org.oracle.entity.user.skillset;

import org.oracle.entity.user.User;

public class UserSkills {

	private Skill[] skills = {};
	
	private User user;
	
	public UserSkills(User user) {
		this.user = user;
		init();
	}
	
	public void init() {
		for (int i = 0; i < 25; i++) {
			skills[i] = new Skill(i, 1, 0);
		}
	}
	
	public Skill[] getSkills() {
		return skills;
	}
	
	public User getUser() {
		return user;
	}
	
}
