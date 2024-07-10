package com.posted.post.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.posted.post.model.Fave;
import com.posted.post.model.compositeId.FavePK;

@Repository
public interface FaveRepository extends JpaRepository<Fave, FavePK> {

    @Query("""
            select f from Fave f where f.post.id = :postId and f.username = :username
            """)
    Optional<Fave> findByPostIdAndUsername(/*@Param("postId")*/Long postId, /*@Param("username")*/String username);

    Optional<List<Fave>> findByPostId(Long id);
}