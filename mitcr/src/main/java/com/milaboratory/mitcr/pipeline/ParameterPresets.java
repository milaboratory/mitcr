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
package com.milaboratory.mitcr.pipeline;

import com.milaboratory.core.segment.Gene;
import com.milaboratory.core.segment.Species;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractorParameters;
import com.milaboratory.mitcr.cdrextraction.Strand;
import com.milaboratory.mitcr.clonegenerator.LQMappingCloneGeneratorParameters;
import com.milaboratory.mitcr.clusterization.CloneClusterizationType;
import com.milaboratory.mitcr.qualitystrategy.IlluminaQualityInterpretationStrategy;
import com.milaboratory.mitcr.vdjmapping.AlignmentDirection;
import com.milaboratory.mitcr.vdjmapping.DSegmentMapperParameters;
import com.milaboratory.mitcr.vdjmapping.VJSegmentMapperParameters;

import java.util.HashMap;
import java.util.Map;

public final class ParameterPresets {
    private ParameterPresets() {
    }

    private static final Parameters flex, jPrimer;

    static {
        flex = new Parameters(Gene.TRB, Species.HomoSapiens,
                new CDR3ExtractorParameters(new VJSegmentMapperParameters(AlignmentDirection.Both, -4, 1, 12, 3),
                        new VJSegmentMapperParameters(AlignmentDirection.Both, -1, 4, 12, 2),
                        new DSegmentMapperParameters(3, true), Strand.Both, true));
        flex.setQualityInterpretationStrategy(new IlluminaQualityInterpretationStrategy((byte) 25));
        flex.setCloneGeneratorParameters(new LQMappingCloneGeneratorParameters());
        flex.setClusterizationType(CloneClusterizationType.OneMismatch, .33f);

        jPrimer = new Parameters(Gene.TRB, Species.HomoSapiens,
                new CDR3ExtractorParameters(new VJSegmentMapperParameters(AlignmentDirection.Both, -4, 1, 12, 3),
                        new VJSegmentMapperParameters(AlignmentDirection.InsideCDR3, -3, 2, 7, 2),
                        new DSegmentMapperParameters(3, true), Strand.Both, true));
        jPrimer.setQualityInterpretationStrategy(new IlluminaQualityInterpretationStrategy((byte) 25));
        jPrimer.setCloneGeneratorParameters(new LQMappingCloneGeneratorParameters());
        jPrimer.setClusterizationType(CloneClusterizationType.OneMismatch, .33f);
    }

    private static final Map<String, Parameters> map;

    static {
        map = new HashMap<>();
        map.put("flex", flex);
        map.put("jprimer", jPrimer);
    }

    public static Parameters getFlex() {
        return flex.clone();
    }

    public static Parameters getJPrimer() {
        return jPrimer.clone();
    }

    public static Parameters getByName(String name) {
        Parameters params = map.get(name.toLowerCase());
        if (params == null)
            return null;
        return params.clone();
    }
}
