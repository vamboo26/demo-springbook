package io.zingoworks.demospringbook.user.service;

import lombok.Getter;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class MockMailSender implements MailSender {
	
	private List<String> requests = new ArrayList<>();
	
	@Override
	public void send(SimpleMailMessage simpleMailMessage) throws MailException {
		requests.add(Objects.requireNonNull(simpleMailMessage.getTo())[0]);
	}
	
	@Override
	public void send(SimpleMailMessage... simpleMailMessages) throws MailException {
	
	}
}
