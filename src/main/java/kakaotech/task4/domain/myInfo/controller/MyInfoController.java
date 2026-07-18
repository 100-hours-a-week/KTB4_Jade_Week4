package kakaotech.task4.domain.myInfo.controller;

import jakarta.validation.Valid;
import kakaotech.task4.common.resolver.CurrentMember;
import kakaotech.task4.domain.myInfo.api.MyInfoApi;
import kakaotech.task4.domain.myInfo.dto.req.UpdateMySecurityRequest;
import kakaotech.task4.domain.myInfo.dto.res.MyBasicInfoResponse;
import kakaotech.task4.domain.myInfo.dto.req.UpdateMyBasicInfoRequest;
import kakaotech.task4.domain.myInfo.dto.res.UpdateMyBasicInfoResponse;
import kakaotech.task4.domain.myInfo.service.MyInfoService;
import kakaotech.task4.common.response.ApiResponse;
import kakaotech.task4.domain.member.entity.Member;
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
    public ResponseEntity<?> getMyBasicInfo(@CurrentMember Member member) {
        MyBasicInfoResponse response = myInfoService.getMyBasicInfo(member);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @PatchMapping("/basic-info")
    @Override
    public ResponseEntity<?> updateMyBasicInfo(
            @CurrentMember Member member,
            @Valid @RequestBody UpdateMyBasicInfoRequest request) {
        UpdateMyBasicInfoResponse response = myInfoService.updateMyBasicInfo(member, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @PutMapping("/security")
    @Override
    public ResponseEntity<?> updateMySecurity(
            @CurrentMember Member member,
            @Valid @RequestBody UpdateMySecurityRequest request) {
        myInfoService.updateMySecurity(member, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping
    @Override
    public ResponseEntity<?> deleteAccount(@CurrentMember Member member) {
        myInfoService.deleteAccount(member);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}