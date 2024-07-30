package com.vermau2k01.bsn.books;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Books, Integer>, JpaSpecificationExecutor<Books> {

    Optional<Books> findByTitle(String title);

    @Query("""
            SELECT book from Books book
            where book.archived = false
            AND book.sharable = true
            AND book.owner.id != :userId
""")
    Page<Books> findAllDisplayableBooks(Pageable pageable, Integer userId);


}
