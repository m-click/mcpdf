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

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.FdfReader;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.XfdfReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.util.Arrays;

public class Main
{
    public static void main(String[] args)
    {
        try {
        	if (inArgs(args, "stamp")) {
        		stamp(parseArgs(args));
        	} else if (inArgs(args, "background")) {
        		background(parseArgs(args));
        	} else {
        		execute(parseArgs(args));
        	}
        } catch (Exception e) {
            System.err.println(e);
            System.err.println("See README for more information.");
            System.exit(1);
        }
    }

    public static Config parseArgs(String[] args)
            throws FileNotFoundException
    {
        if (args.length == 0) {
            throw new RuntimeException("Missing arguments.");
        }
        Config config = new Config();
        config.pdfInputStream = new FileInputStream(args[0]);
        config.pdfOutputStream = System.out;
        config.formInputStream = null;
        config.pdf_sig = "";
        config.flatten = false;
        config.isFdf = false;
        byte[] fdfHeader = "%FDF".getBytes();
        for (int i = 1; i < args.length; i++) {
        	if ("stamp".equals(args[i]) || 
        			"background".equals(args[i])) {
        		i++;
        		config.pdf_sig = args[i];
        	} else if ("fill_form".equals(args[i])) {
                config.formInputStream = new BufferedInputStream(System.in);
                config.formInputStream.mark(4);
                try {
                    byte[] header = new byte[4];
                    config.formInputStream.read(header, 0, 4);
                    if (Arrays.equals(header, fdfHeader)) {
                        config.isFdf = true;
                    }
                    config.formInputStream.reset();
                } catch (Exception e) {
                    System.err.println(e);
                    System.err.println("Problem reading standard input.");
                }
                i++;
                if (!"-".equals(args[i])) {
                    throw new RuntimeException("Missing \"-\" after fill_form operation.");
                }
            } else if ("output".equals(args[i])) {
                i++;
                if (!"-".equals(args[i])) {
                    throw new RuntimeException("Missing \"-\" after output operation.");
                }
            }  else if ("flatten".equals(args[i])) {
                config.flatten = true;
            } 
            else {
                throw new RuntimeException("Unknown operation: " + args[i]);
            }
        }
        return config;
    }

    public static void execute(Config config)
            throws IOException, DocumentException
    {
        PdfReader reader = new PdfReader(config.pdfInputStream);
        PdfStamper stamper = new PdfStamper(reader, config.pdfOutputStream, '\0');
        if (config.formInputStream != null) {
            if (config.isFdf) {
                stamper.getAcroFields().setFields(new FdfReader(config.formInputStream));
            } else {
                stamper.getAcroFields().setFields(new XfdfReader(config.formInputStream));
            }
            
        }
        stamper.setFormFlattening(config.flatten);
        stamper.close();
    }
    
    public static void stamp(Config config) 
    	throws IOException, DocumentException {
    		PdfReader reader = new PdfReader(config.pdfInputStream);
    		PdfStamper stamper = new PdfStamper(reader, config.pdfOutputStream, '\0');
    		int num_pages = reader.getNumberOfPages();
    		PdfReader r = new PdfReader(config.pdf_sig);
    		PdfImportedPage page = stamper.getImportedPage(r, 1);
    		for (int i = 1; i <= num_pages; i++) {
        		PdfContentByte canvas = stamper.getOverContent(i);
        		canvas.addTemplate(page, 0, 0);
    		}
    		stamper.getWriter().freeReader(r);
    		r.close();
    		stamper.close();
    	}
    
    public static void background(Config config) 
        	throws IOException, DocumentException {
        		PdfReader reader = new PdfReader(config.pdfInputStream);
        		PdfStamper stamper = new PdfStamper(reader, config.pdfOutputStream, '\0');
        		int num_pages = reader.getNumberOfPages();
        		PdfReader r = new PdfReader(config.pdf_sig);
        		PdfImportedPage page = stamper.getImportedPage(r, 1);
        		for (int i = 1; i <= num_pages; i++) {
            		PdfContentByte canvas = stamper.getUnderContent(i);
            		canvas.addTemplate(page, 0, 0);
        		}
        		stamper.getWriter().freeReader(r);
        		r.close();
        		stamper.close();
        	}
        
    public static boolean inArgs(String[] args, String arg) {
    	for (int i = 1;i < args.length; i++) {
    		if (arg.equals(args[i])) {
    			return true;
   			}
   		}
   		return false;
   	}
}

