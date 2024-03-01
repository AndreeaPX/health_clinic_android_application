package com.example.msl.Instance;

import java.util.Date;
import java.util.Objects;

public class Document {
    private String title;
    private String body;
    private Date uploadingDate;
    private int id;
    private int idUser;

    public Document(String title, String body, Date uploadingDate) {
        this.title = title;
        this.body = body;
        this.uploadingDate = uploadingDate;
    }

    public Document(String title, String body, Date uploadingDate, int id, int idUser) {
        this.title = title;
        this.body = body;
        this.uploadingDate = uploadingDate;
        this.id = id;
        this.idUser = idUser;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public Date getUploadingDate() {
        return uploadingDate;
    }

    public int getId() {
        return id;
    }

    public int getIdUser() {
        return idUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document)) return false;
        Document document = (Document) o;
        return getId() == document.getId() && getTitle().equals(document.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getId());
    }
}
