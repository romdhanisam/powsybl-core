/**
 * Copyright (c) 2021, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.cgmes.extensions;

import com.google.auto.service.AutoService;
import com.powsybl.commons.extensions.AbstractExtensionSerDe;
import com.powsybl.commons.extensions.ExtensionSerDe;
import com.powsybl.commons.io.DeserializerContext;
import com.powsybl.commons.io.SerializerContext;
import com.powsybl.iidm.network.DanglingLine;
import com.powsybl.iidm.serde.IidmVersion;
import com.powsybl.iidm.serde.NetworkDeserializerContext;
import com.powsybl.iidm.serde.NetworkSerializerContext;

import static com.powsybl.iidm.serde.util.IidmSerDeUtil.fromMinimumVersionOrElse;

/**
 * @author Miora Ralambotiana {@literal <miora.ralambotiana at rte-france.com>}
 */
@AutoService(ExtensionSerDe.class)
public class CgmesDanglingLineBoundaryNodeSerDe extends AbstractExtensionSerDe<DanglingLine, CgmesDanglingLineBoundaryNode> {

    public CgmesDanglingLineBoundaryNodeSerDe() {
        super("cgmesDanglingLineBoundaryNode", "network", CgmesDanglingLineBoundaryNode.class,
                "cgmesDanglingLineBoundaryNode.xsd",
                "http://www.powsybl.org/schema/iidm/ext/cgmes_dangling_line_boundary_node/1_0", "cdlbn");
    }

    @Override
    public void write(CgmesDanglingLineBoundaryNode extension, SerializerContext context) {
        NetworkSerializerContext networkContext = (NetworkSerializerContext) context;
        networkContext.getWriter().writeBooleanAttribute("isHvdc", extension.isHvdc());
        String lineEnergyIdentificationCodeEic = extension.getLineEnergyIdentificationCodeEic().orElse(null);
        String lineEnergyIdentificationCodeEicToWrite = fromMinimumVersionOrElse(IidmVersion.V_1_16, networkContext,
                () -> networkContext.getAnonymizer().anonymizeString(lineEnergyIdentificationCodeEic),
                () -> lineEnergyIdentificationCodeEic
        );
        networkContext.getWriter().writeStringAttribute("lineEnergyIdentificationCodeEic", lineEnergyIdentificationCodeEicToWrite);
    }

    @Override
    public CgmesDanglingLineBoundaryNode read(DanglingLine extendable, DeserializerContext context) {
        NetworkDeserializerContext networkContext = (NetworkDeserializerContext) context;
        boolean isHvdc = networkContext.getReader().readBooleanAttribute("isHvdc");
        String lineEnergyIdentificationCodeEic = networkContext.getReader().readStringAttribute("lineEnergyIdentificationCodeEic");
        String lineEnergyIdentificationCodeEicToRead = fromMinimumVersionOrElse(IidmVersion.V_1_16, networkContext,
                () -> networkContext.getAnonymizer().deanonymizeString(lineEnergyIdentificationCodeEic),
                () -> lineEnergyIdentificationCodeEic
        );
        networkContext.getReader().readEndNode();
        extendable.newExtension(CgmesDanglingLineBoundaryNodeAdder.class).setHvdc(isHvdc).setLineEnergyIdentificationCodeEic(lineEnergyIdentificationCodeEicToRead).add();
        return extendable.getExtension(CgmesDanglingLineBoundaryNode.class);
    }
}
