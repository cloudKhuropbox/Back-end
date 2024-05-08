package com.khu.cloudcomputing.khuropbox.auth.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames="username")})
public class UserEntity {
    @Id
    @UuidGenerator
    private String id;

    @Column(nullable = false)
    private String username;

    private String password;
    private String role;
    private String authProvider; // 이후 OAuth에서 사용
}
/*
여기에서 password가 null이 아닌 이유는
OAuth를 사용해 SSO를 구현하기 위해서는
password가 null이면 안된다
=>
데이터베이스에 password를 입력하도록 entity에서 규제하는 것이 아니라
회원 가입을 구현하는 controllere단에서 password를 반드시 입력하게 한다.
 */