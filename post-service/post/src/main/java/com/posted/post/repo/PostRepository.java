package com.posted.post.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.posted.post.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @NonNull Optional<Post> findById(@NonNull Long id);

    List<Post> findByUsername(String username);
}
