package me.iiaii.demoinflearnrestapi.accounts;

import me.iiaii.demoinflearnrestapi.common.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountServiceTest extends BaseTest {

    @Autowired
    AccountService accountService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void findByUsername() throws Exception {
        // given
        String username = "iiaii@email.com";
        String password = "iiaii";
        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();

        this.accountService.saveAccount(account);

        // when
        UserDetailsService userDetailsService = this.accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // then
        assertThat(this.passwordEncoder.matches(password, userDetails.getPassword())).isTrue();
        
    }

    @Test
    public void findByUsernameFail() throws Exception {
        // given
        // when
        // then
        assertThrows(UsernameNotFoundException.class, () ->
            accountService.loadUserByUsername("random@email.com")
        );
    }
}