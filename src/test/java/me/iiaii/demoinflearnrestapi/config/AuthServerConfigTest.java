package me.iiaii.demoinflearnrestapi.config;

import me.iiaii.demoinflearnrestapi.accounts.Account;
import me.iiaii.demoinflearnrestapi.accounts.AccountRole;
import me.iiaii.demoinflearnrestapi.accounts.AccountService;
import me.iiaii.demoinflearnrestapi.common.BaseControllerTest;
import me.iiaii.demoinflearnrestapi.common.TestDescription;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthServerConfigTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;

    @Test
    @TestDescription("인증 토큰을 발급 받는 테스트")
    public void getAuthToken() throws Exception {
        // given
        String username = "iiaii@email.com";
        String password = "iiaii";
        Account iiaii = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        this.accountService.saveAccount(iiaii);

        String clientId = "myApp";
        String clientSecret = "pass";


        // when
        // then
        this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(clientId, clientSecret))
                .param("username", username)
                .param("password", password)
                .param("grant_type", "password"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists());



    }

}