package kakaotech.task4.domain.myInfo.controller;

import jakarta.validation.Valid;
import kakaotech.task4.domain.myInfo.api.MyApi;
import kakaotech.task4.domain.myInfo.dto.req.UpdateMySecurityRequest;
import kakaotech.task4.domain.myInfo.dto.res.MyBasicInfoResponse;
import kakaotech.task4.domain.myInfo.dto.req.UpdateMyBasicInfoRequest;
import kakaotech.task4.domain.myInfo.dto.res.UpdateMyBasicInfoResponse;
import kakaotech.task4.domain.myInfo.service.MyService;
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
    public ResponseEntity<?> getMyBasicInfo(
            @RequestHeader("Authorization") String userUuid) {
        MyBasicInfoResponse response = myService.getMyBasicInfo(userUuid);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/basic-info")
    @Override
    public ResponseEntity<?> updateMyBasicInfo(
            @RequestHeader("Authorization") String userUuid,
            @Valid @RequestBody UpdateMyBasicInfoRequest request) {
        UpdateMyBasicInfoResponse response = myService.updateMyBasicInfo(userUuid, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/security")
    @Override
    public ResponseEntity<?> updateMySecurity(
            @RequestHeader("Authorization") String userUuid,
            @Valid @RequestBody UpdateMySecurityRequest request) {
        myService.updateMySecurity(userUuid, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping
    @Override
    public ResponseEntity<?> deleteAccount(
            @RequestHeader("Authorization") String userUuid) {
        myService.deleteAccount(userUuid);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}