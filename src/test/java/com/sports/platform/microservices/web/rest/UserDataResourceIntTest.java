package com.sports.platform.microservices.web.rest;

import com.sports.platform.microservices.SportsPlatformMicroservicesApp;

import com.sports.platform.microservices.domain.UserData;
import com.sports.platform.microservices.repository.UserDataRepository;
import com.sports.platform.microservices.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;


import static com.sports.platform.microservices.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the UserDataResource REST controller.
 *
 * @see UserDataResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SportsPlatformMicroservicesApp.class)
public class UserDataResourceIntTest {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final Instant DEFAULT_REGISTRATION_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REGISTRATION_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_USER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_USER_NAME = "BBBBBBBBBB";

    @Autowired
    private UserDataRepository userDataRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restUserDataMockMvc;

    private UserData userData;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final UserDataResource userDataResource = new UserDataResource(userDataRepository);
        this.restUserDataMockMvc = MockMvcBuilders.standaloneSetup(userDataResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserData createEntity() {
        UserData userData = new UserData()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .email(DEFAULT_EMAIL)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .registrationDate(DEFAULT_REGISTRATION_DATE)
            .userName(DEFAULT_USER_NAME);
        return userData;
    }

    @Before
    public void initTest() {
        userDataRepository.deleteAll();
        userData = createEntity();
    }

    @Test
    public void createUserData() throws Exception {
        int databaseSizeBeforeCreate = userDataRepository.findAll().size();

        // Create the UserData
        restUserDataMockMvc.perform(post("/api/user-data")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userData)))
            .andExpect(status().isCreated());

        // Validate the UserData in the database
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeCreate + 1);
        UserData testUserData = userDataList.get(userDataList.size() - 1);
        assertThat(testUserData.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testUserData.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testUserData.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testUserData.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testUserData.getRegistrationDate()).isEqualTo(DEFAULT_REGISTRATION_DATE);
        assertThat(testUserData.getUserName()).isEqualTo(DEFAULT_USER_NAME);
    }

    @Test
    public void createUserDataWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = userDataRepository.findAll().size();

        // Create the UserData with an existing ID
        userData.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserDataMockMvc.perform(post("/api/user-data")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userData)))
            .andExpect(status().isBadRequest());

        // Validate the UserData in the database
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void getAllUserData() throws Exception {
        // Initialize the database
        userDataRepository.save(userData);

        // Get all the userDataList
        restUserDataMockMvc.perform(get("/api/user-data?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userData.getId())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME.toString())))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER.toString())))
            .andExpect(jsonPath("$.[*].registrationDate").value(hasItem(DEFAULT_REGISTRATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].userName").value(hasItem(DEFAULT_USER_NAME.toString())));
    }
    
    @Test
    public void getUserData() throws Exception {
        // Initialize the database
        userDataRepository.save(userData);

        // Get the userData
        restUserDataMockMvc.perform(get("/api/user-data/{id}", userData.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(userData.getId()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME.toString()))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER.toString()))
            .andExpect(jsonPath("$.registrationDate").value(DEFAULT_REGISTRATION_DATE.toString()))
            .andExpect(jsonPath("$.userName").value(DEFAULT_USER_NAME.toString()));
    }

    @Test
    public void getNonExistingUserData() throws Exception {
        // Get the userData
        restUserDataMockMvc.perform(get("/api/user-data/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateUserData() throws Exception {
        // Initialize the database
        userDataRepository.save(userData);

        int databaseSizeBeforeUpdate = userDataRepository.findAll().size();

        // Update the userData
        UserData updatedUserData = userDataRepository.findById(userData.getId()).get();
        updatedUserData
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .registrationDate(UPDATED_REGISTRATION_DATE)
            .userName(UPDATED_USER_NAME);

        restUserDataMockMvc.perform(put("/api/user-data")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedUserData)))
            .andExpect(status().isOk());

        // Validate the UserData in the database
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeUpdate);
        UserData testUserData = userDataList.get(userDataList.size() - 1);
        assertThat(testUserData.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testUserData.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testUserData.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testUserData.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testUserData.getRegistrationDate()).isEqualTo(UPDATED_REGISTRATION_DATE);
        assertThat(testUserData.getUserName()).isEqualTo(UPDATED_USER_NAME);
    }

    @Test
    public void updateNonExistingUserData() throws Exception {
        int databaseSizeBeforeUpdate = userDataRepository.findAll().size();

        // Create the UserData

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserDataMockMvc.perform(put("/api/user-data")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userData)))
            .andExpect(status().isBadRequest());

        // Validate the UserData in the database
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteUserData() throws Exception {
        // Initialize the database
        userDataRepository.save(userData);

        int databaseSizeBeforeDelete = userDataRepository.findAll().size();

        // Get the userData
        restUserDataMockMvc.perform(delete("/api/user-data/{id}", userData.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<UserData> userDataList = userDataRepository.findAll();
        assertThat(userDataList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserData.class);
        UserData userData1 = new UserData();
        userData1.setId("id1");
        UserData userData2 = new UserData();
        userData2.setId(userData1.getId());
        assertThat(userData1).isEqualTo(userData2);
        userData2.setId("id2");
        assertThat(userData1).isNotEqualTo(userData2);
        userData1.setId(null);
        assertThat(userData1).isNotEqualTo(userData2);
    }
}
