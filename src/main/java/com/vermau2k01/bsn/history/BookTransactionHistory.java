package com.vermau2k01.bsn.history;

import com.vermau2k01.bsn.books.Books;
import com.vermau2k01.bsn.common.BaseEntity;
import com.vermau2k01.bsn.user.Users;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BookTransactionHistory extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Books book;

    private boolean returned;
    private boolean returnApproved;


}
