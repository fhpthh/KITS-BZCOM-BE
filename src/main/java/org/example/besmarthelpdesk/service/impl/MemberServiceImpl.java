package org.example.besmarthelpdesk.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.besmarthelpdesk.constant.MessageConstants;
import org.example.besmarthelpdesk.dto.request.RegisterRequest;
import org.example.besmarthelpdesk.dto.request.UpdateMemberRequest;
import org.example.besmarthelpdesk.dto.response.MemberResponse;
import org.example.besmarthelpdesk.entity.Member;
import org.example.besmarthelpdesk.exception.BadRequestException;
import org.example.besmarthelpdesk.exception.ResourceNotFoundException;
import org.example.besmarthelpdesk.repository.MemberRepository;
import org.example.besmarthelpdesk.service.MemberService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public MemberResponse createMember(RegisterRequest request) {
        log.info("(createMember) request: {}", request);

        if (memberRepository.existsByEmail(request.getEmail())) {
            log.warn("(createMember) email {} already exists", request.getEmail());
            throw new BadRequestException(MessageConstants.EMAIL_REGISTERED);
        }

        String memberId = request.getMemberId();
        if (memberId != null && !memberId.trim().isEmpty()) {
            memberId = memberId.trim();
            if (memberRepository.existsByMemberId(memberId)) {
                log.warn("(createMember) memberId {} already exists", memberId);
                throw new BadRequestException(MessageConstants.MEMBER_ID_EXISTS);
            }
        } else {
            memberId = generateUniqueMemberId(request.getRole());
        }

        String status = (request.getStatus() != null && !request.getStatus().trim().isEmpty())
                ? request.getStatus().trim()
                : "active";

        Member member = Member.builder()
                .memberId(memberId)
                .companyId(request.getCompanyId() != null ? request.getCompanyId().trim() : null)
                .email(request.getEmail().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName().trim())
                .phone(request.getPhone() != null ? request.getPhone().trim() : null)
                .role(request.getRole())
                .status(status)
                .isDeleted(false)
                .build();

        Member savedMember = memberRepository.save(member);
        log.info("(createMember) savedMember ID: {}, memberId: {}", savedMember.getId(), savedMember.getMemberId());

        return mapToResponse(savedMember);
    }

    private String generateUniqueMemberId(org.example.besmarthelpdesk.enums.Role role) {
        String prefix;
        if (role == null) {
            prefix = "MB_";
        } else {
            switch (role) {
                case ADMIN -> prefix = "MB_ADM";
                case DEVELOPER -> prefix = "MB_DEV";
                case CLIENT -> prefix = "MB_CLI";
                default -> prefix = "MB_";
            }
        }

        String generated;
        int attempts = 0;
        do {
            String suffix = String.format("%04d", (int) (Math.random() * 10000));
            generated = prefix + suffix;
            attempts++;
        } while (memberRepository.existsByMemberId(generated) && attempts < 100);

        if (attempts >= 100) {
            generated = "MB_" + UUID.randomUUID().toString().substring(0, 8);
        }
        return generated;
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponse getMemberById(UUID id) {
        log.info("(getMemberById) id: {}", id);
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("(getMemberById) member with ID {} not found", id);
                    return new ResourceNotFoundException(MessageConstants.MEMBER_NOT_FOUND + id);
                });
        return mapToResponse(member);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponse getMemberByIdentifier(String identifier) {
        log.info("(getMemberByIdentifier) identifier: {}", identifier);
        Member member = findMemberByIdentifier(identifier);
        return mapToResponse(member);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberResponse> getAllMembers() {
        log.info("(getAllMembers)");
        return memberRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MemberResponse updateMember(String identifier, UpdateMemberRequest request) {
        log.info("(updateMember) identifier: {}, request: {}", identifier, request);
        Member member = findMemberByIdentifier(identifier);

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            member.setName(request.getName().trim());
        }
        if (request.getPhone() != null) {
            member.setPhone(request.getPhone().trim());
        }
        if (request.getCompanyId() != null) {
            member.setCompanyId(request.getCompanyId().trim());
        }
        if (request.getRole() != null) {
            member.setRole(request.getRole());
        }
        if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
            member.setStatus(request.getStatus().trim());
        }

        Member updated = memberRepository.save(member);
        log.info("(updateMember) updated member ID: {}", updated.getId());
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteMember(String identifier) {
        log.info("(deleteMember) identifier: {}", identifier);
        Member member = findMemberByIdentifier(identifier);
        member.setIsDeleted(true);
        member.setStatus("inactive");
        member.setDeletedAt(java.time.Instant.now());
        memberRepository.save(member);
        log.info("(deleteMember) soft deleted member ID: {}", member.getId());
    }

    private Member findMemberByIdentifier(String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new BadRequestException(MessageConstants.BAD_REQUEST);
        }
        String cleanId = identifier.trim();
        try {
            UUID uuid = UUID.fromString(cleanId);
            return memberRepository.findById(uuid)
                    .orElseGet(() -> memberRepository.findByMemberId(cleanId)
                    .orElseGet(() -> memberRepository.findByEmail(cleanId)
                    .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.MEMBER_NOT_FOUND + cleanId))));
        } catch (IllegalArgumentException e) {
            return memberRepository.findByMemberId(cleanId)
                    .orElseGet(() -> memberRepository.findByEmail(cleanId)
                    .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.MEMBER_NOT_FOUND + cleanId)));
        }
    }

    private MemberResponse mapToResponse(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .memberId(member.getMemberId())
                .companyId(member.getCompanyId())
                .email(member.getEmail())
                .name(member.getName())
                .phone(member.getPhone())
                .role(member.getRole())
                .status(member.getStatus())
                .isDeleted(member.getIsDeleted())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}
