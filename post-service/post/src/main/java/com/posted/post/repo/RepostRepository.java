package com.posted.post.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.posted.post.model.Repost;
import com.posted.post.model.compositeId.RepostPK;

@Repository
public interface RepostRepository extends JpaRepository<Repost, RepostPK>{

    Optional<List<Repost>> findByPostId(Long Id);

    @Query("""
            select r from Repost r where r.post.id = :postId and r.username = :username
            """)
    Optional<Repost> findByIdAndUsername(/*@Param("postId")*/Long postId, /*@Param("username")*/String username);
}
