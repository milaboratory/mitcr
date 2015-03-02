/*
 * MiTCR <http://milaboratory.com>
 *
 * Copyright (c) 2010-2013:
 *     Bolotin Dmitriy     <bolotin.dmitriy@gmail.com>
 *     Chudakov Dmitriy    <chudakovdm@mail.ru>
 *
 * MiTCR is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.milaboratory.mitcr.qualitystrategy;

import org.jdom.Element;

public final class QualityInterpretationStrategyDeserializer {
    private QualityInterpretationStrategyDeserializer() {
    }

    public static QualityInterpretationStrategy fromXML(Element e) {
        Element c = (Element) e.getChildren().get(0);
        if (c.getName() == "dummy")
            return new DummyQualityInterpretationStrategy();
        else if (c.getName() == "illumina")
            return new IlluminaQualityInterpretationStrategy(Byte.decode(c.getTextTrim()));
        throw new RuntimeException("Unsupported quality interpretation type.");
    }
}
