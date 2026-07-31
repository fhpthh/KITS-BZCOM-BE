package org.example.besmarthelpdesk.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.besmarthelpdesk.constant.MessageConstants;
import org.example.besmarthelpdesk.dto.request.RegisterRequest;
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

        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(request.getRole())
                .build();

        Member savedMember = memberRepository.save(member);
        log.info("(createMember) savedMember ID: {}", savedMember.getId());

        return mapToResponse(savedMember);
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
    public List<MemberResponse> getAllMembers() {
        log.info("(getAllMembers)");
        return memberRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private MemberResponse mapToResponse(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .role(member.getRole())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
