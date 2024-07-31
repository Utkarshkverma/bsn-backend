package com.vermau2k01.bsn.books;


import com.vermau2k01.bsn.common.PageResponse;
import com.vermau2k01.bsn.exception.OperationNotPermittedException;
import com.vermau2k01.bsn.files.IfileStorageService;
import com.vermau2k01.bsn.history.BookTransactionHistory;
import com.vermau2k01.bsn.history.BookTransactionHistoryRepository;
import com.vermau2k01.bsn.user.Users;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class BookService implements IBookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookTransactionHistoryRepository transactionHistoryRepository;
    private final IfileStorageService fileStorageService;

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

    @Override
    public PageResponse<BorrowedBookResponse> findAllBorrowedBook(int page, int size, Authentication connectedUser) {
        Users user = (Users) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdDate").descending());
        Page<BookTransactionHistory> bookTransactionHistories =
                transactionHistoryRepository
                        .findAllBorrowedBooks(pageable,user.getId());

        List<BorrowedBookResponse> borrowedBookResponses = bookTransactionHistories
                .stream().map(bookMapper::toBorrowedBookResponse).toList();

        return new PageResponse<>(
                borrowedBookResponses,
                bookTransactionHistories.getNumber(),
                bookTransactionHistories.getSize(),
                bookTransactionHistories.getTotalElements(),
                bookTransactionHistories.getTotalPages(),
                bookTransactionHistories.isFirst(),
                bookTransactionHistories.isLast());
    }

    @Override
    public PageResponse<BorrowedBookResponse> findAllReturnedBook(int page, int size, Authentication connectedUser) {
        Users user = (Users) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdDate").descending());
        Page<BookTransactionHistory> bookTransactionHistories =
                transactionHistoryRepository
                        .findAllReturnedBooks(pageable,user.getId());

        List<BorrowedBookResponse> borrowedBookResponses = bookTransactionHistories
                .stream().map(bookMapper::toBorrowedBookResponse).toList();

        return new PageResponse<>(
                borrowedBookResponses,
                bookTransactionHistories.getNumber(),
                bookTransactionHistories.getSize(),
                bookTransactionHistories.getTotalElements(),
                bookTransactionHistories.getTotalPages(),
                bookTransactionHistories.isFirst(),
                bookTransactionHistories.isLast());

    }

    @Override
    public Integer updateSharableStatus(Integer bookId,
                                        Authentication connectedUser) {
        Books book = bookRepository
                .findById(bookId).orElseThrow(()-> new EntityNotFoundException("No book found with ID :: "+ bookId));
        Users user = (Users) connectedUser.getPrincipal();
        if(!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update book sharable status");
        }
        book.setSharable(!book.isSharable());
        bookRepository.save(book);
        return bookId;
    }

    @Override
    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {
        Books book = bookRepository
                .findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException
                        ("No book found with ID :: "+ bookId));
        Users user = (Users) connectedUser.getPrincipal();
        if(!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException
                    ("You cannot update book archived status");
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return bookId;
    }

    @Override
    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
        Books book = bookRepository
                .findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException
                        ("No book found with ID :: "+ bookId));
        if(book.isArchived() || !book.isSharable())
            throw new OperationNotPermittedException("The requested book cannot be borrowed");

        Users user = (Users) connectedUser.getPrincipal();
        if(Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("Owner cannot borrow his/her own book");
        }
        final boolean isAlreadyBorrowed = transactionHistoryRepository.isAlreadyBorrowedByUser(bookId, user.getId());
        if(isAlreadyBorrowed) {
            throw new OperationNotPermittedException("The requested book is already borrowed");
        }

        BookTransactionHistory build = BookTransactionHistory
                .builder()
                .book(book)
                .returnApproved(false)
                .user(user)
                .build();

        return transactionHistoryRepository.save(build).getId();
    }

    @Override
    public Integer returnBook(Integer bookId, Authentication connectedUser) {
        Books book = bookRepository
                .findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException
                        ("No book found with ID :: "+ bookId));
        if(book.isArchived() || !book.isSharable())
            throw new OperationNotPermittedException("The requested book cannot be returned");
        Users user = (Users) connectedUser.getPrincipal();
        if(Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("Owner cannot return his/her own book");
        }

        BookTransactionHistory bookTransactionHistory = transactionHistoryRepository.findByBookIdAndUserId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("You didn't borrowed the book"));

        bookTransactionHistory.setReturned(true);
        return transactionHistoryRepository.save(bookTransactionHistory).getId();

    }

    @Override
    public Integer approvedReturnBook(Integer bookId, Authentication connectedUser) {
        Books book = bookRepository
                .findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException
                        ("No book found with ID :: "+ bookId));
        if(book.isArchived() || !book.isSharable())
            throw new OperationNotPermittedException("The return request cannot be approved");
        Users user = (Users) connectedUser.getPrincipal();
        if(!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("Only owner can approve the return of book");
        }

        BookTransactionHistory bookTransactionHistory = transactionHistoryRepository.findByBookIdAndOwnerId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("The book is not returned yet so you cannot approve it"));

        bookTransactionHistory.setReturnApproved(true);
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    @Override
    public void uploadBookCover(MultipartFile file, Authentication connectedUser, Integer bookId) {
        Books book = bookRepository
                .findById(bookId)
                .orElseThrow(()-> new EntityNotFoundException
                        ("No book found with ID :: "+ bookId));

        Users user = (Users) connectedUser.getPrincipal();

        var bookCover = fileStorageService.saveFile(file, book, user.getId());
        book.setBookCover(bookCover);
        bookRepository.save(book);
    }


}
