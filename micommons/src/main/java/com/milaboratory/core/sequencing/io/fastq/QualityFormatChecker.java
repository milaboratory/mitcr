package com.milaboratory.core.sequencing.io.fastq;

import com.milaboratory.core.sequence.quality.QualityFormat;

import java.io.BufferedReader;
import java.io.IOException;

import static com.milaboratory.core.sequence.quality.QualityFormat.Phred33;
import static com.milaboratory.core.sequence.quality.QualityFormat.Phred64;

/**
 * This class contains methods to infer quality string format from FASTQ data.
 */
public final class QualityFormatChecker {
    public static QualityFormat guessFormat(BufferedReader reader, int maxBytes) throws IOException {
        String line;
        int k, chr;
        boolean signal33, signal64;
        int maxReadSize = 0, currentReadSize;
        do {
            currentReadSize = 0;

            for (k = 0; k < 3; ++k)
                if ((line = reader.readLine()) != null)
                    currentReadSize += line.length() + 2;
                else
                    return null;

            line = reader.readLine();
            if (line == null)
                return null;
            currentReadSize += line.length() + 2;

            signal33 = false;
            signal64 = false;

            for (k = line.length() - 1; k >= 0; --k) {
                chr = (int) line.charAt(k);
                signal33 |= (chr - 64) < Phred64.getMinValue();
                signal64 |= (chr - 33) > Phred33.getMaxValue();
            }

            //The file has bad format.
            //If any of formats is applicable file contains out of range values in any way.
            if (signal33 && signal64)
                return null;

            if (signal33)
                return Phred33;
            if (signal64)
                return Phred64;

            maxBytes -= currentReadSize;

            if (maxReadSize < currentReadSize)
                maxReadSize = currentReadSize;

        } while (maxBytes - maxReadSize > 0); //Weak condition if file contains reads with different read lengths

        return null;
    }
}