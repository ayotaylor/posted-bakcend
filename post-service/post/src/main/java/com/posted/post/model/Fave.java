package com.posted.post.model;

import com.posted.post.model.compositeId.FavePK;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@IdClass(FavePK.class)
public class Fave {

    @Id
    @ManyToOne
    @JoinColumn(name = "postId", nullable = false)
    private Post post;

    @Id
    @Column(nullable = false)
    private String username;
}
