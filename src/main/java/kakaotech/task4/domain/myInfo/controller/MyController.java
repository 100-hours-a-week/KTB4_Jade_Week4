package kakaotech.task4.domain.myInfo.controller;

import jakarta.validation.Valid;
import kakaotech.task4.common.resolver.CurrentUser;
import kakaotech.task4.domain.myInfo.api.MyApi;
import kakaotech.task4.domain.myInfo.dto.req.UpdateMySecurityRequest;
import kakaotech.task4.domain.myInfo.dto.res.MyBasicInfoResponse;
import kakaotech.task4.domain.myInfo.dto.req.UpdateMyBasicInfoRequest;
import kakaotech.task4.domain.myInfo.dto.res.UpdateMyBasicInfoResponse;
import kakaotech.task4.domain.myInfo.service.MyService;
import kakaotech.task4.domain.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/my")
@AllArgsConstructor
public class MyController implements MyApi {
    private final MyService myService;

    @GetMapping("/basic-info")
    @Override
    public ResponseEntity<?> getMyBasicInfo(@CurrentUser User user) {
        MyBasicInfoResponse response = myService.getMyBasicInfo(user);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/basic-info")
    @Override
    public ResponseEntity<?> updateMyBasicInfo(
            @CurrentUser User user,
            @Valid @RequestBody UpdateMyBasicInfoRequest request) {
        UpdateMyBasicInfoResponse response = myService.updateMyBasicInfo(user, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/security")
    @Override
    public ResponseEntity<?> updateMySecurity(
            @CurrentUser User user,
            @Valid @RequestBody UpdateMySecurityRequest request) {
        myService.updateMySecurity(user, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping
    @Override
    public ResponseEntity<?> deleteAccount(@CurrentUser User user) {
        myService.deleteAccount(user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}