package kakaotech.task4.domain.member.entity;

import jakarta.persistence.*;
import kakaotech.task4.common.baseEntity.BaseEntity;
import kakaotech.task4.domain.auth.dto.req.SignUpRequest;
import kakaotech.task4.domain.myInfo.dto.req.UpdateMyBasicInfoRequest;
import lombok.*;

@Entity
@Table
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, unique = true, updatable = false)
    private String memberUuid;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(nullable = false, length = 20)
    private String nickname;

    @Column(nullable = false, length = 512)
    private String profileImageUrl;

    @Builder
    public Member(String memberUuid, String email, String password, String nickname, String profileImageUrl) {
        this.memberUuid = memberUuid;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    public static Member of(String memberUuid, SignUpRequest request) {
        return Member.builder()
                .memberUuid(memberUuid)
                .email(request.email())
                .password(request.password())
                .nickname(request.nickname())
                .profileImageUrl(request.profileImageUrl())
                .build();
    }

    public void updateBasicInfo(UpdateMyBasicInfoRequest request) {
        if (request.nickname() != null) this.nickname = request.nickname();
        if (request.profileImageUrl() != null) this.profileImageUrl = request.profileImageUrl();
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Member member)) return false;
        return memberUuid != null && memberUuid.equals(member.getMemberUuid());
    }

    @Override
    public int hashCode() {
        return (memberUuid != null) ? memberUuid.hashCode() : 0;
    }
}