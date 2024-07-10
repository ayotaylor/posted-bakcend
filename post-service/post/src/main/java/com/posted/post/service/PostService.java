package com.posted.post.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.posted.post.messaging.kafka.KafkaProducer;
import com.posted.post.model.Fave;
import com.posted.post.model.Post;
import com.posted.post.model.Repost;
import com.posted.post.repo.FaveRepository;
import com.posted.post.repo.PostRepository;
import com.posted.post.repo.RepostRepository;

@Service
@Transactional
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private FaveRepository faveRepository;

    @Autowired
    private RepostRepository repostRepository;

    @Autowired
    private KafkaProducer kafkaProducer;

    public Post createPost(String content, String username) {

        Post post = new Post();
        //post.setCreatedAt(LocalDateTime.now());
        //post.setUpdatedAt(LocalDateTime.now());
        post.setFaveCount(0);
        post.setRepostCount(0);
        post.setContent(content);
        post.setUsername(username);

        Post savedPost = postRepository.save(post);

        // Publish post created event to Kafka
        kafkaProducer.sendPostCreatedEvent(savedPost);

        return savedPost;
    }

    //@Cacheable(value = "popularTweets", unless = "#result == null || !#result.popular")
    public Post getPostById(Long postId) {

        Post post =  postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));;

        return post;
    }

    public List<Post> getPostsByUsername(String username) {

        List<Post> posts =  postRepository.findByUsername(username);

        return posts;
    }

    public void deletePost(Long postId) {
        Post postToDelete = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        postRepository.deleteById(postId);

        kafkaProducer.sendPostDeletedEvent(postToDelete);
    }

    public void favePost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        Fave fave = new Fave();
        fave.setPost(post);
        fave.setUsername(username);
        faveRepository.save(fave);

        // Update post like count
        post.setFaveCount(post.getFaveCount() + 1);
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);

        // Publish post faved event to Kafka
        kafkaProducer.sendPostFavedEvent(fave);
    }

    public void unfavePost(Long postId, String username) {   // maybe unlike?

        Fave fave = faveRepository.findByPostIdAndUsername(postId, username)
        .orElseThrow(() -> new IllegalArgumentException("Faved Post not found"));
        faveRepository.delete(fave);

        // Update tweet like count
        Post post = fave.getPost();
        post.setFaveCount(post.getFaveCount() - 1);
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);

        // Publish post unfaved event to Kafka
        kafkaProducer.sendPostUnfavedEvent(fave);
    }

    public void repost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        Repost repost = new Repost();
        repost.setPost(post);
        repost.setUsername(username);
        repostRepository.save(repost);

        // Update tweet retweet count
        post.setRepostCount(post.getRepostCount() + 1);
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);

        // publish post reposted event to kafka
        kafkaProducer.sendPostRepostedEvent(repost);
    }

    public void undoRepost(Long repostId, String username) {
        Repost repost = repostRepository.findByIdAndUsername(repostId, username) //handle dataaccessexception
            .orElseThrow(() -> new IllegalArgumentException("RePost not found"));
        repostRepository.delete(repost);

        // Update tweet retweet count
        Post post = repost.getPost();
        post.setRepostCount(post.getRepostCount() - 1);
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);

        kafkaProducer.sendUndoRepostedEvent(repost);
    }

    public Post updatePost(Long postId, Post updatedPost) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        // Update tweet fields
        post.setContent(updatedPost.getContent());
        post.setUpdatedAt(LocalDateTime.now());
        updatedPost = postRepository.save(post);

        kafkaProducer.sendPostUpdatedEvent(post);

        return updatedPost;
    }

    public List<Fave> getLikesByPost(Long postId) {
        return faveRepository.findByPostId(postId)
        .orElseThrow(() -> new IllegalArgumentException("Likes not found"));
    }

    public List<Repost> getRepostsByPost(Long postId) {
        return repostRepository.findByPostId(postId)
        .orElseThrow(() -> new IllegalArgumentException("Reposts not found"));
    }
}
