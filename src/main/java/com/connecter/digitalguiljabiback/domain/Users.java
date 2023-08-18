package com.connecter.digitalguiljabiback.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class Users implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;

    @NotNull
    @Column(unique = true)
    private String uid;

    @NotNull
    private String passwords;

    private OauthType oauthType;

    @NotNull
    @CreationTimestamp
    @Column(name = "create_at")
    private LocalDateTime createAt = LocalDateTime.now();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "roles")
    private UserRole role = UserRole.USER;

    //카카오 로그인이 완전히 완료된 후에 nickname을 지정할 것 같아서 일단 nullable
    @Column(unique = true)
    private String nickname;

    @Lob
    @Column(name = "profile_url", length = 99999)
    private String profileUrl = null;

    @Lob
    @Column(length = 999999999)
    private String introduction;

    private String id1365;

    private String idvms;

    private boolean isAccountNonLocked = true;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks = new ArrayList<>();

    public void updateNickname(String nickname) { this.nickname = nickname; }
    public void updateProfileImg(String profileUrl) { this.profileUrl = profileUrl; }

    public static Users makeUsers(String uidString, String passwords, OauthType oauthType) {
        Users user = new Users();
        user.uid = uidString;
        user.oauthType = oauthType;
        user.passwords = passwords;
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return passwords;
    }

    @Override
    public String getUsername() {
        return uid;
    }

    @Override //만료여부
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override //잠긴 사용자
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override //자격증명 만료
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override //활성화, 비활성화 여부
    public boolean isEnabled() { //활성화상태인가? - 휴면상태 관리
        return true;
    }

    public void updateInfo(String nickname, String introduction, String idvms, String id1365) {
        this.nickname = (nickname != null)? nickname : this.nickname;
        this.introduction = (introduction != null)? introduction : this.introduction;
        this.idvms = (idvms != null)? idvms : this.idvms;
        this.id1365 = (id1365 != null)? id1365 : this.id1365;
    }

    public void lock() {
        this.isAccountNonLocked = false;
    }

    public void unlock() {
        this.isAccountNonLocked = true;
    }

    public String convertDate(LocalDateTime createAt) {
        String convertedDate = createAt.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm:ss"));
        return convertedDate;
    }
}
