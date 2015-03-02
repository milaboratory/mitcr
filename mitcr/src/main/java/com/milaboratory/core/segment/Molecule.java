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
package com.milaboratory.core.segment;

/**
 * Immunological molecules. T and B cell receptors.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public enum Molecule {
    TCRab(new Chain[]{Chain.Alpha, Chain.Beta}, new Gene[]{Gene.TRA, Gene.TRB}, (byte) 0),
    TCRgd(new Chain[]{Chain.Gamma, Chain.Delta}, new Gene[]{Gene.TRG, Gene.TRD}, (byte) 1);
    private final Chain[] chains;
    private final Gene[] genes;
    private final byte id;

    private Molecule(Chain[] chains, Gene[] genes, byte id) {
        for (int i = 0; i < chains.length; ++i)
            if (chains[i].id() != i)
                throw new RuntimeException();
        this.chains = chains;
        this.id = id;
        this.genes = genes;
    }

    public static Molecule getMolecule(byte id) {
        for (Molecule m : values())
            if (m.id == id)
                return m;
        return null;
    }

    public Chain[] getChains() {
        return chains;
    }

    public Gene[] getGenes() {
        return genes;
    }

    public Gene getGeneByChain(Chain chain) {
        return genes[chain.id()];
    }

    public byte id() {
        return id;
    }
}
