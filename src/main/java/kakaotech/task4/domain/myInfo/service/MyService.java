package kakaotech.task4.domain.myInfo.service;

import kakaotech.task4.common.exception.CommonFieldError;
import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.domain.myInfo.code.MyExceptionCode;
import kakaotech.task4.domain.myInfo.dto.req.UpdateMyBasicInfoRequest;
import kakaotech.task4.domain.myInfo.dto.req.UpdateMySecurityRequest;
import kakaotech.task4.domain.myInfo.dto.res.MyBasicInfoResponse;
import kakaotech.task4.domain.myInfo.dto.res.UpdateMyBasicInfoResponse;
import kakaotech.task4.domain.user.entity.User;
import kakaotech.task4.domain.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class MyService {
    private final UserService userService;

    public MyBasicInfoResponse getMyBasicInfo(User user) {
        return MyBasicInfoResponse.from(user);
    }

    public UpdateMyBasicInfoResponse updateMyBasicInfo(User user, UpdateMyBasicInfoRequest request) {
        validateAllNull(request);
        validateDuplicateNickname(request.nickname());

        user.updateBasicInfo(request);
        return UpdateMyBasicInfoResponse.from(user);
    }

    public void updateMySecurity(User user, UpdateMySecurityRequest request) {
        validatePasswordMatch(request);
        user.updatePassword(request.password());
    }

    public void deleteAccount(User user) {
        user.softDelete();
    }

    private void validateAllNull(UpdateMyBasicInfoRequest request) {
        if (request.isAllNull()) {
            throw new CustomException(MyExceptionCode.BAD_REQUEST);
        }
    }

    private void validateDuplicateNickname(String nickname) {
        if (userService.existsByNickname(nickname)) {
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