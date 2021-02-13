package io.zingoworks.demospringbook.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Level {
	BASIC(1),
	SILVER(2),
	GOLD(3),
	;
	
	private final int value;
	
	public static Level findByValue(int value) {
		return Arrays.stream(values())
				.filter(l -> l.getValue() == value)
				.findFirst()
				.orElseThrow(() -> new AssertionError("Unknown value: " + value));
	}
}
