package io.zingoworks.demospringbook.user.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTest {
	
	private User user;
	
	@BeforeEach
	void setUp() {
		user = new User();
	}
	
	@Test
	void upgradeLevel() {
		Level[] levels = Level.values();
		for (Level level : levels) {
			if (level.getNext() == null) {
				continue;
			}
			user.setLevel(level);
			user.upgradeLevel();
			assertThat(user.getLevel()).isEqualTo(level.getNext());
		}
	}
	
	@Test
	void cannotUpgradeLevel() {
		assertThrows(IllegalStateException.class,
				() -> {
					Level[] levels = Level.values();
					for (Level level : levels) {
						if (level.getNext() != null) {
							continue;
						}
						user.setLevel(level);
						user.upgradeLevel();
					}
				});
	}
}
