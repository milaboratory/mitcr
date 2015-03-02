package com.milaboratory.core.sequencing.io.fastq;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PairedFiles {
    final String commonName;
    File[] files = new File[2];

    private PairedFiles(String commonName) {
        this.commonName = commonName;
    }

    public PairedFiles(String commonName, File fileR1, File fileR2) {
        this.commonName = commonName;
        this.files[0] = fileR1;
        this.files[1] = fileR2;
    }

    public boolean isPairedReads() {
        return files[1] != null;
    }

    public File getR1File() {
        return files[0];
    }

    public File getR2File() {
        return files[1];
    }

    public String getR1Address() {
        return files[0].getAbsolutePath();
    }

    public String getR2Address() {
        return files[1].getAbsolutePath();
    }

    public String getCommonName() {
        return commonName;
    }

    static final Pattern preFilter = Pattern.compile("\\.fastq", Pattern.CASE_INSENSITIVE);
    static final Pattern[] rPatterns = {Pattern.compile("(.*)[_\\.]R1[_\\.](.*)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(.*)[_\\.]R2[_\\.](.*)", Pattern.CASE_INSENSITIVE)};

    public static List<PairedFiles> getFastqFilesInDirectory(File directory) {
        if (!directory.isDirectory())
            throw new IllegalArgumentException();

        Map<String, PairedFiles> map = new HashMap<>();

        int i, hit;
        Matcher matcher = null, t;
        String commonName;
        PairedFiles pair;

        for (File file : directory.listFiles()) {
            if (!preFilter.matcher(file.getName()).find())
                continue;

            hit = -1;
            for (i = 0; i < 2; ++i) {
                if ((t = rPatterns[i].matcher(file.getName())).matches())
                    if (hit == -1) {
                        hit = i;
                        matcher = t;
                    } else
                        continue;
            }

            if (hit == -1)
                commonName = file.getName();
            else
                commonName = matcher.group(1) + '_' + matcher.group(2);

            if ((pair = map.get(commonName)) == null)
                map.put(commonName, pair = new PairedFiles(commonName));

            pair.files[hit == -1 ? 0 : hit] = file;
        }

        return new ArrayList<>(map.values());
    }
}
