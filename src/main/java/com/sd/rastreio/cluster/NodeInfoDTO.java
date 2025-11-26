package com.sd.rastreio.cluster;

public record NodeInfoDTO(
        Integer nodeId,
        long startTime,
        boolean isLeader) {
}