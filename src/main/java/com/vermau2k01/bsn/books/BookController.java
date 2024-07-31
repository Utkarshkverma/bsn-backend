package com.vermau2k01.bsn.books;

import com.vermau2k01.bsn.common.PageResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@Tag(name = "Books")
public class BookController {

     private final IBookService bookService;

     @PostMapping
    public ResponseEntity<Integer> saveBook(@Valid @RequestBody BookRequest request,
                                            Authentication connectedUser) {
         return ResponseEntity.ok(bookService.save(request,connectedUser));
     }

     @GetMapping("{book-id}")
    public ResponseEntity<BookResponse> getBook(@PathVariable("book-id") Integer bookId) {
         return ResponseEntity.ok(bookService.findById(bookId));
     }


     @GetMapping
     public ResponseEntity<PageResponse<BookResponse>> getAllBooks(
             @RequestParam(name = "page",defaultValue = "0",required = false) int page,
             @RequestParam(name = "size",defaultValue = "10",required = false) int size,
             Authentication connectedUser
     ) {

         return ResponseEntity.ok(bookService.findAll(page,size,connectedUser));
     }

     @GetMapping("/owner")
    public ResponseEntity<PageResponse<BookResponse>> getAllBooksByOwner(
             @RequestParam(name = "page",defaultValue = "0",required = false) int page,
             @RequestParam(name = "size",defaultValue = "10",required = false) int size,
             Authentication connectedUser
     )
     {
         return ResponseEntity.ok(bookService.findAllByOwner(page,size,connectedUser));
     }

    @GetMapping("/borrowed")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> getAllBorrowedBooks(
            @RequestParam(name = "page",defaultValue = "0",required = false) int page,
            @RequestParam(name = "size",defaultValue = "10",required = false) int size,
            Authentication connectedUser
    )
    {
        return ResponseEntity
                .ok(bookService.findAllBorrowedBook(page,size,connectedUser));
    }



    @GetMapping("/returned")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> getAllReturnedBooks(
            @RequestParam(name = "page",defaultValue = "0",required = false) int page,
            @RequestParam(name = "size",defaultValue = "10",required = false) int size,
            Authentication connectedUser
    )
    {
        return ResponseEntity
                .ok(bookService.findAllReturnedBook(page,size,connectedUser));
    }

    @PatchMapping("/sharable/{book-id}")
    public ResponseEntity<Integer> updateSharableStatus(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    )
    {
        return ResponseEntity
                .ok(bookService
                        .updateSharableStatus(bookId,connectedUser));
    }


    @PatchMapping("/archived/{book-id}")
    public ResponseEntity<Integer> updateArchivedStatus(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    )
    {
        return ResponseEntity
                .ok(bookService
                        .updateArchivedStatus(bookId,connectedUser));
    }


    @PostMapping("/borrow/{book-id}")
    public ResponseEntity<Integer> BorrowBook(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    )
    {
        return ResponseEntity.ok(bookService.borrowBook(bookId,connectedUser));
    }


    @PatchMapping("/borrow/return/{book-id}")
    public ResponseEntity<Integer> ReturnBook(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    )
    {
        return ResponseEntity.ok(bookService.returnBook(bookId,connectedUser));
    }

    @PatchMapping("/borrow/return/approved/{book-id}")
    public ResponseEntity<Integer> approvedReturnBook(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    )
    {
        return ResponseEntity.ok(bookService.approvedReturnBook(bookId,connectedUser));
    }


    @PostMapping(value = "/cover/{book-id}",consumes = "multipart/form-data")
    public ResponseEntity<?> uploadCover(
            @PathVariable("book-id") Integer bookId,
            @Parameter()
            @RequestPart("file") MultipartFile file,
            Authentication connectedUser
    )
    {
        bookService.uploadBookCover(file,connectedUser,bookId);
        return ResponseEntity.accepted().build();
    }



}
