package me.iiaii.demoinflearnrestapi.accounts;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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
        String username = "random@email.com";
        expectedException.expect(UsernameNotFoundException.class);
        expectedException.expectMessage(Matchers.containsString(username));

        // when
        // then
         UserDetails userDetails = accountService.loadUserByUsername(username);


    }
}