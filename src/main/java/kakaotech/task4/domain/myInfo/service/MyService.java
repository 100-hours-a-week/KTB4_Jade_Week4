package kakaotech.task4.domain.myInfo.service;

import kakaotech.task4.common.exception.CommonFieldError;
import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.domain.myInfo.code.MyExceptionCode;
import kakaotech.task4.domain.myInfo.dto.req.UpdateMyBasicInfoRequest;
import kakaotech.task4.domain.myInfo.dto.req.UpdateMySecurityRequest;
import kakaotech.task4.domain.myInfo.dto.res.MyBasicInfoResponse;
import kakaotech.task4.domain.myInfo.dto.res.UpdateMyBasicInfoResponse;
import kakaotech.task4.domain.member.entity.Member;
import kakaotech.task4.domain.member.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class MyService {
    private final MemberService memberService;

    public MyBasicInfoResponse getMyBasicInfo(Member member) {
        return MyBasicInfoResponse.from(member);
    }

    public UpdateMyBasicInfoResponse updateMyBasicInfo(Member member, UpdateMyBasicInfoRequest request) {
        validateAllNull(request);
        validateDuplicateNickname(request.nickname());

        member.updateBasicInfo(request);
        return UpdateMyBasicInfoResponse.from(member);
    }

    public void updateMySecurity(Member member, UpdateMySecurityRequest request) {
        validatePasswordMatch(request);
        member.updatePassword(request.password());
    }

    public void deleteAccount(Member member) {
        member.softDelete();
    }

    private void validateAllNull(UpdateMyBasicInfoRequest request) {
        if (request.isAllNull()) {
            throw new CustomException(MyExceptionCode.BAD_REQUEST);
        }
    }

    private void validateDuplicateNickname(String nickname) {
        if (memberService.existsByNickname(nickname)) {
            throw new CustomException(MyExceptionCode.DUPLICATE_NICKNAME);
        }
    }

    private void validatePasswordMatch(UpdateMySecurityRequest request) {
        if (!request.validatePasswordMatch()) {
            Map<String, Object> fieldErrors = new HashMap<>();
            fieldErrors.put(CommonFieldError.PASSWORD_MISMATCH.getField(), CommonFieldError.PASSWORD_MISMATCH.getMessage());
            throw new CustomException(MyExceptionCode.INVALID_PASSWORD, fieldErrors);
        }
    }
}