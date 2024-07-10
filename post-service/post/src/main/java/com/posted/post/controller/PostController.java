package com.posted.post.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.posted.post.model.Post;
import com.posted.post.service.PostService;

@RestController
@RequestMapping("/api/post")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("/create")
    public ResponseEntity<?> createPost(@RequestBody Map<String, String> request) {

        String content = request.get("content");
        String username = request.get("username");
        Post post = postService.createPost(content, username);

        return ResponseEntity.ok(post);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> getTweetById(@PathVariable("tweetId") String postId)
    {

        Long id = Long.valueOf(postId);
        var post = postService.getPostById(id);

        //add code to handle null
        // if(post==null)
        // {
        //     throw  new NoRecordFoundException(tweetId + " not found");
        // }
        return new ResponseEntity<>(post,HttpStatus.OK);
    }

    @GetMapping("/user/{username}/tweet")
    public ResponseEntity<?> getAllUsersTweet(@PathVariable("username") String username)
    {
        var tweet = postService.getPostsByUsername(username);

        //System.out.println(tweet);
       // System.out.println(tweetsRepository.findByUserId(userId));

        return new ResponseEntity<>(tweet,HttpStatus.OK);
    }

    @DeleteMapping("/{tweetId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {

        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{tweetId}/fave")
    public ResponseEntity<?> favePost(@PathVariable Long postId, @RequestBody Map<String, String> request) {
        String username = request.get("username");
        postService.favePost(postId, username);
        return new ResponseEntity<>("Tweet faved Sucessfully...!!!", HttpStatus.CREATED);
    }

    @PostMapping("/{tweetId}/unfave")
    public ResponseEntity<?> unfavePost(@PathVariable Long postId, @RequestBody Map<String, String> request) {

        String username = request.get("username");
        postService.unfavePost(postId, username);
        return new ResponseEntity<>("Tweet unfaved Sucessfully...!!!", HttpStatus.CREATED);
    }

    @PostMapping("/{postId}/repost")
    public ResponseEntity<?> repost(@PathVariable Long postId, @RequestBody Map<String, String> request) {
        String username = request.get("username");
        postService.repost(postId, username);
        return new ResponseEntity<>("Tweet liked Sucessfully...!!!", HttpStatus.CREATED);
    }

    @PostMapping("/{postId}/undo-repost")
    public ResponseEntity<?> undoRepost(@PathVariable Long postId, @RequestBody Map<String, String> request) {
        String username = request.get("username");
        postService.undoRepost(postId, username);
        return ResponseEntity.noContent().build();
    }
}
