package com.codequest.user;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Page<User> findAllBy(Pageable pageable);

    @Query("""
            select count(u) from User u
            where u.xp > :xp
               or (u.xp = :xp and u.name < :name)
               or (u.xp = :xp and u.name = :name and u.id < :userId)
            """)
    long countUsersRankedAhead(@Param("xp") Integer xp, @Param("name") String name, @Param("userId") UUID userId);
}
