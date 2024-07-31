package com.vermau2k01.bsn.feedback;

import com.vermau2k01.bsn.books.Books;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FeedbackMapper {


    public FeedBack toFeedBack(FeedBackRequest request) {
        return FeedBack
                .builder()
                .note(request.note())
                .comment(request.comment())
                .books(Books
                        .builder()
                        .id(request
                                .bookId())
                        .build())
                .build();
    }

    public FeedbackResponse toFeedbackResponse(FeedBack feedback, Integer id) {
        return FeedbackResponse.builder()
                .note(feedback.getNote())
                .comment(feedback.getComment())
                .ownFeedback(Objects.equals(feedback.getCreatedBy(), id))
                .build();
    }
}
