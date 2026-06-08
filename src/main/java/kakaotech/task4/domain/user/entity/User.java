package kakaotech.task4.domain.user.entity;

import jakarta.validation.constraints.NotNull;
import kakaotech.task4.domain.auth.dto.req.SignUpRequest;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Setter
    private int userId;

    @NotNull
    private String userUuid;

    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String nickname;

    @NotNull
    private String profileImageUrl;

    @Builder
    public User(int userId, String userUuid, String email, String password, String nickname, String profileImageUrl) {
        this.userId = userId;
        this.userUuid = userUuid;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    public static User of(String userUuid, SignUpRequest request) {
        return User.builder()
                .userUuid(userUuid)
                .email(request.email())
                .password(request.password())
                .nickname(request.nickname())
                .profileImageUrl(request.profileImageUrl())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return userUuid != null && userUuid.equals(user.getUserUuid());
    }

    @Override
    public int hashCode() {
        return (userUuid != null) ? userUuid.hashCode() : 0;
    }

}