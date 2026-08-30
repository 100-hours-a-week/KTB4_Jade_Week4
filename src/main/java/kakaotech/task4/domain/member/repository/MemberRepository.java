package kakaotech.task4.domain.member.repository;

import kakaotech.task4.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

    @Query("""
            select m from Member m
            where m.deletedAt is null
              and m.memberId = (select min(active.memberId) from Member active where active.deletedAt is null)
            """)
    Optional<Member> findFirstActiveMember();

    @Query("""
            select count(m) > 0 from Member m
            where m.nickname = :nickname
              and m.deletedAt is null
            """)
    boolean existsByNickname(@Param("nickname") String nickname);

    @Query("""
            select m from Member m
            where m.email = :email
              and m.deletedAt is null
            """)
    Optional<Member> findByEmail(@Param("email") String email);

    @Query("""
            select m from Member m
            where m.memberUuid = :memberUuid
              and m.deletedAt is null
            """)
    Optional<Member> findByMemberUuid(@Param("memberUuid") String memberUuid);
}
