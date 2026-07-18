package kakaotech.task4.myInfo;

import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.domain.member.entity.Member;
import kakaotech.task4.domain.member.service.MemberService;
import kakaotech.task4.domain.myInfo.code.MyInfoExceptionCode;
import kakaotech.task4.domain.myInfo.dto.req.UpdateMyBasicInfoRequest;
import kakaotech.task4.domain.myInfo.dto.req.UpdateMySecurityRequest;
import kakaotech.task4.domain.myInfo.dto.res.MyBasicInfoResponse;
import kakaotech.task4.domain.myInfo.dto.res.UpdateMyBasicInfoResponse;
import kakaotech.task4.domain.myInfo.service.MyInfoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyInfoServiceTest {

    @Mock
    private MemberService memberService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MyInfoService myInfoService;

    @Test
    @DisplayName("내 기본 정보를 조회하면 현재 회원의 이메일, 닉네임, 프로필 이미지를 반환한다")
    void getMyBasicInfo() {
        // given
        Member member = createMember();

        // when
        MyBasicInfoResponse response = myInfoService.getMyBasicInfo(member);

        // then
        assertThat(response.email()).isEqualTo("jade@example.com");
        assertThat(response.nickname()).isEqualTo("jade");
        assertThat(response.profileImageUrl()).isEqualTo("https://example.com/profile.png");
    }

    @Test
    @DisplayName("내 기본 정보를 수정하면 현재 회원의 닉네임과 프로필 이미지가 변경된다")
    void updateMyBasicInfo() {
        // given
        Member member = createMember();
        UpdateMyBasicInfoRequest request = new UpdateMyBasicInfoRequest("newJade", "https://example.com/new-profile.png");

        when(memberService.existsByNickname("newJade")).thenReturn(false);

        // when
        UpdateMyBasicInfoResponse response = myInfoService.updateMyBasicInfo(member, request);

        // then
        assertThat(response.nickname()).isEqualTo("newJade");
        assertThat(response.profileImageUrl()).isEqualTo("https://example.com/new-profile.png");
        assertThat(member.getNickname()).isEqualTo("newJade");
        assertThat(member.getProfileImageUrl()).isEqualTo("https://example.com/new-profile.png");
    }

    @Test
    @DisplayName("변경할 기본 정보가 없으면 BAD_REQUEST 예외가 발생한다")
    void updateMyBasicInfoWithEmptyRequest() {
        // given
        Member member = createMember();
        UpdateMyBasicInfoRequest request = new UpdateMyBasicInfoRequest(null, null);

        // when & then
        assertThatThrownBy(() -> myInfoService.updateMyBasicInfo(member, request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", MyInfoExceptionCode.BAD_REQUEST);

        verify(memberService, never()).existsByNickname(null);
    }

    @Test
    @DisplayName("비밀번호와 비밀번호 확인이 다르면 INVALID_PASSWORD 예외가 발생한다")
    void updateMySecurityWithPasswordMismatch() {
        // given
        Member member = createMember();
        UpdateMySecurityRequest request = new UpdateMySecurityRequest("Password123!", "Password1234!", "Password12345!");
        when(passwordEncoder.matches("Password123!", member.getPassword())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> myInfoService.updateMySecurity(member, request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", MyInfoExceptionCode.INVALID_PASSWORD);

        assertThat(member.getPassword()).isEqualTo("oldPassword123!");
    }

    @Test
    @DisplayName("회원 탈퇴를 하면 현재 회원의 deletedAt이 기록된다")
    void deleteAccount() {
        // given
        Member member = createMember();

        // when
        myInfoService.deleteAccount(member);

        // then
        assertThat(member.getDeletedAt()).isNotNull();
    }

    private Member createMember() {
        return Member.builder()
                .memberUuid("member_uuid")
                .email("jade@example.com")
                .password("oldPassword123!")
                .nickname("jade")
                .profileImageUrl("https://example.com/profile.png")
                .build();
    }
}
