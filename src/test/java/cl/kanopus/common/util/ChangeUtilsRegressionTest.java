/*-
 * !--
 * For support and inquiries regarding this library, please contact:
 *   soporte@kanopus.cl
 *
 * Project website:
 *   https://www.kanopus.cl
 * %%
 * Copyright (C) 2025 Pablo Díaz Saavedra
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * --!
 */
package cl.kanopus.common.util;

import static org.junit.jupiter.api.Assertions.*;

import cl.kanopus.common.change.ChangeAction;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ChangeUtilsRegressionTest {

    static class Holder {
        private List<String> values;

        Holder(List<String> values) {
            this.values = values;
        }
    }

    static class WithJdkType {
        private Optional<String> optional;
        private Thread.State state;

        WithJdkType(Optional<String> optional) {
            this.optional = optional;
            this.state = Thread.State.NEW;
        }
    }

    @Test
    void nullTargetListWithNonNullSourceIsUpdate() {
        assertEquals(ChangeAction.UPDATE, ChangeUtils.checkChange(new Holder(List.of("a")), new Holder(null)));
        assertEquals(ChangeAction.UPDATE, ChangeUtils.checkChange(new Holder(null), new Holder(List.of("a"))));
        assertEquals(ChangeAction.NONE, ChangeUtils.checkChange(new Holder(null), new Holder(new ArrayList<>())));
        assertEquals(ChangeAction.NONE, ChangeUtils.checkChange(new Holder(List.of("a")), new Holder(List.of("a"))));
        assertTrue(ChangeUtils.hasChanges(new Holder(List.of("a")), new Holder(List.of("b"))));
    }

    @Test
    void listsWithNullElementsDoNotFail() {
        List<String> withNull = new ArrayList<>();
        withNull.add(null);
        assertEquals(ChangeAction.NONE, ChangeUtils.checkChange(new Holder(withNull), new Holder(new ArrayList<>(withNull))));
        assertEquals(2, ChangeUtils.checkChangeOnList(withNull, List.of("x")).size());
    }

    @Test
    void inaccessibleJdkTypesFallBackToEquals() {
        assertDoesNotThrow(() -> ChangeUtils.checkChange(new WithJdkType(Optional.of("a")), new WithJdkType(Optional.of("a"))));
        assertEquals(ChangeAction.NONE, ChangeUtils.checkChange(new WithJdkType(Optional.of("a")), new WithJdkType(Optional.of("a"))));
        assertEquals(ChangeAction.UPDATE, ChangeUtils.checkChange(new WithJdkType(Optional.of("a")), new WithJdkType(Optional.of("b"))));
        assertEquals(ChangeAction.UPDATE, ChangeUtils.checkChange(Optional.of(Duration.ZERO), Optional.of(Duration.ofSeconds(1))));
    }
}
