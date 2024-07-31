package com.vermau2k01.bsn.feedback;

import com.vermau2k01.bsn.common.PageResponse;
import org.springframework.security.core.Authentication;

public interface IFeedBackService {
    Integer save(FeedBackRequest request, Authentication connectedUser);

    PageResponse<FeedbackResponse> findAllFeedbacksByBook(Integer bookId, int page, int size, Authentication connectedUser);
}
