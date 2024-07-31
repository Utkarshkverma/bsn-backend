package com.vermau2k01.bsn.feedback;

import com.vermau2k01.bsn.books.BookRepository;
import com.vermau2k01.bsn.books.Books;
import com.vermau2k01.bsn.common.PageResponse;
import com.vermau2k01.bsn.exception.OperationNotPermittedException;
import com.vermau2k01.bsn.user.Users;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedBackService  implements IFeedBackService {

    private final BookRepository bookRepository;
    private final FeedBackRepository feedBackRepository;
    private final FeedbackMapper feedbackMapper;

    @Override
    public Integer save(FeedBackRequest request, Authentication connectedUser) {
        Books book = bookRepository
                .findById(request.bookId())
                .orElseThrow(()-> new EntityNotFoundException
                        ("No book found with ID :: "+ request.bookId()));

        if(book.isArchived() || !book.isSharable())
            throw new OperationNotPermittedException("Feedback cannot be given as the book might be archived or not sharable");


        Users user = (Users) connectedUser.getPrincipal();
        if(Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException
                    ("Owner cannot provide feedback to his/her book");
        }

        FeedBack feedback = feedbackMapper.toFeedBack(request);
        return feedBackRepository.save(feedback).getId();
    }

    @Override
    public PageResponse<FeedbackResponse> findAllFeedbacksByBook(Integer bookId, int page, int size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page, size);
        Users user = ((Users) connectedUser.getPrincipal());
        Page<FeedBack> feedbacks = feedBackRepository.findAllByBookId(bookId, pageable);
        List<FeedbackResponse> feedbackResponses = feedbacks.stream()
                .map(f -> feedbackMapper.toFeedbackResponse(f, user.getId()))
                .toList();
        return new PageResponse<>(
                feedbackResponses,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );
    }
}
