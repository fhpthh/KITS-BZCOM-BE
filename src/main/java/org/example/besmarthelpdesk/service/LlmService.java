package org.example.besmarthelpdesk.service;

import org.example.besmarthelpdesk.dto.request.LlmClassifyRequest;
import org.example.besmarthelpdesk.dto.request.LlmPriorityRequest;
import org.example.besmarthelpdesk.dto.response.LlmClassifyResponse;
import org.example.besmarthelpdesk.dto.response.LlmPriorityResponse;
import org.example.besmarthelpdesk.dto.response.LlmSummaryResponse;
import org.example.besmarthelpdesk.dto.response.SmartTriageResponse;

public interface LlmService {

    LlmClassifyResponse classifyCategory(LlmClassifyRequest request);

    LlmPriorityResponse suggestPriority(LlmPriorityRequest request);

    LlmSummaryResponse generateSummaryByRequestId(Long id);

    SmartTriageResponse autoTriage(String title, String description);
}
