package com.mps.assignment.service;
import com.mps.assignment.dto.AssignmentDtos.ConflictView; import org.junit.jupiter.api.Test; import java.util.List; import static org.junit.jupiter.api.Assertions.*;
class ConflictRulesTest { @Test void blockingConflictsAreRecognized(){var service=new ConflictDetectionService(null,null);assertTrue(service.hasBlocking(List.of(new ConflictView("OVERLAP","x",true))));assertFalse(service.hasBlocking(List.of(new ConflictView("UNAVAILABLE","x",false))));} }
