package kakaotech.task4.domain.user.repository;

import kakaotech.task4.domain.user.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {
    void save(User user);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}
