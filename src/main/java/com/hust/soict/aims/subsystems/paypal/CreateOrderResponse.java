package com.hust.soict.aims.subsystems.paypal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.ArrayList;

class CreateOrderResponse {
	// SUCCESS
	private String id;
	private String status;
	private List<Link> links;

	// FAIL
	private String name;
	private String message;
	private String debugId;

	void parseResponseString(String json) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(json);

			// Error handling
			if (root.has("name")) {
				this.name = root.path("name").asText(null);
				this.message = root.path("message").asText(null);
				this.debugId = root.path("debug_id").asText(null);
				return;
			}

			// Success fields
			this.id = root.path("id").asText(null);
			this.status = root.path("status").asText(null);

			JsonNode linksNode = root.path("links");
			if (linksNode.isArray()) {
				this.links = new ArrayList<>();
				for (JsonNode linkNode : linksNode) {
					Link link = new Link(linkNode.path("href").asText(null), linkNode.path("rel").asText(null),
							linkNode.path("method").asText(null));
					this.links.add(link);
				}
			}

			if (this.id == null || this.id.isBlank()) {
				throw new IllegalStateException("Invalid PayPal success response: missing id");
			}

		} catch (Exception e) {
			throw new RuntimeException("Cannot parse PayPal response", e);
		}
	}

	String getId() {
		return id;
	}

	String getStatus() {
		return status;
	}

	String getApproveLink() {
		if (links == null)
			return null;

		return links.stream().filter(l -> "approve".equals(l.getRel())).map(Link::getHref).findFirst().orElse(null);
	}

	boolean isCreated() {
		return "CREATED".equals(status);
	}

	boolean isSuccess() {
		return id != null && name == null;
	}

	boolean isError() {
		return name != null;
	}

	String getErrorMessage() {
		if (!isError())
			return null;
		return name + " - " + message + " (debug_id=" + debugId + ")";
	}
}
