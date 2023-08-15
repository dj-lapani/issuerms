package com.dj.issuerms.service;

import com.dj.issuerms.model.Book;
import com.dj.issuerms.model.Issue;
import com.dj.issuerms.repository.IssueRepository;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IssueService {
    private final IssueRepository issueRepository;
    private WebClient.Builder webClientBuilder;

    public IssueService(IssueRepository issueRepository, WebClient.Builder webClientBuilder) {
        this.issueRepository = issueRepository;
        this.webClientBuilder = webClientBuilder;
    }



    public List<Issue> getAllIssues() {
        return issueRepository.findAll();
    }

    public Issue getIssueById(Long id) {
        return issueRepository.findById(id).orElse(null);
    }

    public Issue issueBook(Issue issue) {
        // Check book availability and update issuedCopies in BookService
        Book book = getBook(issue);

        if (book != null && book.getIssuedCopies() + issue.getNoOfCopies() <= book.getTotalCopies()) {
            book.setIssuedCopies(book.getIssuedCopies() + issue.getNoOfCopies());

            updateBook(book);

            issue.setIssuedDate(LocalDateTime.now());
            return issueRepository.save(issue);
        }

        return null;
    }

    public void cancelIssue(Long id) {
        Issue issue = issueRepository.findById(id).orElse(null);
        if (issue != null) {
            Book book = getBook(issue);
            if (book != null) {
                book.setIssuedCopies(book.getIssuedCopies() - issue.getNoOfCopies());
                updateBook(book);
            }

            issueRepository.deleteById(id);
        }
    }

    private void updateBook(Book book) {
        Book updatedBook = webClientBuilder.build().post().
                uri("http://book-service/api/books").
                body(Mono.just(book),Book.class).retrieve().bodyToMono(Book.class).block();
    }

    private Book getBook(Issue issue) {
        Book book = webClientBuilder.build().get().
                uri("http://book-service/api/books/{isbn}", issue.getIsbn())
                .retrieve()
                .bodyToMono(Book.class).block();
        return book;
    }
}