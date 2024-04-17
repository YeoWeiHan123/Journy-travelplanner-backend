package sg.edu.nus.journybackend.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import sg.edu.nus.journybackend.auth.AuthenticationRequest;
import sg.edu.nus.journybackend.auth.AuthenticationResponse;
import sg.edu.nus.journybackend.auth.RegisterRequest;
import sg.edu.nus.journybackend.entity.Member;
import sg.edu.nus.journybackend.exception.ResourceNotFoundException;
import sg.edu.nus.journybackend.service.MemberService;

@CrossOrigin("*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request
    ) {
        try{
            AuthenticationResponse response = memberService.register(request);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        try{
            AuthenticationResponse response = memberService.authenticate(request);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/follow/{targetMemberId}")
    public ResponseEntity<?> follow(
            @PathVariable Long targetMemberId
    ) {
        try{
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = userDetails.getUsername();
            Long memberId = memberService.findByUsername(username).getMemberId();

            memberService.followByMemberId(memberId, targetMemberId);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/my-profile")
    public ResponseEntity<?> getMyProfile() {
        try{
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = userDetails.getUsername();
            Member member = memberService.findByUsername(username);

            processMemberForResponse(member);

            return new ResponseEntity<>(member, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(
            @RequestBody Member updatedMember
    ) {
        try{
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = userDetails.getUsername();
            Long memberId = memberService.findByUsername(username).getMemberId();

            Member persistedMember = memberService.updateMember(memberId, updatedMember);

            processMemberForResponse(persistedMember);

            return new ResponseEntity<>(persistedMember, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    private void processMemberForResponse(Member member) {
        for (Member follower : member.getFollowersMembers()) {
            removeRecursion(follower);
        }
        for (Member following : member.getFollowingMembers()) {
            removeRecursion(following);
        }
    }

    private void removeRecursion(Member member) {
        member.setFollowersMembers(null);
        member.setFollowingMembers(null);
        member.setPosts(null);
        member.setLikedPosts(null);
        member.setComments(null);
    }
}
