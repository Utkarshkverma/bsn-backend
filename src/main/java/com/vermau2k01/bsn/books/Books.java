package com.vermau2k01.bsn.books;

import com.vermau2k01.bsn.common.BaseEntity;
import com.vermau2k01.bsn.feedback.FeedBack;
import com.vermau2k01.bsn.history.BookTransactionHistory;
import com.vermau2k01.bsn.user.Users;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Books extends BaseEntity {

    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private String bookCover;
    private boolean archived;
    private boolean sharable;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Users owner;
    @OneToMany(mappedBy = "books")
    private List<FeedBack> feedbacks;
    @OneToMany(mappedBy = "book")
    private List<BookTransactionHistory> bookTransactionHistoryList;
    @Transient
    public double getRate()
    {
        if(feedbacks == null || feedbacks.isEmpty()) {
            return 0.0;
        }

        var rate = this
                .feedbacks
                .stream()
                .mapToDouble(FeedBack::getNote)
                .average()
                .orElse(0.0);
        return Math.round(rate*10.0)/10.0;
    }


}
