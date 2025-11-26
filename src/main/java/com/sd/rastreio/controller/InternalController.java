package com.sd.rastreio.controller;

import com.sd.rastreio.cluster.NodeContext;
import com.sd.rastreio.cluster.NodeInfoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
public class InternalController {

    private final NodeContext nodeContext;

    public InternalController(NodeContext nodeContext) {
        this.nodeContext = nodeContext;
    }

    // Endpoint para outros nós consultarem a saúde e idade deste nó
    @GetMapping("/health")
    public ResponseEntity<NodeInfoDTO> getHealth() {

        NodeInfoDTO info = new NodeInfoDTO(
                nodeContext.getNodeId(),
                nodeContext.getStartTime(),
                nodeContext.isLeader());

        return ResponseEntity.ok(info);
    }
}