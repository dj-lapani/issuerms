package com.dj.issuerms.repository;

import com.dj.issuerms.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueRepository extends JpaRepository<Issue, Long> {

}