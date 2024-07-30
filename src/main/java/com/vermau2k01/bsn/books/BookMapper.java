package com.vermau2k01.bsn.books;

import com.vermau2k01.bsn.history.BookTransactionHistory;
import org.springframework.stereotype.Service;

@Service
public class BookMapper {


    public Books toBook(BookRequest request) {
        return Books
                .builder()
                .id(request.id())
                .title(request.title())
                .authorName(request.authorName())
                .synopsis(request.synopsis())
                .archived(false)
                .sharable(request.sharable())
                .build();
    }

    public BookResponse toBookResponse(Books books) {

        return BookResponse
                .builder()
                .id(books.getId())
                .title(books.getTitle())
                .authorName(books.getAuthorName())
                .synopsis(books.getSynopsis())
                .isbn(books.getIsbn())
                .rate(books.getRate())
                .sharable(books.isSharable())
                .owner(books.getOwner().getFullName())
                .build();
    }

    public BorrowedBookResponse toBorrowedBookResponse(BookTransactionHistory history) {
        return BorrowedBookResponse.builder()
                .id(history.getBook().getId())
                .title(history.getBook().getTitle())
                .authorName(history.getBook().getAuthorName())
                .isbn(history.getBook().getIsbn())
                .rate(history.getBook().getRate())
                .returned(history.isReturned())
                .returnedApproved(history.isReturnApproved())
                .build();
    }
}
