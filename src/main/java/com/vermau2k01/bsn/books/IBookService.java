package com.vermau2k01.bsn.books;

import com.vermau2k01.bsn.common.PageResponse;
import org.springframework.security.core.Authentication;

public interface IBookService {

    Integer save(BookRequest request, Authentication connectedUser);


    BookResponse findById(Integer bookId);


    PageResponse<BookResponse> findAll(int page, int size, Authentication connectedUser);

    PageResponse<BookResponse> findAllByOwner(int page, int size, Authentication connectedUser);
}
