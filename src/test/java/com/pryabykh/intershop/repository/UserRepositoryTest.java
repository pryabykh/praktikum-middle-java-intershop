package com.pryabykh.intershop.repository;

import com.pryabykh.intershop.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserRepositoryTest extends DataJpaPostgreSQLTestContainerBaseTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Sql(statements = {
            "insert into intershop.users (name) values ('ADMIN');",
    })
    void findByItemIdAndUserId_whenEntityExists_shouldReturnIt() {
        Long userId = userRepository.findAll().stream().findFirst().map(User::getId).orElseThrow();
        assertNotNull(userId);
    }
}
