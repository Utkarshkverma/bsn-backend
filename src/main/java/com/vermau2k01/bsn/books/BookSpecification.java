package com.vermau2k01.bsn.books;

import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<Books> withOwnerId(Integer ownerId){
        return (root,query,criteriaBuilder)->
                criteriaBuilder.equal(root.get("owner").get("id"),ownerId);
    }
}
