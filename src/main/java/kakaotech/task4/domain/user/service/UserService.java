package kakaotech.task4.domain.user.service;

import kakaotech.task4.common.uuid.UuidCreator;
import kakaotech.task4.common.uuid.UuidPrefix;
import kakaotech.task4.domain.auth.dto.SignUpRequest;
import kakaotech.task4.domain.user.entity.User;
import kakaotech.task4.domain.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void signUp(SignUpRequest request) {
        String uuid = UuidCreator.create(UuidPrefix.USER);
        User user = User.of(uuid, request);
        userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

}
