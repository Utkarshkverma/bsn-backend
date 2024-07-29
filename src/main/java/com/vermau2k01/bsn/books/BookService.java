package com.vermau2k01.bsn.books;


import com.vermau2k01.bsn.common.PageResponse;
import com.vermau2k01.bsn.user.Users;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
public class BookService implements IBookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public Integer save(BookRequest request, Authentication connectedUser) {
        Users user = (Users) connectedUser.getPrincipal();
        Books book = bookMapper.toBook(request);
        book.setOwner(user);
        bookRepository.save(book);
        return book.getId();
    }

    @Override
    public BookResponse findById(Integer bookId) {
        return bookRepository
                .findById(bookId)
                .map(bookMapper::toBookResponse)
                .orElseThrow(()-> new EntityNotFoundException("No book exists with ID :: "+ bookId));
    }

    @Override
    public PageResponse<BookResponse> findAll(int page,
                                              int size, Authentication connectedUser) {
        Users user = (Users) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdDate").descending());
        Page<Books> books = bookRepository.findAllDisplayableBooks(pageable,
                user.getId());
        List<BookResponse> bookResponses = books
                .stream().map(bookMapper::toBookResponse).toList();

        return new PageResponse<>(bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),books.isFirst(),books.isLast());
    }

    @Override
    public PageResponse<BookResponse> findAllByOwner(int page, int size,
                                                     Authentication connectedUser) {
        Users user = (Users) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdDate").descending());
        Page<Books> books = bookRepository
                .findAll(BookSpecification
                        .withOwnerId(user.getId()),pageable);

        List<BookResponse> bookResponses = books
                .stream().map(bookMapper::toBookResponse).toList();

        return new PageResponse<>(bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),books.isFirst(),books.isLast());


    }


}
