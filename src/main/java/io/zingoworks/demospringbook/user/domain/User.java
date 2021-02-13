package io.zingoworks.demospringbook.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
	
	private String id;
	private String name;
	private String password;
	private Level level;
	private int loginSequence;
	private int recommendationCount;
	
	public void upgradeLevel() {
		Level next = this.level.getNext();
		
		if (next == null) {
			throw new IllegalStateException(this.level + "은 업그레이드가 불가능합니다");
		} else {
			this.level = next;
		}
	}
}
