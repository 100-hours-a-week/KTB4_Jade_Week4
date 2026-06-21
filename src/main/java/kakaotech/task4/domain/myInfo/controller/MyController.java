package kakaotech.task4.domain.myInfo.controller;

import jakarta.validation.Valid;
import kakaotech.task4.common.resolver.CurrentMember;
import kakaotech.task4.domain.myInfo.api.MyApi;
import kakaotech.task4.domain.myInfo.dto.req.UpdateMySecurityRequest;
import kakaotech.task4.domain.myInfo.dto.res.MyBasicInfoResponse;
import kakaotech.task4.domain.myInfo.dto.req.UpdateMyBasicInfoRequest;
import kakaotech.task4.domain.myInfo.dto.res.UpdateMyBasicInfoResponse;
import kakaotech.task4.domain.myInfo.service.MyService;
import kakaotech.task4.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me")
@AllArgsConstructor
public class MyController implements MyApi {
    private final MyService myService;

    @GetMapping("/basic-info")
    @Override
    public ResponseEntity<?> getMyBasicInfo(@CurrentMember Member member) {
        MyBasicInfoResponse response = myService.getMyBasicInfo(member);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/basic-info")
    @Override
    public ResponseEntity<?> updateMyBasicInfo(
            @CurrentMember Member member,
            @Valid @RequestBody UpdateMyBasicInfoRequest request) {
        UpdateMyBasicInfoResponse response = myService.updateMyBasicInfo(member, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/security")
    @Override
    public ResponseEntity<?> updateMySecurity(
            @CurrentMember Member member,
            @Valid @RequestBody UpdateMySecurityRequest request) {
        myService.updateMySecurity(member, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping
    @Override
    public ResponseEntity<?> deleteAccount(@CurrentMember Member member) {
        myService.deleteAccount(member);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}