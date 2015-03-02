package com.milaboratory;

import cc.redberry.pipe.OutputPort;
import com.milaboratory.core.sequencing.io.fastq.SFastqReader;
import com.milaboratory.core.sequencing.read.SSequencingRead;

import java.io.IOException;

public class MiStatic {
    static OutputPort<SSequencingRead> fastq(String fileName) throws IOException {
        return new SFastqReader(fileName);
    }
}
