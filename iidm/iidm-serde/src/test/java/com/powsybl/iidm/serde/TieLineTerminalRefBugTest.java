/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.iidm.serde;

import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import com.powsybl.iidm.serde.anonymizer.Anonymizer;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.powsybl.iidm.serde.IidmSerDeConstants.CURRENT_IIDM_VERSION;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Geoffroy Jamgotchian {@literal <geoffroy.jamgotchian at rte-france.com>}
 */
class TieLineTerminalRefBugTest extends AbstractIidmSerDeTest {

    @Test
    void test() throws IOException {
        assertDoesNotThrow(() -> NetworkSerDe.read(getClass().getResourceAsStream(getVersionedNetworkPath("tieLineTerminalRefBug.xml", CURRENT_IIDM_VERSION))));
    }

    @Test
    void importNetworkWithTiLineAndAnonymizeInNewerVersionShouldSucceed() {
        Network network = EurostagTutorialExample1Factory.createWithTieLinesAndAreas();
        testForAllVersionsSince(IidmVersion.V_1_16, version -> {
            ExportOptions exportOptions = new ExportOptions().setVersion(version.toString(".")).setAnonymized(true);
            // Export (with Anonymize option)
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Anonymizer anonymizer = NetworkSerDe.write(network, exportOptions, os);
            // Import (with Anonymize)
            //Before
//            PowsyblException e = assertThrows(PowsyblException.class, () -> NetworkSerDe.read(new ByteArrayInputStream(os.toByteArray()), new ImportOptions(), anonymizer));
//            assertEquals("AC tie Line 'NHV1_NHV2_1': J and/or Q are not boundary lines in the network", e.getMessage());
            //After fix
            assertDoesNotThrow(() -> NetworkSerDe.read(new ByteArrayInputStream(os.toByteArray()), new ImportOptions(), anonymizer));
        });
    }

    @Test
    void importNetworkWithTiLineAndAnonymizeInOlderVersionShouldSucceed() {
        Network network = EurostagTutorialExample1Factory.createWithTieLinesAndAreas();
        testForAllVersionsBetween(IidmVersion.V_1_10, IidmVersion.V_1_15, version -> {
            ExportOptions exportOptions = new ExportOptions().setVersion(version.toString(".")).setAnonymized(true);
            // Export (with Anonymize option)
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Anonymizer anonymizer = NetworkSerDe.write(network, exportOptions, os);
            // Import (with Anonymize)
            //Before
//            PowsyblException e = assertThrows(PowsyblException.class, () -> NetworkSerDe.read(new ByteArrayInputStream(os.toByteArray()), new ImportOptions(), anonymizer));
//            assertEquals("AC tie Line 'NHV1_NHV2_1': J and/or Q are not boundary lines in the network", e.getMessage());
            // After fix
            assertDoesNotThrow(() -> NetworkSerDe.read(new ByteArrayInputStream(os.toByteArray()), new ImportOptions(), anonymizer));
        });
    }
}
