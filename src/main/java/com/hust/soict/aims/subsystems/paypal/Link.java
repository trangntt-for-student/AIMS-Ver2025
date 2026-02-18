package com.hust.soict.aims.subsystems.paypal;

class Link {
    private String href;
    private String rel;
    private String method;

    String getHref() {
        return href;
    }

    String getRel() {
        return rel;
    }

    String getMethod() {
        return method;
    }

    void setHref(String href) {
        this.href = href;
    }

    void setRel(String rel) {
        this.rel = rel;
    }

    void setMethod(String method) {
        this.method = method;
    }
}
