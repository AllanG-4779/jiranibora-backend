package org.jiranibora.com.auth;

import org.jiranibora.com.models.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthenticationRepository extends JpaRepository<Member, String> {
    // load username by id
    @Query("SELECT u FROM Member  u where u.memberId=?1 OR u.prevRef.emailAddress=?1 OR u.prevRef.nationalId=?1")
    Member findMemberByMemberId(String memberId);
    Member findByRoleContaining(String role);

    List<Member> findAllByIsActive( Boolean active );

}
