package org.example.besmarthelpdesk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.besmarthelpdesk.constant.MessageConstants;
import org.example.besmarthelpdesk.dto.ResponseGeneral;
import org.example.besmarthelpdesk.dto.request.RegisterRequest;
import org.example.besmarthelpdesk.dto.request.UpdateMemberRequest;
import org.example.besmarthelpdesk.dto.response.MemberResponse;
import org.example.besmarthelpdesk.facade.MemberFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Member API", description = "Endpoints cho quản lý và đăng ký thành viên")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberFacade memberFacade;

    @Operation(summary = "Đăng ký thành viên mới", description = "Tạo tài khoản member mới với vai trò ADMIN, DEVELOPER hoặc CLIENT. Mật khẩu gửi lên sẽ được BCrypt mã hóa và lưu an toàn vào DB.")
    @PostMapping
    public ResponseEntity<ResponseGeneral<MemberResponse>> registerMember(@Valid @RequestBody RegisterRequest request) {
        log.info("(registerMember) request: {}", request);
        MemberResponse response = memberFacade.register(request);
        ResponseGeneral<MemberResponse> body = ResponseGeneral.success(MessageConstants.REGISTER_SUCCESS, response);
        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    @Operation(summary = "Lấy danh sách tất cả thành viên", description = "Trả về danh sách tất cả thành viên trong hệ thống (Chỉ dành cho ADMIN)")
    @GetMapping
    public ResponseEntity<ResponseGeneral<List<MemberResponse>>> getAllMembers() {
        log.info("(getAllMembers)");
        List<MemberResponse> responses = memberFacade.getAllMembers();
        ResponseGeneral<List<MemberResponse>> body = ResponseGeneral.success(responses);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @Operation(summary = "Xem thông tin chi tiết 1 thành viên theo UUID / Member ID / Email")
    @GetMapping("/{identifier}")
    public ResponseEntity<ResponseGeneral<MemberResponse>> getMemberDetail(@PathVariable String identifier) {
        log.info("(getMemberDetail) identifier: {}", identifier);
        MemberResponse response = memberFacade.getMemberByIdentifier(identifier);
        ResponseGeneral<MemberResponse> body = ResponseGeneral.success(response);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @Operation(summary = "Cập nhật thông tin thành viên", description = "Cập nhật thông tin thành viên theo UUID / Member ID / Email")
    @PutMapping("/{identifier}")
    public ResponseEntity<ResponseGeneral<MemberResponse>> updateMember(
            @PathVariable String identifier,
            @Valid @RequestBody UpdateMemberRequest request) {
        log.info("(updateMember) identifier: {}, request: {}", identifier, request);
        MemberResponse response = memberFacade.updateMember(identifier, request);
        ResponseGeneral<MemberResponse> body = ResponseGeneral.success("Member updated successfully", response);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @Operation(summary = "Xóa mềm thành viên (Soft Delete)", description = "Chuyển isDeleted=true, status=inactive và lưu thời điểm xóa vào deletedAt")
    @DeleteMapping("/{identifier}")
    public ResponseEntity<ResponseGeneral<Void>> deleteMember(@PathVariable String identifier) {
        log.info("(deleteMember) identifier: {}", identifier);
        memberFacade.deleteMember(identifier);
        ResponseGeneral<Void> body = ResponseGeneral.success("Member deleted successfully", null);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }
}


