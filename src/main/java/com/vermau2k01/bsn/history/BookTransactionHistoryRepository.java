package com.vermau2k01.bsn.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookTransactionHistoryRepository
        extends JpaRepository<BookTransactionHistory, Long> {

    @Query("""
          SELECT history
           from BookTransactionHistory
           history where history.user.id = :userId
""")
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Integer userId);


    @Query("""
          SELECT history
           from BookTransactionHistory
           history where history.book.owner.id = :userId
""")
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, Integer userId);


    @Query("""
SELECT (count(*)>0) as isBorrowed from BookTransactionHistory bookTransactionHistory
where bookTransactionHistory.user.id = :userId
 and bookTransactionHistory.book.id = :bookId
 and bookTransactionHistory.returnApproved = false
""")
    boolean isAlreadyBorrowedByUser(Integer bookId, Integer userId);


    @Query("""
     Select transactions from BookTransactionHistory transactions
     where transactions.user.id = :userId AND transactions.book.id = :bookId
     and transactions.returned = false and transactions.returnApproved = false
""")
    Optional<BookTransactionHistory> findByBookIdAndUserId(Integer bookId, Integer userId);


    @Query("""
     Select transactions from BookTransactionHistory transactions
     where transactions.book.owner.id = :userId AND transactions.book.id = :bookId
     and transactions.returned = true and
      transactions.returnApproved = false
""")
    Optional<BookTransactionHistory> findByBookIdAndOwnerId(Integer bookId, Integer userId);
}
