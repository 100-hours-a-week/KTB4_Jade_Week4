package kakaotech.task4.domain.user.repository;

import kakaotech.task4.domain.user.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository {
    void save(User user);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    Optional<User> findByEmail(String email);
    Optional<User> findByUuid(String userUuid);
    void addAll(List<User> users);
}
