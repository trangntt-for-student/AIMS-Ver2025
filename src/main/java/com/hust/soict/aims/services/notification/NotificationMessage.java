package com.hust.soict.aims.services.notification;

public class NotificationMessage {
	private final String recipient;
	private final String subject;
	private final String content;

	public NotificationMessage(String recipient, String subject, String content) {
		this.recipient = recipient;
		this.subject = subject;
		this.content = content;
	}

	public String getRecipient() {
		return recipient;
	}

	public String getSubject() {
		return subject;
	}

	public String getContent() {
		return content;
	}
}
