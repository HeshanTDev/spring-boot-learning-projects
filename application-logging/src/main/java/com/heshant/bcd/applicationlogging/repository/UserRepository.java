package com.heshant.bcd.applicationlogging.repository;

import com.heshant.bcd.applicationlogging.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
