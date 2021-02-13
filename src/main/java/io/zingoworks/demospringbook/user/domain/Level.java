package io.zingoworks.demospringbook.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Level {
	GOLD(3, null),
	SILVER(2, GOLD),
	BASIC(1, SILVER),
	;
	
	private final int value;
	private final Level next;
	
	public static Level findByValue(int value) {
		return Arrays.stream(values())
				.filter(l -> l.getValue() == value)
				.findFirst()
				.orElseThrow(() -> new AssertionError("Unknown value: " + value));
	}
}
