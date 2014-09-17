package pl.cierniewski.friendlygallery.facebookapi;

import org.junit.Test;

import javax.inject.Inject;

import pl.cierniewski.friendlygallery.facebookapi.model.UsersRegisterResponse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;


public class GistrUsersApiTest extends BaseGistrApiTest {

    @Inject
    FacebookApi mFacebookApi;

    @Test
    public void testRegister() throws Exception {
        final UsersRegisterResponse registerResponse = mFacebookApi.users()
                .register().execute();
        assertThat(registerResponse, is(notNullValue()));
        assertThat(registerResponse.accessToken, is(notNullValue()));
    }
}


