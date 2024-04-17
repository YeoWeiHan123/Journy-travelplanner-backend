package sg.edu.nus.journybackend.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import sg.edu.nus.journybackend.entity.Comment;
import sg.edu.nus.journybackend.exception.ResourceNotFoundException;
import sg.edu.nus.journybackend.service.CommentService;
import sg.edu.nus.journybackend.service.MemberService;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin("*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final MemberService memberService;
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<?> getComments() {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = userDetails.getUsername();
            Long memberId = memberService.findByUsername(username).getMemberId();

            List<Comment> comments = commentService.retrieveCommentsByMemberId(memberId);

            for (Comment comment : comments) {
                comment.getCommenter().setComments(new ArrayList<>());
                comment.getCommenter().setPosts(new ArrayList<>());
            }

            return ResponseEntity.ok(comments);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("commentId") Long commentId) {
        try {
            commentService.deleteComment(commentId);
            return ResponseEntity.ok(String.format("CommentID: %s deleted successfully", commentId));
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}