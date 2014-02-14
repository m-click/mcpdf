/**
 * Mcpdf is a drop-in replacement for PDFtk.
 *
 * It fixes PDFtk's unicode issues when filling in PDF forms,
 * and is essentially a command line interface for the iText
 * PDF library with a PDFtk compatible syntax.
 */

/*
 * Copyright (C) 2014  Volker Grabsch <grabsch@m-click.aero>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * https://www.gnu.org/licenses/agpl-3.0.html
 */

package aero.m_click.mcpdf;

import java.io.*;
import java.util.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class Main
{
    public static void main(String[] args)
        throws IOException, DocumentException
    {
        if (args.length == 0) {
            throw new RuntimeException("Missing arguments.");
        }
        InputStream input_pdf = new FileInputStream(args[0]);
        OutputStream output_pdf = System.out;
        InputStream fill_form = null;
        boolean flatten = false;
        for (int i = 1; i < args.length; i++) {
            if ("fill_form".equals(args[i])) {
                fill_form = System.in;
                i++;
                if (!"-".equals(args[i])) {
                    throw new RuntimeException("Missing \"-\" after fill_form operation.");
                }
            } else if ("output".equals(args[i])) {
                i++;
                if (!"-".equals(args[i])) {
                    throw new RuntimeException("Missing \"-\" after output operation.");
                }
            } else if ("flatten".equals(args[i])) {
                flatten = true;
            } else {
                throw new RuntimeException("Unknown operation: " + args[i]);
            }
        }
        execute(input_pdf, output_pdf, fill_form, flatten);
    }

    public static void execute(InputStream input_pdf,
                               OutputStream output_pdf,
                               InputStream fill_form,
                               boolean flatten)
        throws IOException, DocumentException
    {
        PdfReader reader = new PdfReader(input_pdf);
        PdfStamper stamper = new PdfStamper(reader, output_pdf, '\0');
        if (fill_form != null) {
            stamper.getAcroFields().setFields(new XfdfReader(fill_form));
        }
        stamper.setFormFlattening(flatten);
        stamper.close();
    }
}
