package io.zingoworks.demospringbook.user.domain;

import lombok.Data;

@Data
public class User {
	String id;
	String name;
	String password;
}
