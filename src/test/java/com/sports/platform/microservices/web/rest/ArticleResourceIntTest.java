package com.sports.platform.microservices.web.rest;

import com.sports.platform.microservices.SportsPlatformMicroservicesApp;

import com.sports.platform.microservices.domain.Article;
import com.sports.platform.microservices.repository.ArticleRepository;
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
 * Test class for the ArticleResource REST controller.
 *
 * @see ArticleResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SportsPlatformMicroservicesApp.class)
public class ArticleResourceIntTest {

    private static final String DEFAULT_ARTICLE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ARTICLE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ARTICLE_SUBJECT = "AAAAAAAAAA";
    private static final String UPDATED_ARTICLE_SUBJECT = "BBBBBBBBBB";

    private static final String DEFAULT_CATAGOERY_REF_ID = "AAAAAAAAAA";
    private static final String UPDATED_CATAGOERY_REF_ID = "BBBBBBBBBB";

    private static final String DEFAULT_ARTICLE_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_ARTICLE_TEXT = "BBBBBBBBBB";

    private static final String DEFAULT_ARTICLE_AUTHOR_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ARTICLE_AUTHOR_FULL_NAME = "BBBBBBBBBB";

    private static final Instant DEFAULT_ARTICLE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ARTICLE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_USER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_USER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_USER_REF_ID = "AAAAAAAAAA";
    private static final String UPDATED_USER_REF_ID = "BBBBBBBBBB";

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restArticleMockMvc;

    private Article article;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ArticleResource articleResource = new ArticleResource(articleRepository);
        this.restArticleMockMvc = MockMvcBuilders.standaloneSetup(articleResource)
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
    public static Article createEntity() {
        Article article = new Article()
            .articleName(DEFAULT_ARTICLE_NAME)
            .articleSubject(DEFAULT_ARTICLE_SUBJECT)
            .catagoeryRefId(DEFAULT_CATAGOERY_REF_ID)
            .articleText(DEFAULT_ARTICLE_TEXT)
            .articleAuthorFullName(DEFAULT_ARTICLE_AUTHOR_FULL_NAME)
            .articleDate(DEFAULT_ARTICLE_DATE)
            .userName(DEFAULT_USER_NAME)
            .userRefId(DEFAULT_USER_REF_ID);
        return article;
    }

    @Before
    public void initTest() {
        articleRepository.deleteAll();
        article = createEntity();
    }

    @Test
    public void createArticle() throws Exception {
        int databaseSizeBeforeCreate = articleRepository.findAll().size();

        // Create the Article
        restArticleMockMvc.perform(post("/api/articles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(article)))
            .andExpect(status().isCreated());

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll();
        assertThat(articleList).hasSize(databaseSizeBeforeCreate + 1);
        Article testArticle = articleList.get(articleList.size() - 1);
        assertThat(testArticle.getArticleName()).isEqualTo(DEFAULT_ARTICLE_NAME);
        assertThat(testArticle.getArticleSubject()).isEqualTo(DEFAULT_ARTICLE_SUBJECT);
        assertThat(testArticle.getCatagoeryRefId()).isEqualTo(DEFAULT_CATAGOERY_REF_ID);
        assertThat(testArticle.getArticleText()).isEqualTo(DEFAULT_ARTICLE_TEXT);
        assertThat(testArticle.getArticleAuthorFullName()).isEqualTo(DEFAULT_ARTICLE_AUTHOR_FULL_NAME);
        assertThat(testArticle.getArticleDate()).isEqualTo(DEFAULT_ARTICLE_DATE);
        assertThat(testArticle.getUserName()).isEqualTo(DEFAULT_USER_NAME);
        assertThat(testArticle.getUserRefId()).isEqualTo(DEFAULT_USER_REF_ID);
    }

    @Test
    public void createArticleWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = articleRepository.findAll().size();

        // Create the Article with an existing ID
        article.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restArticleMockMvc.perform(post("/api/articles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(article)))
            .andExpect(status().isBadRequest());

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll();
        assertThat(articleList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void getAllArticles() throws Exception {
        // Initialize the database
        articleRepository.save(article);

        // Get all the articleList
        restArticleMockMvc.perform(get("/api/articles?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(article.getId())))
            .andExpect(jsonPath("$.[*].articleName").value(hasItem(DEFAULT_ARTICLE_NAME.toString())))
            .andExpect(jsonPath("$.[*].articleSubject").value(hasItem(DEFAULT_ARTICLE_SUBJECT.toString())))
            .andExpect(jsonPath("$.[*].catagoeryRefId").value(hasItem(DEFAULT_CATAGOERY_REF_ID.toString())))
            .andExpect(jsonPath("$.[*].articleText").value(hasItem(DEFAULT_ARTICLE_TEXT.toString())))
            .andExpect(jsonPath("$.[*].articleAuthorFullName").value(hasItem(DEFAULT_ARTICLE_AUTHOR_FULL_NAME.toString())))
            .andExpect(jsonPath("$.[*].articleDate").value(hasItem(DEFAULT_ARTICLE_DATE.toString())))
            .andExpect(jsonPath("$.[*].userName").value(hasItem(DEFAULT_USER_NAME.toString())))
            .andExpect(jsonPath("$.[*].userRefId").value(hasItem(DEFAULT_USER_REF_ID.toString())));
    }
    
    @Test
    public void getArticle() throws Exception {
        // Initialize the database
        articleRepository.save(article);

        // Get the article
        restArticleMockMvc.perform(get("/api/articles/{id}", article.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(article.getId()))
            .andExpect(jsonPath("$.articleName").value(DEFAULT_ARTICLE_NAME.toString()))
            .andExpect(jsonPath("$.articleSubject").value(DEFAULT_ARTICLE_SUBJECT.toString()))
            .andExpect(jsonPath("$.catagoeryRefId").value(DEFAULT_CATAGOERY_REF_ID.toString()))
            .andExpect(jsonPath("$.articleText").value(DEFAULT_ARTICLE_TEXT.toString()))
            .andExpect(jsonPath("$.articleAuthorFullName").value(DEFAULT_ARTICLE_AUTHOR_FULL_NAME.toString()))
            .andExpect(jsonPath("$.articleDate").value(DEFAULT_ARTICLE_DATE.toString()))
            .andExpect(jsonPath("$.userName").value(DEFAULT_USER_NAME.toString()))
            .andExpect(jsonPath("$.userRefId").value(DEFAULT_USER_REF_ID.toString()));
    }

    @Test
    public void getNonExistingArticle() throws Exception {
        // Get the article
        restArticleMockMvc.perform(get("/api/articles/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateArticle() throws Exception {
        // Initialize the database
        articleRepository.save(article);

        int databaseSizeBeforeUpdate = articleRepository.findAll().size();

        // Update the article
        Article updatedArticle = articleRepository.findById(article.getId()).get();
        updatedArticle
            .articleName(UPDATED_ARTICLE_NAME)
            .articleSubject(UPDATED_ARTICLE_SUBJECT)
            .catagoeryRefId(UPDATED_CATAGOERY_REF_ID)
            .articleText(UPDATED_ARTICLE_TEXT)
            .articleAuthorFullName(UPDATED_ARTICLE_AUTHOR_FULL_NAME)
            .articleDate(UPDATED_ARTICLE_DATE)
            .userName(UPDATED_USER_NAME)
            .userRefId(UPDATED_USER_REF_ID);

        restArticleMockMvc.perform(put("/api/articles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedArticle)))
            .andExpect(status().isOk());

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll();
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate);
        Article testArticle = articleList.get(articleList.size() - 1);
        assertThat(testArticle.getArticleName()).isEqualTo(UPDATED_ARTICLE_NAME);
        assertThat(testArticle.getArticleSubject()).isEqualTo(UPDATED_ARTICLE_SUBJECT);
        assertThat(testArticle.getCatagoeryRefId()).isEqualTo(UPDATED_CATAGOERY_REF_ID);
        assertThat(testArticle.getArticleText()).isEqualTo(UPDATED_ARTICLE_TEXT);
        assertThat(testArticle.getArticleAuthorFullName()).isEqualTo(UPDATED_ARTICLE_AUTHOR_FULL_NAME);
        assertThat(testArticle.getArticleDate()).isEqualTo(UPDATED_ARTICLE_DATE);
        assertThat(testArticle.getUserName()).isEqualTo(UPDATED_USER_NAME);
        assertThat(testArticle.getUserRefId()).isEqualTo(UPDATED_USER_REF_ID);
    }

    @Test
    public void updateNonExistingArticle() throws Exception {
        int databaseSizeBeforeUpdate = articleRepository.findAll().size();

        // Create the Article

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restArticleMockMvc.perform(put("/api/articles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(article)))
            .andExpect(status().isBadRequest());

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll();
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteArticle() throws Exception {
        // Initialize the database
        articleRepository.save(article);

        int databaseSizeBeforeDelete = articleRepository.findAll().size();

        // Get the article
        restArticleMockMvc.perform(delete("/api/articles/{id}", article.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Article> articleList = articleRepository.findAll();
        assertThat(articleList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Article.class);
        Article article1 = new Article();
        article1.setId("id1");
        Article article2 = new Article();
        article2.setId(article1.getId());
        assertThat(article1).isEqualTo(article2);
        article2.setId("id2");
        assertThat(article1).isNotEqualTo(article2);
        article1.setId(null);
        assertThat(article1).isNotEqualTo(article2);
    }
}
