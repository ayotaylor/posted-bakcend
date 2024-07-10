package com.posted.post.messaging.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.posted.post.model.Fave;
import com.posted.post.model.Post;
import com.posted.post.model.Repost;

@Component
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPostCreatedEvent(Post post) {
        kafkaTemplate.send("postCreatedTopic", post);
    }

    public void sendPostDeletedEvent(Post post) {
        kafkaTemplate.send("postDeletedTopic", post);
    }

    public void sendPostUpdatedEvent(Post post) {
        kafkaTemplate.send("postUpdatedTopic", post);
    }

    public void sendPostFavedEvent(Fave fave) {
        kafkaTemplate.send("postFavedTopic", fave);
    }

    public void sendPostUnfavedEvent(Fave fave) {
        kafkaTemplate.send("postUnfavedTopic", fave);
    }

    public void sendPostRepostedEvent(Repost repost) {
        kafkaTemplate.send("postRepostedTopic", repost);
    }

    public void sendUndoRepostedEvent(Repost repost) {
        kafkaTemplate.send("postUndoRepostTopic", repost);
    }
}
