package com.perficient.etm.utils;

import org.junit.Before;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class SpringMockTest extends Mockito {

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }
}
