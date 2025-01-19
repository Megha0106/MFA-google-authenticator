package com.example.mfa_google_authenticator.repositories;

import com.example.mfa_google_authenticator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository <User,Long>{

    @Query("SELECT u from User u WHERE u.userName = :userName")
    User findUser(@Param("userName")String userName);

    User findByUserName(String userName);
//    User findById(Long id);


}
