
package org.gridworld;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MainTest {

    @Test
    @DisplayName ("Main runs successfully")
    void mainRunsSuccessfully() {
        assertDoesNotThrow(() -> Main.main(new String[0]));
    }
}
