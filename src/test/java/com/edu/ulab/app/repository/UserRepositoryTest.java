package com.edu.ulab.app.repository;

import com.edu.ulab.app.config.SystemJpaTest;
import com.edu.ulab.app.entity.Person;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import static com.vladmihalcea.sql.SQLStatementCountValidator.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты репозитория {@link UserRepository}.
 */
@SystemJpaTest
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        SQLStatementCountValidator.reset();
    }


    @DisplayName("Сохранить пользователя. Число select должно равняться 1, параметры созданного и сохраненного пользователя должны совпадать")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql"
    })
    void insertPerson_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(111);
        person.setTitle("reader");
        person.setFullName("Test Test");

        //When
        Person result = userRepository.save(person);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getAge()).isEqualTo(111);
        assertThat(result.getTitle()).isEqualTo("reader");
        assertThat(result.getFullName()).isEqualTo("Test Test");
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Проверить наличие пользователя. Число select должно равняться 1, в результате проверки наличия в бд возвращается true")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql"
    })
    void checkPersonById_thenAssertDmlCount() {
        //Given
        Long userId = 1001L;

        //When
        boolean userExistInDB = userRepository.existsById(userId);

        //Then
        assertThat(userExistInDB).isTrue();
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);

    }

    @DisplayName("Обновить пользователя. Число select должно равняться 1, параметры созданного и сохраненного пользователя должны совпадать id пользователя не меняется")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void updatePerson_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setId(1001L);
        person.setAge(55);
        person.setTitle("reader updated");
        person.setFullName("default user updated");


        //When
        Person result = userRepository.save(person);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1001L);
        assertThat(result.getAge()).isEqualTo(55);
        assertThat(result.getTitle()).isEqualTo("reader updated");
        assertThat(result.getFullName()).isEqualTo("default user updated");
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Получить пользователя. Число select должно равняться 1, параметры созданного и сохраненного пользователя должны совпадать id пользователя не меняется")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getPersonById_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setId(1001L);
        person.setAge(55);
        person.setTitle("reader");
        person.setFullName("default user");


        //When
        Person result = userRepository.findById(person.getId()).orElse(new Person());

        //Then
        assertThat(result.getId()).isEqualTo(1001L);
        assertThat(result.getAge()).isEqualTo(55);
        assertThat(result.getTitle()).isEqualTo("reader");
        assertThat(result.getFullName()).isEqualTo("default user");
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Проверить наличие пользователя. Число select должно равняться 2, delete 1, в результате проверки наличия в бд возвращается true")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql"
    })
    void deletePersonById_thenAssertDmlCount() {
        //Given
        Long userId = 1001L;

        //When
        userRepository.deleteById(userId);
        boolean userWithUserIdExist = userRepository.existsById(userId);

        //Then
        assertThat(userWithUserIdExist).isFalse();
        assertSelectCount(2);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(1);

    }

    // update
    // get
    // get all
    // delete

    // * failed
}
