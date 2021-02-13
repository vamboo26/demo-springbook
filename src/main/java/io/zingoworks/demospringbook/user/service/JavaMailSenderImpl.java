package io.zingoworks.demospringbook.user.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
@Getter
public class JavaMailSenderImpl implements MailSender {
	
	@Value("host:mail.server.com")
	private String host;
	
	@Override
	public void send(SimpleMailMessage simpleMailMessage) throws MailException {
		System.out.println("java mail sender : send");
	}
	
	@Override
	public void send(SimpleMailMessage... simpleMailMessages) throws MailException {
		System.out.println("java mail sender : send");
	}
}
