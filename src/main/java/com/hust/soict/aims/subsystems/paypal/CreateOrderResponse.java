package com.hust.soict.aims.subsystems.paypal;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

public class CreateOrderResponse {
	private String id;
	private List<Link> links;

	public void parseResponseString(String json) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			CreateOrderResponse temp = mapper.readValue(json, CreateOrderResponse.class);

			this.id = temp.id;
			this.links = temp.links;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String getId() {
		return id;
	}

	public String getApproveLink() {
		return links.stream().filter(l -> "approve".equals(l.getRel())).findFirst().map(Link::getHref).orElse(null);
	}
}
