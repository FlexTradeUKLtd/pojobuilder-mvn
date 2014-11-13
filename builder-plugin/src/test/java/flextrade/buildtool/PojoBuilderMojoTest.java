package flextrade.buildtool;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PojoBuilderMojoTest {

    PojoBuilderMojo pojoBuilderMojo;

    @Before
    public void setupPojoBuilderMojo() {
        pojoBuilderMojo = new PojoBuilderMojo();
    }

    @Test
    public void name() {
        assertTrue(true);

    }
}