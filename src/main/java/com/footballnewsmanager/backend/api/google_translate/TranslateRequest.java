package com.footballnewsmanager.backend.api.google_translate;

public class TranslateRequest {

    private String q;
    private String source;
    private String target;


    public TranslateRequest(String q, String source, String target) {
        this.q = q;
        this.source = source;
        this.target = target;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
