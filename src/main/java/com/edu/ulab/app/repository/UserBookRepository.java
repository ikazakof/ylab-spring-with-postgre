package com.edu.ulab.app.repository;

import com.edu.ulab.app.entity.PersonBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Table;
import java.util.List;

@Repository
@Table(name = "person_book")
public interface UserBookRepository extends JpaRepository<PersonBook, Long> {


    List<PersonBook> findPersonBookByPersonId(Long personId);

    PersonBook getPersonBookByPersonId(Long personId);

    void deletePersonBookByPersonId(Long personId);

    boolean existsByPersonId(Long personId);

}
