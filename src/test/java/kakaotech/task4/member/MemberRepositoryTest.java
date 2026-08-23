package kakaotech.task4.member;

import kakaotech.task4.domain.member.entity.Member;
import kakaotech.task4.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 탈퇴_회원을_제외하고_가장_먼저_등록된_활성_회원을_조회한다() {
        Member deletedMember = saveMember("deleted-uuid", "deleted@example.com", "deleted");
        deletedMember.softDelete();
        memberRepository.saveAndFlush(deletedMember);
        Member activeMember = saveMember("active-uuid", "active@example.com", "active");

        assertThat(memberRepository.findFirstActiveMember())
                .contains(activeMember);
    }

    private Member saveMember(String uuid, String email, String nickname) {
        return memberRepository.saveAndFlush(Member.builder()
                .memberUuid(uuid)
                .email(email)
                .password("password")
                .nickname(nickname)
                .profileImageUrl("https://example.com/profile.png")
                .build());
    }
}
