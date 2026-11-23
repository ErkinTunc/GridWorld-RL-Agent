
package org.gridworld;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MainTest {

    @Test
    void mainRunsSuccessfully() {
        assertDoesNotThrow(() -> Main.main(new String[0]));
    }
}
