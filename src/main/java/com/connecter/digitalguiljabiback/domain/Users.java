package com.connecter.digitalguiljabiback.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Builder
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
    @CreationTimestamp
    @Column(name = "create_datetime")
    @Builder.Default
    private LocalDate createDatetime = LocalDate.now();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "roles")
    @Builder.Default
    private UserRole role = UserRole.USER;

    @NotNull
    private String nickname;

    @Builder.Default
    @Lob
    @Column(name = "profile_url", length = 99999)
    private String profileUrl = null;

    @Lob
    @Column(length = 999999999)
    private String introduction;

    @NotNull
    private String id1365;

    @NotNull
    private String idvms;

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() { //활성화상태인가? - 휴면상태 관리
        return true;
    }

}
