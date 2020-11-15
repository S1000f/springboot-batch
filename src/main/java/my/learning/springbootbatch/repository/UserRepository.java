package my.learning.springbootbatch.repository;

import my.learning.springbootbatch.domain.User;
import my.learning.springbootbatch.domain.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByUpdatedDateBeforeAndStatusEquals(LocalDateTime localDateTime, UserStatus status);
}
