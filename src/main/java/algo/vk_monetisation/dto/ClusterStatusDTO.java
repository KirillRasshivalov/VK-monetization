package algo.vk_monetisation.dto;

import java.util.List;

public record ClusterStatusDTO(
        String nodeId,
        boolean complianceListenerEnabled,
        boolean notificationListenerEnabled,
        boolean engagementSchedulerEnabled,
        boolean eisStubMode,
        List<EisIntegrationLogDTO> recentEisLogs
) {
}
