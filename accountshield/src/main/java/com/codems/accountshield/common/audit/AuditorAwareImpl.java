package com.codems.accountshield.common.audit;

import com.codems.accountshield.common.constants.ApplicationConstants;
import com.codems.accountshield.common.util.ApplicationUtility;
import com.codems.accountshield.domain.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorAwareImpl")
@Slf4j
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        var loggedInUser = ApplicationUtility.getLoggedInUser();
        return loggedInUser.map(User::getName).or(() -> Optional.of(ApplicationConstants.SYSTEM));
    }
}
