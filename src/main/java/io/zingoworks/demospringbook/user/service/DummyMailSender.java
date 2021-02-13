package io.zingoworks.demospringbook.user.service;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
public class DummyMailSender implements MailSender {
	
	@Override
	public void send(SimpleMailMessage simpleMailMessage) throws MailException {
		System.out.println("dummy mail sender : send");
	}
	
	@Override
	public void send(SimpleMailMessage... simpleMailMessages) throws MailException {
		System.out.println("dummy mail sender : send");
	}
}
