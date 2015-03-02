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

import com.milaboratory.mitcr.pipeline.ParameterPresets;
import com.milaboratory.mitcr.pipeline.Parameters;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;

public class ParametersIO {
    public static String exportParametersToString(Parameters parameters) {
        Format format = Format.getCompactFormat();
        return new XMLOutputter(format).outputString(parameters.asXML());
    }

    public static Parameters importParametersFromString(String string) {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(new StringReader(string));
            return Parameters.fromXML(document.getRootElement());
        } catch (JDOMException | IOException ae) {
            throw new RuntimeException(ae);
        }
    }

    public static void exportParameters(Parameters parameters, String fileName) {
        Format format = Format.getPrettyFormat();
        format.setLineSeparator("\n");
        XMLOutputter outputter = new XMLOutputter(format);
        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            Document doc = new Document(parameters.asXML());
            outputter.output(doc, outputStream);
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
    }

    public static Parameters loadParameters(String fileName) {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(fileName);
            return Parameters.fromXML(document.getRootElement());
        } catch (JDOMException | IOException ae) {
            ae.printStackTrace();
            return null;
        }
    }


    public static Parameters getPresetParameters(String name) {
        return ParameterPresets.getByName(name);
    }

    public static Parameters getParameters(String name) {
        File file = new File(name);
        Parameters parameters = null;
        if (!file.exists())
            parameters = ParameterPresets.getByName(name);
        else
            parameters = loadParameters(file.getAbsolutePath());
        return parameters;
    }
}
