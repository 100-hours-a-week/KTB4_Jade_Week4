package kakaotech.task4.domain.user.repository;

import kakaotech.task4.domain.user.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public class PureJavaUserRepository implements UserRepository {

    @Override
    public void save(User user) {

    }

    @Override
    public User findById(int id) {
        return null;
    }
}
