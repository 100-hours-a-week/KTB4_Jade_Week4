package kakaotech.task4.domain.myInfo.service;

import kakaotech.task4.common.exception.CommonFieldError;
import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.domain.auth.code.AuthExceptionCode;
import kakaotech.task4.domain.myInfo.code.MyInfoExceptionCode;
import kakaotech.task4.domain.myInfo.dto.req.UpdateMyBasicInfoRequest;
import kakaotech.task4.domain.myInfo.dto.req.UpdateMySecurityRequest;
import kakaotech.task4.domain.myInfo.dto.res.MyBasicInfoResponse;
import kakaotech.task4.domain.myInfo.dto.res.UpdateMyBasicInfoResponse;
import kakaotech.task4.domain.file.service.ProfileImageUrlValidator;
import kakaotech.task4.domain.member.entity.Member;
import kakaotech.task4.domain.member.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class MyInfoService {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final ProfileImageUrlValidator profileImageUrlValidator;

    @Transactional(readOnly = true)
    public MyBasicInfoResponse getMyBasicInfo(String memberUuid) {
        Member member = findCurrentMember(memberUuid);
        return MyBasicInfoResponse.from(member);
    }

    @Transactional
    public UpdateMyBasicInfoResponse updateMyBasicInfo(
            String memberUuid,
            UpdateMyBasicInfoRequest request) {
        Member member = findCurrentMember(memberUuid);
        validateAllNull(request);
        validateDuplicateNickname(member, request.nickname());
        profileImageUrlValidator.validate(request.profileImageUrl());

        member.updateBasicInfo(request);
        return UpdateMyBasicInfoResponse.from(member);
    }

    @Transactional
    public void updateMySecurity(String memberUuid, UpdateMySecurityRequest request) {
        Member member = findCurrentMember(memberUuid);
        validateNowPassword(member, request.nowPassword());
        validatePasswordMatch(request);

        String encodedPassword = passwordEncoder.encode(request.nextPassword());
        member.updatePassword(encodedPassword);
    }

    @Transactional
    public void deleteAccount(String memberUuid) {
        Member member = findCurrentMember(memberUuid);
        member.softDelete();
    }

    private Member findCurrentMember(String memberUuid) {
        return memberService.findByUuid(memberUuid, AuthExceptionCode.UNAUTHORIZED);
    }

    private void validateAllNull(UpdateMyBasicInfoRequest request) {
        if (request.hasNoChanges()) {
            throw new CustomException(MyInfoExceptionCode.BAD_REQUEST);
        }
    }

    private void validateDuplicateNickname(Member member, String nickname) {
        if (nickname == null || nickname.equals(member.getNickname())) {
            return;
        }
        if (memberService.existsByNickname(nickname)) {
            throw new CustomException(MyInfoExceptionCode.DUPLICATE_NICKNAME);
        }
    }

    private void validatePasswordMatch(UpdateMySecurityRequest request) {
        if (!request.validatePasswordMatch()) {
            throwInvalidPassword(CommonFieldError.PASSWORD_MISMATCH);
        }
    }

    private void validateNowPassword(Member member, String nowPassword) {
        if (!passwordEncoder.matches(nowPassword, member.getPassword())) {
            throwInvalidPassword(CommonFieldError.INVALID_NOW_PASSWORD);
        }
    }

    private void throwInvalidPassword(CommonFieldError fieldError) {
        Map<String, Object> fieldErrors = new HashMap<>();
        fieldErrors.put(fieldError.getField(), fieldError.getMessage());
        throw new CustomException(MyInfoExceptionCode.INVALID_PASSWORD, fieldErrors);
    }
}
