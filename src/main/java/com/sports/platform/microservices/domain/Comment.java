package com.sports.platform.microservices.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A Comment.
 */
@Document(collection = "comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("comments")
    private String comments;

    @Field("comment_date")
    private Instant commentDate;

    @Field("comment_author")
    private String commentAuthor;

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

    public String getComments() {
        return comments;
    }

    public Comment comments(String comments) {
        this.comments = comments;
        return this;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Instant getCommentDate() {
        return commentDate;
    }

    public Comment commentDate(Instant commentDate) {
        this.commentDate = commentDate;
        return this;
    }

    public void setCommentDate(Instant commentDate) {
        this.commentDate = commentDate;
    }

    public String getCommentAuthor() {
        return commentAuthor;
    }

    public Comment commentAuthor(String commentAuthor) {
        this.commentAuthor = commentAuthor;
        return this;
    }

    public void setCommentAuthor(String commentAuthor) {
        this.commentAuthor = commentAuthor;
    }

    public String getUserName() {
        return userName;
    }

    public Comment userName(String userName) {
        this.userName = userName;
        return this;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRefId() {
        return userRefId;
    }

    public Comment userRefId(String userRefId) {
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
        Comment comment = (Comment) o;
        if (comment.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), comment.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Comment{" +
            "id=" + getId() +
            ", comments='" + getComments() + "'" +
            ", commentDate='" + getCommentDate() + "'" +
            ", commentAuthor='" + getCommentAuthor() + "'" +
            ", userName='" + getUserName() + "'" +
            ", userRefId='" + getUserRefId() + "'" +
            "}";
    }
}
