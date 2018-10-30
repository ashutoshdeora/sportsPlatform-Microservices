package com.sports.platform.microservices.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A Article.
 */
@Document(collection = "article")
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("article_name")
    private String articleName;

    @Field("article_subject")
    private String articleSubject;

    @Field("catagoery_ref_id")
    private String catagoeryRefId;

    @Field("article_text")
    private String articleText;

    @Field("article_author_full_name")
    private String articleAuthorFullName;

    @Field("article_date")
    private Instant articleDate;

    @Field("user_name")
    private String userName;

    @Field("user_ref_id")
    private String userRefId;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArticleName() {
        return articleName;
    }

    public Article articleName(String articleName) {
        this.articleName = articleName;
        return this;
    }

    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    public String getArticleSubject() {
        return articleSubject;
    }

    public Article articleSubject(String articleSubject) {
        this.articleSubject = articleSubject;
        return this;
    }

    public void setArticleSubject(String articleSubject) {
        this.articleSubject = articleSubject;
    }

    public String getCatagoeryRefId() {
        return catagoeryRefId;
    }

    public Article catagoeryRefId(String catagoeryRefId) {
        this.catagoeryRefId = catagoeryRefId;
        return this;
    }

    public void setCatagoeryRefId(String catagoeryRefId) {
        this.catagoeryRefId = catagoeryRefId;
    }

    public String getArticleText() {
        return articleText;
    }

    public Article articleText(String articleText) {
        this.articleText = articleText;
        return this;
    }

    public void setArticleText(String articleText) {
        this.articleText = articleText;
    }

    public String getArticleAuthorFullName() {
        return articleAuthorFullName;
    }

    public Article articleAuthorFullName(String articleAuthorFullName) {
        this.articleAuthorFullName = articleAuthorFullName;
        return this;
    }

    public void setArticleAuthorFullName(String articleAuthorFullName) {
        this.articleAuthorFullName = articleAuthorFullName;
    }

    public Instant getArticleDate() {
        return articleDate;
    }

    public Article articleDate(Instant articleDate) {
        this.articleDate = articleDate;
        return this;
    }

    public void setArticleDate(Instant articleDate) {
        this.articleDate = articleDate;
    }

    public String getUserName() {
        return userName;
    }

    public Article userName(String userName) {
        this.userName = userName;
        return this;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRefId() {
        return userRefId;
    }

    public Article userRefId(String userRefId) {
        this.userRefId = userRefId;
        return this;
    }

    public void setUserRefId(String userRefId) {
        this.userRefId = userRefId;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Article article = (Article) o;
        if (article.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), article.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Article{" +
            "id=" + getId() +
            ", articleName='" + getArticleName() + "'" +
            ", articleSubject='" + getArticleSubject() + "'" +
            ", catagoeryRefId='" + getCatagoeryRefId() + "'" +
            ", articleText='" + getArticleText() + "'" +
            ", articleAuthorFullName='" + getArticleAuthorFullName() + "'" +
            ", articleDate='" + getArticleDate() + "'" +
            ", userName='" + getUserName() + "'" +
            ", userRefId='" + getUserRefId() + "'" +
            "}";
    }
}
