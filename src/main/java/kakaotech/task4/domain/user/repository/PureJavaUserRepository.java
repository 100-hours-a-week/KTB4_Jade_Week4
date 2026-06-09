package kakaotech.task4.domain.user.repository;

import kakaotech.task4.domain.user.entity.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class PureJavaUserRepository implements UserRepository {
    private final List<User> users = new ArrayList<>();
    private final AtomicInteger sequence = new AtomicInteger(1);

    @Override
    public void save(User user) {
        user.setUserId(sequence.getAndIncrement());
        users.add(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return users.stream()
                .filter(user -> !user.isDeleted())
                .anyMatch(user -> user.getEmail().equals(email));
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return users.stream()
                .filter(user -> !user.isDeleted())
                .anyMatch(user -> user.getNickname().equals(nickname));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.stream()
                .filter(user -> !user.isDeleted())
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public Optional<User> findByUuid(String userUuid) {
        return users.stream()
                .filter(user -> !user.isDeleted())
                .filter(user -> user.getUserUuid().equals(userUuid))
                .findFirst();
    }

    @Override
    public void addAll(List<User> users) {
        this.users.addAll(users);
    }

}
