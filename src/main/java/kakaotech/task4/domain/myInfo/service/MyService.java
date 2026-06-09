package kakaotech.task4.domain.myInfo.service;

import kakaotech.task4.common.exception.CustomException;
import kakaotech.task4.common.exception.ExceptionCode.GlobalExceptionCode;
import kakaotech.task4.domain.myInfo.code.MyExceptionCode;
import kakaotech.task4.domain.myInfo.dto.req.UpdateMyBasicInfoRequest;
import kakaotech.task4.domain.myInfo.dto.res.MyBasicInfoResponse;
import kakaotech.task4.domain.myInfo.dto.res.UpdateMyBasicInfoResponse;
import kakaotech.task4.domain.user.entity.User;
import kakaotech.task4.domain.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MyService {
    private final UserService userService;

    public MyBasicInfoResponse getMyBasicInfo(String userUuid) {
        User user = findUserByUuid(userUuid);
        return MyBasicInfoResponse.from(user);
    }

    private User findUserByUuid(String userUuid) {
        return userService.findByUuid(userUuid)
                .orElseThrow(() -> new CustomException(GlobalExceptionCode.INTERNAL_SERVER_ERROR));
    }

    public UpdateMyBasicInfoResponse updateMyBasicInfo(String userUuid, UpdateMyBasicInfoRequest request) {
        User user = findUserByUuid(userUuid);
        validateDuplicateNickname(request.nickname());
        user.updateBasicInfo(request);
        return UpdateMyBasicInfoResponse.from(user);
    }

    private void validateDuplicateNickname(String nickname) {
        if (userService.existsByNickname(nickname)) {
            throw new CustomException(MyExceptionCode.DUPLICATE_NICKNAME);
        }
    }
}