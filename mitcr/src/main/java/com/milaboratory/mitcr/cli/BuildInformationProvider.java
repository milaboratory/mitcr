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
package com.milaboratory.mitcr.cli;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class BuildInformationProvider {
    public static BuildInformation get() {
        try {
            Enumeration<URL> resources = ClassLoader.getSystemResources("META-INF/MANIFEST.MF");

            while (resources.hasMoreElements()) {
                try {
                    Manifest manifest = new Manifest(resources.nextElement().openStream());
                    Attributes attributes = manifest.getMainAttributes();
                    if (attributes.getValue("Implementation-Title") == null ||
                            !manifest.getMainAttributes().getValue("Implementation-Title").trim().equalsIgnoreCase("MiTCR"))
                        continue;

                    return new BuildInformation(manifest.getMainAttributes().getValue("SCM-Changeset"),
                            manifest.getMainAttributes().getValue("SCM-Date"),
                            manifest.getMainAttributes().getValue("SCM-Branch"),
                            manifest.getMainAttributes().getValue("Implementation-Version"),
                            manifest.getMainAttributes().getValue("Build-Jdk"),
                            manifest.getMainAttributes().getValue("Built-By"),
                            manifest.getMainAttributes().getValue("Build-Date"));
                } catch (IOException E) {
                    System.out.println("SS");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //System.out.println("SS1");
            return null;
        }
        //System.out.println("SS2");
        return null;
    }

    public static String getVersion() {
        BuildInformation buildInformation = get();
        if (buildInformation == null)
            return null;
        return buildInformation.version;
    }
}
