package com.vermau2k01.bsn.books;

import com.vermau2k01.bsn.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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



}
