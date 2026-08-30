package kakaotech.task4.domain.myInfo.controller;

import jakarta.validation.Valid;
import kakaotech.task4.common.resolver.CurrentMember;
import kakaotech.task4.common.security.AuthenticatedMember;
import kakaotech.task4.domain.myInfo.api.MyInfoApi;
import kakaotech.task4.domain.myInfo.dto.req.UpdateMySecurityRequest;
import kakaotech.task4.domain.myInfo.dto.res.MyBasicInfoResponse;
import kakaotech.task4.domain.myInfo.dto.req.UpdateMyBasicInfoRequest;
import kakaotech.task4.domain.myInfo.dto.res.UpdateMyBasicInfoResponse;
import kakaotech.task4.domain.myInfo.service.MyInfoService;
import kakaotech.task4.common.response.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me")
@AllArgsConstructor
public class MyInfoController implements MyInfoApi {
    private final MyInfoService myInfoService;

    @GetMapping("/basic-info")
    @Override
    public ResponseEntity<?> getMyBasicInfo(@CurrentMember AuthenticatedMember member) {
        MyBasicInfoResponse response = myInfoService.getMyBasicInfo(member.memberUuid());
        return ApiResponse.success(response).toEntity();
    }

    @PatchMapping("/basic-info")
    @Override
    public ResponseEntity<?> updateMyBasicInfo(
            @CurrentMember AuthenticatedMember member,
            @Valid @RequestBody UpdateMyBasicInfoRequest request) {
        UpdateMyBasicInfoResponse response =
                myInfoService.updateMyBasicInfo(member.memberUuid(), request);
        return ApiResponse.success(response).toEntity();
    }

    @PutMapping("/security")
    @Override
    public ResponseEntity<?> updateMySecurity(
            @CurrentMember AuthenticatedMember member,
            @Valid @RequestBody UpdateMySecurityRequest request) {
        myInfoService.updateMySecurity(member.memberUuid(), request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping
    @Override
    public ResponseEntity<?> deleteAccount(@CurrentMember AuthenticatedMember member) {
        myInfoService.deleteAccount(member.memberUuid());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
