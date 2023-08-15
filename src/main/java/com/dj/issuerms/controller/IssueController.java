package com.dj.issuerms.controller;

import com.dj.issuerms.model.Issue;
import com.dj.issuerms.service.IssueService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
@Tag(name = "ISSUES-SERVICE")
public class IssueController {
    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @GetMapping
    public List<Issue> getAllIssues() {
        return issueService.getAllIssues();
    }

    @GetMapping("/{id}")
    public Issue getIssueById(@PathVariable Long id) {
        return issueService.getIssueById(id);
    }

    @PostMapping
    public ResponseEntity<String> issueBook(@RequestBody Issue issue) {
        Issue issuedIssue = issueService.issueBook(issue);
        if (issuedIssue != null) {
            return ResponseEntity.ok("Book issued successfully.");
        } else {
            return ResponseEntity.badRequest().body("Book not available or insufficient copies.");
        }
    }

    @DeleteMapping("/{id}")
    public void cancelIssue(@PathVariable Long id) {
        issueService.cancelIssue(id);
    }
}