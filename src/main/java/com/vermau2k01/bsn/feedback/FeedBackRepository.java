package com.vermau2k01.bsn.feedback;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedBackRepository extends JpaRepository<FeedBack, Integer> {

    @Query("""
            SELECT feedback
            FROM FeedBack  feedback
            WHERE feedback.books.id = :bookId
""")
    Page<FeedBack> findAllByBookId(@Param("bookId") Integer bookId, Pageable pageable);
}
