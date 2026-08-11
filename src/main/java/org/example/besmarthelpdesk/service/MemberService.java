package org.example.besmarthelpdesk.service;

import org.example.besmarthelpdesk.dto.request.RegisterRequest;
import org.example.besmarthelpdesk.dto.request.UpdateMemberRequest;
import org.example.besmarthelpdesk.dto.response.MemberResponse;

import java.util.List;
import java.util.UUID;

public interface MemberService {
    MemberResponse createMember(RegisterRequest request);
    MemberResponse getMemberById(UUID id);
    MemberResponse getMemberByIdentifier(String identifier);
    List<MemberResponse> getAllMembers();
    MemberResponse updateMember(String identifier, UpdateMemberRequest request);
    void deleteMember(String identifier);
}

