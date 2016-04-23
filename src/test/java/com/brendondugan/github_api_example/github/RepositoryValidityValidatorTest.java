package com.brendondugan.github_api_example.github;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;


import static org.junit.Assert.*;


/**
 * Created by brendon on 4/23/2016.
 */
public class RepositoryValidityValidatorTest {
    @Mock
    private GitHubClient client = mock(GitHubClient.class);
    @Mock
    private RepositoryService repositoryService = mock(RepositoryService.class);
    private RepositoryValidityValidator validator;

    @Before
    public void setUp() throws Exception {
        this.validator = new RepositoryValidityValidator("test", "test", "test");
        this.validator.setGitHubClient(client);
        this.validator.setRepositoryService(repositoryService);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getBranches() throws Exception {
        validator.getBranches();
        verify(repositoryService, times(1)).getRepository(anyString(), anyString());
        verify(repositoryService, times(1)).getBranches(any(Repository.class));
    }

}